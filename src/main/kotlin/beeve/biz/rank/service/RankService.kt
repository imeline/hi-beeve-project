package beeve.biz.rank.service

import beeve.biz.rank.dto.response.FitnessGradeHistoryResponse
import beeve.biz.rank.dto.response.FitnessGradeSelectResponse
import beeve.biz.rank.dto.response.FitnessRankSelectResponse

interface RankService {

    fun getGradeHistories(memberId:Long):FitnessGradeSelectResponse

    fun getRankHistories(memberId:Long):FitnessRankSelectResponse

}