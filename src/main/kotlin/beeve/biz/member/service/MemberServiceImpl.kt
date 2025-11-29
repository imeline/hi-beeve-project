package beeve.biz.member.service

import beeve.biz.auth.dto.request.SignupRequest
import beeve.biz.auth.repository.RefreshTokenRepository
import beeve.biz.auth.repository.SocialAuthRepository
import beeve.biz.member.dto.request.MemberProfileRequest
import beeve.biz.member.dto.request.MemberWithdrawRequest
import beeve.biz.member.dto.response.MemberHeaderProfileResponse
import beeve.biz.member.dto.response.MemberProfileResponse
import beeve.biz.member.entity.Member
import beeve.biz.member.repository.MemberRepository
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val socialAuthRepository: SocialAuthRepository
) : MemberService {

    @Transactional
    override fun createMember(req: SignupRequest): Member {
        val member = Member.create(req)
        return memberRepository.save(member)
    }

    @Transactional(readOnly = true)
    override fun getActiveMemberById(memberId: Long): Member {
        return memberRepository.findByMemberIdAndDeletedYn(memberId)
            .orElseThrow { GlobalException(ErrorStatus.MEMBER_NOT_FOUND) }
    }

    @Transactional
    override fun createAndUpdateProfile(memberId: Long, req: MemberProfileRequest)
            : MemberHeaderProfileResponse {
        val member = getActiveMemberById(memberId)
        member.createAndUpdateProfile(req)
        val savedMember = memberRepository.save(member)
        return MemberHeaderProfileResponse.from(savedMember)
    }

    @Transactional(readOnly = true)
    override fun getProfile(memberId: Long): MemberProfileResponse {
        val member = getActiveMemberById(memberId)
        return MemberProfileResponse.from(member)
    }

    @Transactional
    override fun withdraw(memberId: Long, req: MemberWithdrawRequest) {
        val member = getActiveMemberById(memberId)

        member.withdraw(req.withdrawReason)
        memberRepository.save(member)

        // 연관된 리프레시 토큰 삭제
        refreshTokenRepository.deleteByMemberId(memberId)
        // 소셜 연동 soft delete 벌크 처리
        socialAuthRepository.softDeleteAllByMemberId(memberId)
    }
}