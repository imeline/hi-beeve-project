package beeve.biz.auth.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "액세스 토큰 응답 바디")
data class AccessTokenResponse(
    @field:Schema(
        description = "Bearer 접두사가 포함된 액세스 토큰"
    )
    val accessToken: String
)