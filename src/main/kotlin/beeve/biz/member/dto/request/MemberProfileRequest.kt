package beeve.biz.member.dto.request

import beeve.biz.member.enum.Gender
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "프로필 등록/수정 요청")
data class MemberProfileRequest(

    @field:Schema(description = "이름", nullable = true)
    @field:Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    val name: String? = null,

    @field:Schema(description = "생년월일(yyyy-MM-dd)", nullable = true)
    @field:Past(message = "생년월일은 과거 날짜여야 합니다.")
    val birthDate: LocalDate? = null,

    @field:Schema(description = "성별(F/M)", nullable = true)
    val gender: Gender? = null,

    @field:Schema(description = "키(cm), 소수점 둘째자리까지", nullable = true)
    @field:Digits(integer = 3, fraction = 2, message = "키는 정수 3자리, 소수 2자리까지 허용됩니다.")
    val height: BigDecimal? = null,

    @field:Schema(description = "몸무게(kg), 소수점 둘째자리까지", nullable = true)
    @field:Digits(integer = 3, fraction = 2, message = "몸무게는 정수 3자리, 소수 2자리까지 허용됩니다.")
    val weight: BigDecimal? = null,

    @field:Schema(description = "프로필 이미지 URL", nullable = true)
    @field:Size(max = 255, message = "프로필 URL은 255자 이하여야 합니다.")
    val profileUrl: String? = null,
)