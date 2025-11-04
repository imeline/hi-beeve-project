package beeve.biz.fitness.dto.request

import beeve.biz.fitness.enum.MeasurePlace
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.math.BigDecimal

@Schema(description = "체력 측정 생성 요청")
data class FitnessCreateRequest(

    @field:Schema(
        description = "측정 장소 (HOME, OUTDOOR, GYM). 없으면 null 로 요청",
        nullable = true,
        allowableValues = ["HOME", "OUTDOOR", "GYM"]
    )
    val measurePlace: MeasurePlace? = null,

    @field:Schema(description = "근력 운동 레벨 (1: 쉬움 ~ 3: 어려움)")
    @field:Min(1)
    @field:Max(3)
    val strengthLevel: Int,

    @field:Schema(description = "팔굽혀펴기 횟수")
    val pushUpReps: Int,

    @field:Schema(description = "윗몸말아올리기 횟수")
    val curlUpReps: Int,

    @field:Schema(description = "스텝검사 회복시 심박수(bpm)")
    val stepTestRecoveryBpm: Int,

    @field:Schema(description = "앉아윗몸앞으로굽히기 횟수")
    val sitAndReach: Int,

    @field:Schema(description = "제자리 멀리뛰기(cm)")
    val standingLongJump: BigDecimal,

    @field:Schema(description = "반복옆뛰기 횟수")
    val sideStepReps: Int,
)