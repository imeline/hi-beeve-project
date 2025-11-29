package beeve.biz.member.dto.response

import beeve.biz.member.entity.Member
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
헤더 영역에서 사용하는 회원 기본 프로필 응답
- 이름, 프로필 이미지만 제공합니다.
"""
)
data class MemberHeaderProfileResponse(

    @field:Schema(description = "이름", nullable = true)
    val name: String? = null,

    @field:Schema(description = "프로필 이미지 URL", nullable = true)
    val profileUrl: String? = null,
) {
    companion object {
        fun from(member: Member): MemberHeaderProfileResponse =
            MemberHeaderProfileResponse(
                name = member.name,
                profileUrl = member.profileUrl
            )
    }
}
