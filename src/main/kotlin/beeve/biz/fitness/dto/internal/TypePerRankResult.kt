package beeve.biz.fitness.dto.internal

import beeve.biz.fitness.enum.FitnessType
import java.math.BigDecimal

data class TypePerRankResult(
    val fitnessType: FitnessType,
    val strengthLevel: Int?,
    val value: BigDecimal,
    val rank: Int,
    val total: Int,
    val graphPosition: Double,
)