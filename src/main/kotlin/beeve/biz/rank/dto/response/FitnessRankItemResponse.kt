package beeve.biz.rank.dto.response

import beeve.biz.fitness.enum.FitnessType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "체력 순위")
class FitnessRankItemResponse (

    @field:Schema(description = "체력타입")
    val type: FitnessType? = null,

    @field:Schema(description = "순위")
    val rank: Int
) {
}