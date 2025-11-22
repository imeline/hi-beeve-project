package beeve.biz.fitness.entity

import beeve.biz.fitness.enum.FitnessType
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

@Document("fitness_measure")
data class FitnessMeasure(
    @Id
    val id: ObjectId? = null,           // 체력측정 ID

    val memberId: Long? = null,         // 회원 ID

    val ageRange: AgeRange,             // 연령대 {min, max}

    val age: Int,                       // 만 나이

    val totalGrade: Int? = null,        // 종합 등급 (1~5)

    val measurePlace: MeasurePlace? = null, // 측정 장소 (HOME, OUTDOOR, GYM)

    val measureDay: LocalDate,          // 측정일

    val gender: Gender,                 // 성별 (M, F)

    val height: Decimal128,             // 신장

    val weight: Decimal128,             // 체중

    val bmi: Decimal128,                // 체질량률

    // { "STRENGTH": { grade, value, program, rawValue }, ... }
    val fitnessResult: Map<FitnessType, FitnessResult>, // 체력 측정 결과

    // null 기본 값이 있어야 자동으로 들어감
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
    val deletedYn: String = "N",
)