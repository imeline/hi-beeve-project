package beeve.biz.auth.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "액세스 토큰 재발급/로그아웃 요청")
data class RefreshTokenRequest(
    @field:Schema(
        description = "Bearer 접두사가 포함된 리프레시 토큰"
    )
    @field:NotBlank
    val refreshToken: String
)