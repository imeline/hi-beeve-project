package beeve.biz.fitness.dto.request

import beeve.biz.member.enum.Gender
import java.math.BigDecimal
import java.time.LocalDate

data class PhysicalMeasureResponse(
    val id: String,
    val memberId: Long?,
    val ageRange: Int,
    val measureDay: LocalDate,
    val gender: Gender?,
    val height: BigDecimal?,
    val weight: BigDecimal?,
    val bmi: BigDecimal?,
    val strengthLevel: Int?,
    val curlUpReps: Int?,
    val stepTestRecoveryBpm: Int?,
    val stepTestVo2max: BigDecimal?,
    val sitAndReach: Int?,
    val standingLongJump: BigDecimal?,
    val sideStepReps: Int?
)