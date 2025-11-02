package beeve.biz.member.service

import beeve.biz.fitness.dto.request.FitnessProfileRequest
import beeve.biz.member.dto.request.MemberProfileRequest
import beeve.biz.member.dto.response.MemberProfileResponse
import beeve.biz.member.entity.Member

interface MemberService {

    fun getById(memberId: Long): Member

    fun mergeProfileFromFitness(
        memberId: Long,
        req: FitnessProfileRequest?
    ): Member

    fun createAndUpdateProfile(memberId: Long, req: MemberProfileRequest)

    fun getProfile(memberId: Long): MemberProfileResponse
}