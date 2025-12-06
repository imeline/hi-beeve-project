package beeve.biz.rank.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "체력 순위 이력")
data class FitnessRankHistoryResponse (

    @field:Schema(description = "순위")
    val rank: Int?,

    @field:Schema(description = "측정일자")
    val date: String
) {
}