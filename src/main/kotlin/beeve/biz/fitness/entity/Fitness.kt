package beeve.biz.fitness.entity

import beeve.biz.fitness.dto.request.FitnessCreateRequest
import beeve.biz.fitness.enum.MeasurePlace
import beeve.biz.member.enum.Gender
import org.bson.types.Decimal128
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.LocalDate

@Document(collection = "fitness")
data class Fitness(
    @Id
    val id: ObjectId? = null,
    val memberId: Long,
    val ageRange: Int,
    val measurePlace: MeasurePlace? = null,
    val measureDay: LocalDate,
    val gender: Gender,
    val height: Decimal128,
    val weight: Decimal128,
    val bmi: Decimal128,
    val strengthWeightedAmount: Decimal128,
    val strengthLevel: Int,
    val pushUpReps: Int,
    val curlUpReps: Int,
    val stepTestRecoveryBpm: Int,
    val stepTestVo2max: Decimal128,
    val sitAndReach: Int,
    val standingLongJump: Decimal128,
    val sideStepReps: Int,
    // 기본 값이 있어야 자동으로 들어감
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
    val deletedYn: String = "N",
) {
    companion object {
        fun of(
            memberId: Long,
            ageRange: Int,
            measureDay: LocalDate,
            request: FitnessCreateRequest,
            gender: Gender,
            height: Decimal128,
            weight: Decimal128,
            bmi: Decimal128,
            strengthWeightedAmount: Decimal128,
            stepTestVo2max: Decimal128,
        ): Fitness {
            val m = request.measure

            return Fitness(
                memberId = memberId,
                ageRange = ageRange,
                measurePlace = m.measurePlace,
                measureDay = measureDay,
                gender = gender,
                height = height,
                weight = weight,
                bmi = bmi,
                strengthWeightedAmount = strengthWeightedAmount,
                strengthLevel = m.strengthLevel,
                pushUpReps = m.pushUpReps,
                curlUpReps = m.curlUpReps,
                stepTestRecoveryBpm = m.stepTestRecoveryBpm,
                stepTestVo2max = stepTestVo2max,
                sitAndReach = m.sitAndReach,
                standingLongJump = Decimal128(m.standingLongJump),
                sideStepReps = m.sideStepReps,
            )
        }
    }
}
