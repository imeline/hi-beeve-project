package beeve.biz.auth.dto.request

import beeve.biz.auth.enum.Provider
import beeve.biz.member.enum.Gender
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "회원가입 요청")
data class SignupRequest(

    @field:Schema(
        description = "소셜 제공자",
        allowableValues = ["KAKAO", "GOOGLE"]
    )
    @field:NotNull
    val provider: Provider,

    @field:Schema(description = "소셜 제공자 측 사용자 식별자")
    @field:NotBlank
    val providerUserId: String,

    @field:Schema(description = "사용자 이름")
    @field:NotBlank
    val name: String,

    @field:Schema(
        description = "사용자 이메일",
        nullable = true
    )
    @field:Email
    val email: String? = null,

    @field:Schema(
        description = "프로필 이미지 URL",
        nullable = true
    )
    val profileUrl: String? = null,

    @field:Schema(
        description = "성별",
        allowableValues = ["F", "M"]
    )
    @field:NotNull
    val gender: Gender,

    @field:Schema(description = "생년월일")
    @field:NotNull
    val birthDate: LocalDate,

    @field:Schema(description = "키(cm). 소수점 둘째 자리까지 가능")
    @field:NotNull
    @field:PositiveOrZero
    val height: BigDecimal,

    @field:Schema(description = "몸무게(kg). 소수점 둘째 자리까지 가능")
    @field:NotNull
    @field:PositiveOrZero
    val weight: BigDecimal
)
