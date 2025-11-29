package beeve.biz.auth.service

import beeve.biz.auth.dto.request.RefreshTokenRequest
import beeve.biz.auth.dto.request.SignupRequest
import beeve.biz.auth.dto.request.SocialLoginRequest
import beeve.biz.auth.dto.response.AccessTokenResponse
import beeve.biz.auth.dto.response.LoginResponse
import beeve.biz.auth.entity.RefreshToken
import beeve.biz.auth.entity.SocialAuth
import beeve.biz.auth.repository.RefreshTokenRepository
import beeve.biz.auth.repository.SocialAuthRepository
import beeve.biz.auth.security.JwtTokenProvider
import beeve.biz.auth.security.UserPrincipal
import beeve.biz.member.entity.Member
import beeve.biz.member.service.MemberService
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val socialAuthRepository: SocialAuthRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberService: MemberService
) : AuthService {

    @Transactional
    override fun signup(req: SignupRequest): LoginResponse {
        // 기존 가입 소셜 정보인지 확인
        // 탈퇴 회원 재가입 불가
        if (socialAuthRepository.existsByProviderAndProviderUserId(req.provider, req.providerUserId)) {
            throw GlobalException(ErrorStatus.SOCIAL_AUTH_ALREADY_EXISTS)
        }

        // Member 생성
        val savedMember = memberService.createMember(req)

        // SocialAuth 생성
        val social = SocialAuth.create(
            memberId = savedMember.memberId!!,
            provider = req.provider,
            providerUserId = req.providerUserId
        )
        socialAuthRepository.save(social)

        // 회원가입 후 바로 토큰 발급 + LoginResponse 리턴
        return issueTokensAndBuildLoginResponse(savedMember)
    }

    @Transactional
    override fun socialLogin(req: SocialLoginRequest): LoginResponse {
        // 소셜 인증 정보 조회
        val social = socialAuthRepository
            .findByProviderAndProviderUserId(req.provider, req.providerUserId)
            .orElseThrow { GlobalException(ErrorStatus.SOCIAL_AUTH_NOT_FOUND) }

        // social.memberId 에 해당하는 member 이 있고, 활성 상태인지 확인
        val member = memberService.getActiveMemberById(social.memberId)

        // 토큰 발급 + LoginResponse 리턴
        return issueTokensAndBuildLoginResponse(member)
    }

    @Transactional(readOnly = true)
    override fun refresh(req: RefreshTokenRequest): AccessTokenResponse {
        val reqRefresh = req.refreshToken
        val memberId = try {
            // 리프레시 토큰에서 memberId 추출
            jwtTokenProvider.extractMemberId(reqRefresh)
        } catch (e: ExpiredJwtException) { // 만료된 리프레시 토큰 예외 처리
            // 만료된 토큰의 memberId로 DB 행 삭제
            val expiredMemberId = e.claims.subject?.toLongOrNull()
            if (expiredMemberId != null) {
                refreshTokenRepository.deleteByMemberId(expiredMemberId)
            }
            throw GlobalException(ErrorStatus.EXPIRED_REFRESH_TOKEN)
        } catch (e: Exception) { // 형식 이상 / 서명 오류 등 예외 처리
            throw GlobalException(ErrorStatus.INVALID_REFRESH_TOKEN)
        }

        // 리프레쉬 토큰 행 조회 및 존재 검증
        val refreshRow = validRefreshTokenRowByMemberId(memberId)
        // 리프레쉬 토큰 (DB 저장값 = 요청 값) 검증
        validRefreshTokenEquals(refreshRow, reqRefresh)

        val newAccess = jwtTokenProvider.generateAccessToken(UserPrincipal.of(memberId))
        return AccessTokenResponse(accessToken = newAccess)
    }

    @Transactional
    override fun logout(memberId: Long, req: RefreshTokenRequest) {
        val refreshRow = validRefreshTokenRowByMemberId(memberId)
        validRefreshTokenEquals(refreshRow, req.refreshToken)
        refreshTokenRepository.deleteByMemberId(memberId)
    }

    /** 액세스/리프레시 토큰 발급 + 리프레시 토큰 upsert + LoginResponse 생성 */
    private fun issueTokensAndBuildLoginResponse(member: Member): LoginResponse {
        val memberId = member.memberId
            ?: throw GlobalException(ErrorStatus.MEMBER_NOT_FOUND)

        val principal = UserPrincipal.of(memberId)

        // 토큰 발급
        val access = jwtTokenProvider.generateAccessToken(principal)
        val refresh = jwtTokenProvider.generateRefreshToken(principal)

        // DB엔 Bearer 제거한 Raw 토큰 저장
        val refreshRaw = jwtTokenProvider.stripBearer(refresh)

        upsertRefreshToken(memberId, refreshRaw)

        return LoginResponse.from(access, refresh, member)
    }

    /** 리프레시 기존 토큰 있으면 업데이트, 없으면 새로 저장 */
    private fun upsertRefreshToken(memberId: Long, refreshRaw: String) {
        refreshTokenRepository.findByMemberId(memberId)
            .ifPresentOrElse(
                { it.updateToken(refreshRaw) },
                { refreshTokenRepository.save(RefreshToken.createRefreshToken(memberId, refreshRaw)) }
            )
    }

    /** memberId로 RefreshToken 행 조회, 없으면 예외 */
    private fun validRefreshTokenRowByMemberId(memberId: Long): RefreshToken =
        refreshTokenRepository.findByMemberId(memberId)
            .orElseThrow { GlobalException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND) }

    /** 리프레쉬 토큰 DB 저장값과 요청 비교 */
    private fun validRefreshTokenEquals(refreshRow: RefreshToken, reqRefresh: String) {
        val dbRefreshRaw = refreshRow.token
        val reqRefreshRaw = jwtTokenProvider.stripBearer(reqRefresh)
        if (dbRefreshRaw.isNullOrBlank() || dbRefreshRaw != reqRefreshRaw) {
            throw GlobalException(ErrorStatus.INVALID_REFRESH_TOKEN)
        }
    }
}
