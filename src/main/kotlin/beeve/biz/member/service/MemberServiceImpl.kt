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
        req: FitnessProfileRequest?
    ): Member {
        val member = getById(memberId)
        // profile 이 없으면 그냥 멤버만 가져와서 리턴
        if (req == null) {
            return member
        }

        // profile 이 있으면 그걸로 덮어씀
        member.birthDate = req.birthDate
        member.gender = req.gender
        member.height = req.height
        member.weight = req.weight

        return memberRepository.save(member)
    }
}