package beeve.biz.rank.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "체력 순위 조회 응답")
data class FitnessRankSelectResponse(

    @field:Schema(description = "순위 이력")
    val rankHistoryList: List<FitnessRankHistoryResponse>,

    @field:Schema(description = "체력별 순위")
    val fitnessRankList: List<FitnessRankItemResponse>,
) {
    companion object {
        fun from(
            rankHistoryList: List<FitnessRankHistoryResponse>,
            fitnessRankList: List<FitnessRankItemResponse>
        ) :FitnessRankSelectResponse {
            return FitnessRankSelectResponse(rankHistoryList, fitnessRankList)
        }
    }
}