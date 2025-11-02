package beeve.biz.member.service

import beeve.biz.fitness.dto.request.FitnessProfileRequest
import beeve.biz.member.entity.Member

interface MemberService {

    fun getById(memberId: Long): Member

    fun mergeProfileFromFitness(
        memberId: Long,
        req: FitnessProfileRequest?
    ): Member
}