package beeve.biz.auth.dto.response

import beeve.biz.member.entity.Member
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 응답")
data class LoginResponse(
    @field:Schema(
        description = "Bearer 접두사가 포함된 액세스 토큰"
    )
    val accessToken: String,

    @field:Schema(
        description = "Bearer 접두사가 포함된 리프레시 토큰."
    )
    val refreshToken: String,

    @field:Schema(
        description = "사용자 이름. " +
                "소셜 로그인에서 제공되지 않았거나, 사용자가 등록하지 않은 경우 null일 수 있습니다.",
        nullable = true
    )
    val name: String? = null,

    @field:Schema(
        description = "사용자 프로필 사진 URL. " +
                "소셜 로그인에서 제공되지 않았거나, 사용자가 등록하지 않은 경우 null일 수 있습니다.",
        nullable = true
    )
    val profileUrl: String? = null
) {

    companion object {

        fun from(accessToken: String, refreshToken: String, member: Member): LoginResponse =
            LoginResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                name = member.name,
                profileUrl = member.profileUrl
            )
    }
}