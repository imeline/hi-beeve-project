package beeve.biz.fitness.dto.response


import beeve.biz.fitness.entity.FitnessMeasure
import beeve.biz.fitness.enum.FitnessType
import beeve.biz.fitness.enum.MeasurePlace
import beeve.biz.member.enum.Gender
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "날짜별 체력 측정 결과 조회 응답")
data class FitnessMeasureResponse(

    @field:Schema(description = "종합 등급 (1~5)")
    val totalGrade: Int,

    @field:Schema(
        description = "종합 백분위 순위 (1~100). " +
                "각 체력요소별 개별 순위를 100분위로 환산 후 평균 내어 계산"
    )
    val totalRank: Int,

    @field:Schema(description = "측정일(yyyy-MM-dd)")
    val measureDay: LocalDate,

    @field:Schema(
        description = "측정 장소 (HOME, OUTDOOR, GYM). 저장되지 않은 경우 null",
        nullable = true,
        allowableValues = ["HOME", "OUTDOOR", "GYM"]
    )
    val measurePlace: MeasurePlace? = null,

    @field:Schema(description = "키(cm)")
    val height: BigDecimal,

    @field:Schema(description = "몸무게(kg)")
    val weight: BigDecimal,

    @field:Schema(description = "만 나이")
    val age: Int,

    @field:Schema(description = "성별(F/M)")
    val gender: Gender,

    @field:Schema(
        description = "각 체력 요소별 측정 결과 리스트. " +
                "STRENGTH, CARDIO, ENDURANCE, FLEXIBILITY, AGILITY, QUICKNESS 총 6개"
    )
    val fitness: List<FitnessItemResponse>,
) {

    companion object {
        fun from(
            measure: FitnessMeasure,
            totalRank: Int,
        ): FitnessMeasureResponse {
            val items = FitnessType.entries.mapNotNull { type ->
                val result = measure.fitnessResult[type] ?: return@mapNotNull null

                FitnessItemResponse(
                    fitnessType = type,
                    program = result.program,
                    value = result.value,
                    rawValue = result.rawValue,
                    grade = result.grade ?: 0, // 등급이 없는 경우 0으로 처리
                )
            }

            return FitnessMeasureResponse(
                totalGrade = measure.totalGrade ?: 0, // 등급이 없는 경우 0로 처리
                totalRank = totalRank,
                measureDay = measure.measureDay,
                measurePlace = measure.measurePlace,
                height = measure.height,
                weight = measure.weight,
                age = measure.age,
                gender = measure.gender,
                fitness = items,
            )
        }
    }
}
