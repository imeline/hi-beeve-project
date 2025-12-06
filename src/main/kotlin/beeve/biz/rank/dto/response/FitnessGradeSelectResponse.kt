package beeve.biz.rank.dto.response

import beeve.biz.fitness.dto.response.FitnessItemResponse
import beeve.biz.fitness.entity.FitnessMeasure
import beeve.biz.fitness.enum.FitnessType
import beeve.biz.fitness.enum.FitnessType.*
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "체력 등급 조회 응답")
data class FitnessGradeSelectResponse(

    @field:Schema(description = "종합 등급 이력")
    val totalGradeList: List<FitnessGradeHistoryResponse>,

    @field:Schema(description = "근력 등급 이력")
    val strengthGradeList: List<FitnessGradeHistoryResponse>,

    @field:Schema(description = "근지구력 등급 이력")
    val cardioGradeList: List<FitnessGradeHistoryResponse>,

    @field:Schema(description = "심폐지구력 등급 이력")
    val enduranceGradeList: List<FitnessGradeHistoryResponse>,

    @field:Schema(description = "유연성 등급 이력")
    val flexibilityGradeList: List<FitnessGradeHistoryResponse>,

    @field:Schema(description = "민첩성 등급 이력")
    val agilityGradeList: List<FitnessGradeHistoryResponse>,

    @field:Schema(description = "순발력 등급 이력")
    val quicknessGradeList: List<FitnessGradeHistoryResponse>,
) {

    companion object {
        fun from(measureList: List<FitnessMeasure>): FitnessGradeSelectResponse {

            // 종합 등급
            val totalGradeList = measureList.map { measure ->
                FitnessGradeHistoryResponse(
                    grade = measure.totalGrade ?: 0,
                    date = measure.createdAt.toString()
                )

//                var sum = 0
//                // 전체 등급은 각 체력 등급의 평균
//                FitnessType.entries.mapNotNull { type ->
//                    val result = measure.fitnessResult[type] ?: return@mapNotNull null
//                    sum += result.grade ?: 4
//                }
//                FitnessGradeHistoryResponse(
//                    date = measure.createdAt.toString(),
//                    grade = sum / 6
//                )
            }

            // 체력별 등급
            // 근력
            val strengthGradeList = measureList.map { measure ->
                val res = measure.fitnessResult[STRENGTH]
                FitnessGradeHistoryResponse(
                    date = measure.createdAt.toString(),
                    grade = res?.grade ?: 5
                )
            }
            // 심폐지구력
            val cardioGradeList = measureList.map { measure ->
                val res = measure.fitnessResult[CARDIO]
                FitnessGradeHistoryResponse(
                    date = measure.createdAt.toString(),
                    grade = res?.grade ?: 4
                )
            }
            // 근지구력
            val enduranceGradeList = measureList.map { measure ->
                val res = measure.fitnessResult[ENDURANCE]
                FitnessGradeHistoryResponse(
                    date = measure.createdAt.toString(),
                    grade = res?.grade ?: 4
                )
            }
            // 유연성
            val flexibilityGradeList = measureList.map { measure ->
                val res = measure.fitnessResult[FLEXIBILITY]
                FitnessGradeHistoryResponse(
                    date = measure.createdAt.toString(),
                    grade = res?.grade ?: 4
                )
            }
            // 민첩성
            val agilityGradeList = measureList.map { measure ->
                val res = measure.fitnessResult[AGILITY]
                FitnessGradeHistoryResponse(
                    date = measure.createdAt.toString(),
                    grade = res?.grade ?: 3
                )
            }
            // 순발력
            val quicknessGradeList = measureList.map { measure ->
                val res = measure.fitnessResult[QUICKNESS]
                FitnessGradeHistoryResponse(
                    date = measure.createdAt.toString(),
                    grade = res?.grade ?: 3
                )
            }
            return FitnessGradeSelectResponse(
                totalGradeList,
                strengthGradeList,
                cardioGradeList,
                enduranceGradeList,
                flexibilityGradeList,
                agilityGradeList,
                quicknessGradeList
            )
        }
    }


}