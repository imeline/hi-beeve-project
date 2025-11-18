package beeve.biz.auth.dto.request

import beeve.biz.auth.enum.Provider
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "소셜 로그인 요청 바디")
data class SocialLoginRequest(
    @field:Schema(
        description = "소셜 제공자",
        allowableValues = ["KAKAO", "GOOGLE"]
    )
    val provider: Provider,

    @field:Schema(
        description = "소셜 제공자 측 사용자 식별자"
    )
    @field:NotBlank
    val providerUserId: String
)