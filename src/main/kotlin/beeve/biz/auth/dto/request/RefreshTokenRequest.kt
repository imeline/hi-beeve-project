package beeve.biz.auth.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    name = "RefreshTokenRequest",
    description = "액세스 토큰 재발급/로그아웃 요청 바디"
)
data class RefreshTokenRequest(
    @field:Schema(
        description = "Bearer 접두사가 포함된 리프레시 토큰"
    )
    val refreshToken: String
)