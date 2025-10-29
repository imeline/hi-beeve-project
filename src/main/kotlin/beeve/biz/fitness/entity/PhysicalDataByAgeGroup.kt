package beeve.biz.fitness.entity

import beeve.biz.fitness.enum.MeasurePlace
import beeve.biz.member.enum.Gender
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Document(collection = "physical_data_by_age_group")
data class PhysicalDataByAgeGroup(
    @Id
    val id: ObjectId? = null,
    val memberId: Long? = null,
    val ageRange: Int,
    val measurePlace: MeasurePlace? = null,
    val measureDay: LocalDate,
    val gender: Gender? = null,
    val height: BigDecimal? = null,
    val weight: BigDecimal? = null,
    val bmi: BigDecimal? = null,
    val strengthWeightedAmount: BigDecimal? = null,
    val strengthLevel: Int? = null,
    val curlUpReps: Int? = null,
    val stepTestRecoveryBpm: Int? = null,
    val stepTestVo2max: BigDecimal? = null,
    val sitAndReach: Int? = null,
    val standingLongJump: BigDecimal? = null,
    val sideStepReps: Int? = null,
    @CreatedDate
    val createdAt: Instant,
    @LastModifiedDate
    val updatedAt: Instant,
    val deletedYn: String = "N",
)