package beeve.biz.fitness.dto.request

import beeve.biz.member.enum.Gender
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Past
import java.math.BigDecimal
import java.time.LocalDate

@Schema(
    description = "체력 측정 시 최초 1회만 보내는 회원 프로필 정보"
)
data class FitnessProfileRequest(

    @field:Schema(description = "생년월일")
    @field:Past(message = "생년월일은 오늘 이전 날짜여야 합니다.")
    val birthDate: LocalDate,

    @field:Schema(description = "성별(F/M)")
    val gender: Gender,

    @field:Schema(description = "신장(cm)")
    val height: BigDecimal,

    @field:Schema(description = "체중(kg)")
    val weight: BigDecimal,
)