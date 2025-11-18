package beeve.biz.member.service

import beeve.biz.member.dto.request.MemberProfileRequest
import beeve.biz.member.dto.request.MemberWithdrawRequest
import beeve.biz.member.dto.response.MemberProfileResponse
import beeve.biz.member.entity.Member

interface MemberService {

    fun getActiveMemberById(memberId: Long): Member

    fun createAndUpdateProfile(memberId: Long, req: MemberProfileRequest)

    fun getProfile(memberId: Long): MemberProfileResponse

    fun withdraw(memberId: Long, req: MemberWithdrawRequest)
}