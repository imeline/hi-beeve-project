package beeve.biz.member.service

import beeve.biz.auth.dto.request.SignupRequest
import beeve.biz.member.dto.request.MemberProfileRequest
import beeve.biz.member.dto.request.MemberWithdrawRequest
import beeve.biz.member.dto.response.MemberHeaderProfileResponse
import beeve.biz.member.dto.response.MemberProfileResponse
import beeve.biz.member.entity.Member

interface MemberService {

    fun createMember(req: SignupRequest): Member

    fun getActiveMemberById(memberId: Long): Member

    fun createAndUpdateProfile(memberId: Long, req: MemberProfileRequest)
            : MemberHeaderProfileResponse

    fun getProfile(memberId: Long): MemberProfileResponse

    fun withdraw(memberId: Long, req: MemberWithdrawRequest)
}