package beeve.biz.fitness.dto.request

import beeve.biz.fitness.enum.MeasurePlace
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "체력 측정 생성 요청")
data class FitnessMeasureRequest(

    @field:Schema(
        description = "측정 장소 (HOME, OUTDOOR, GYM). 없으면 null 로 요청",
        nullable = true,
        allowableValues = ["HOME", "OUTDOOR", "GYM"]
    )
    val measurePlace: MeasurePlace? = null,

    // === 근력(Strength) – 세 가지 중 하나만 전송 ===
    @field:Schema(
        description = "벽 팔굽혀펴기 횟수(회). " +
                "근력 측정 시 wallPushUpReps, kneePushUpReps, standardPushUpReps 중 하나만 전송",
        nullable = true
    )
    val wallPushUpReps: Int? = null,

    @field:Schema(
        description = "무릎 팔굽혀펴기 횟수(회). " +
                "근력 측정 시 wallPushUpReps, kneePushUpReps, standardPushUpReps 중 하나만 전송",
        nullable = true
    )
    val kneePushUpReps: Int? = null,

    @field:Schema(
        description = "표준 팔굽혀펴기 횟수(회). " +
                "근력 측정 시 wallPushUpReps, kneePushUpReps, standardPushUpReps 중 하나만 전송",
        nullable = true
    )
    val standardPushUpReps: Int? = null,

    // === 심폐지구력(Cardio – 스텝검사) ===
    @field:Schema(
        description = "스텝검사 회복시 심박수(bpm). " +
                "VO2max 계산에 사용되는 회복기 심박수"
    )
    val stepTestRecoveryBpm: Int,

    // === 근지구력(Endurance) ===
    @field:Schema(description = "교차윗몸일으키기 횟수(회)")
    val crossCrunchReps: Int,

    // === 유연성(Flexibility) ===
    @field:Schema(description = "앉아윗몸앞으로굽히기 거리(cm), 소수점 입력 가능")
    val sitAndReach: BigDecimal,

    // === 민첩성(Agility) ===
    @field:Schema(description = "반응시간(초), 소수점 입력 가능")
    val reactionTime: BigDecimal,

    // === 순발력(Quickness) ===
    @field:Schema(description = "체공시간(초), 소수점 입력 가능")
    val flightTime: BigDecimal,
)