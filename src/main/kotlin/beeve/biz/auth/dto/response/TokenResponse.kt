package beeve.biz.auth.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    name = "TokenResponse",
    description = "발급된 토큰 응답 바디. 로그인 시 accessToken/refreshToken, 재발급 시 accessToken만 포함."
)
data class TokenResponse(
    @field:Schema(
        description = "Bearer 접두사가 포함된 액세스 토큰"
    )
    val accessToken: String,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:Schema(
        description = "Bearer 접두사가 포함된 리프레시 토큰. 사용하지 않는 응답(예: 재발급)에서는 누락됩니다.",
        nullable = true
    )
    val refreshToken: String? = null
)