package beeve.biz.auth.dto.response

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
                "소셜 로그인에서 제공되지 않았거나, 사용자가 이름을 등록하지 않은 경우 null일 수 있습니다.",
    )
    val name: String? = null
)