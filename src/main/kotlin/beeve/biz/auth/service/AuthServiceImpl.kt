package beeve.biz.auth.service

import beeve.biz.auth.dto.request.SocialLoginRequest
import beeve.biz.auth.dto.request.RefreshTokenRequest
import beeve.biz.auth.dto.response.TokenResponse
import beeve.biz.auth.entity.RefreshToken
import beeve.biz.auth.repository.RefreshTokenRepository
import beeve.biz.auth.repository.SocialAuthRepository
import beeve.biz.auth.security.JwtTokenProvider
import beeve.biz.auth.security.UserPrincipal
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val socialAuthRepository: SocialAuthRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) : AuthService {

    @Transactional
    override fun socialLogin(request: SocialLoginRequest): TokenResponse {
        // todo: 탈퇴 회원 검증 필요
        // 소셜 인증 정보 조회
        val social = socialAuthRepository
            .findByProviderAndProviderUserId(request.provider, request.providerUserId)
            .orElseThrow { GlobalException(ErrorStatus.SOCIAL_AUTH_NOT_FOUND) }

        val memberId = social.memberId
        val principal = UserPrincipal.of(memberId)

        // 토큰 발급
        val access = jwtTokenProvider.generateAccessToken(principal)
        val refresh = jwtTokenProvider.generateRefreshToken(principal)

        // DB엔 Bearer 제거한 Raw 토큰 저장
        val refreshRaw = jwtTokenProvider.stripBearer(refresh)
        // 기존 토큰 있으면 업데이트, 없으면 새로 저장
        refreshTokenRepository.findByMemberId(memberId)
            .ifPresentOrElse(
                { it.updateToken(refreshRaw) },
                { refreshTokenRepository.save(RefreshToken.of(memberId, refreshRaw)) }
            )

        return TokenResponse(accessToken = access, refreshToken = refresh)
    }

    @Transactional(readOnly = true)
    override fun refresh(request: RefreshTokenRequest): TokenResponse {
        val reqRefresh = request.refreshToken
        val memberId = jwtTokenProvider.extractMemberId(reqRefresh)

        val refreshRow = validRefreshTokenRowByMemberId(memberId)
        validRefreshTokenEquals(refreshRow, reqRefresh)

        // 리프레스 토큰 만료 검증
        validRefreshTokenNotExpired(reqRefresh)

        val newAccess = jwtTokenProvider.generateAccessToken(UserPrincipal.of(memberId))
        return TokenResponse(accessToken = newAccess)
    }

    @Transactional
    override fun logout(memberId: Long, request: RefreshTokenRequest) {
        val refreshRow = validRefreshTokenRowByMemberId(memberId)
        validRefreshTokenEquals(refreshRow, request.refreshToken)
        refreshTokenRepository.deleteByMemberId(memberId)
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

    /** 리프레쉬 토큰 만료 검증, 만료 시 DB에서 삭제 후 예외 */
    private fun validRefreshTokenNotExpired(reqRefresh: String) {
        if (jwtTokenProvider.validateToken(reqRefresh)) return
        val memberId = jwtTokenProvider.extractMemberId(reqRefresh)
        refreshTokenRepository.deleteByMemberId(memberId)
        throw GlobalException(ErrorStatus.EXPIRED_REFRESH_TOKEN)
    }
}
