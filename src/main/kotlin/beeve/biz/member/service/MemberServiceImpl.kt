package beeve.biz.member.service

import beeve.biz.fitness.dto.request.FitnessProfileRequest
import beeve.biz.member.entity.Member
import beeve.biz.member.repository.MemberRepository
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberServiceImpl(
    private val memberRepository: MemberRepository
) : MemberService {

    @Transactional(readOnly = true)
    override fun getById(memberId: Long): Member {
        return memberRepository.findById(memberId)
            .orElseThrow { GlobalException(ErrorStatus.MEMBER_NOT_FOUND) }
    }

    @Transactional
    override fun mergeProfileFromFitness(
        memberId: Long,
        profile: FitnessProfileRequest?
    ): Member {
        val member = getById(memberId)
        // profile 이 없으면 그냥 멤버만 가져와서 리턴
        if (profile == null) {
            return member
        }

        // profile 이 있으면 그걸로 덮어씀
        member.birthDate = profile.birthDate
        member.gender = profile.gender
        member.height = profile.height
        member.weight = profile.weight

        return memberRepository.save(member)
    }

    override fun validateProfileIfPresent(member: Member): Member {
        if (member.birthDate == null ||
            member.gender == null ||
            member.height == null ||
            member.weight == null
        ) {
            throw GlobalException(ErrorStatus.MEMBER_PROFILE_NOT_FOUND)
        }
        return member
    }
}