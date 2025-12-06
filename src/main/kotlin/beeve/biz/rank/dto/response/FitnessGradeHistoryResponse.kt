package beeve.biz.rank.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "체력 등급 이력")
data class FitnessGradeHistoryResponse (

    @field:Schema(description = "등급")
    val grade: Int?,

    @field:Schema(description = "측정일자")
    val date: String
) {
}