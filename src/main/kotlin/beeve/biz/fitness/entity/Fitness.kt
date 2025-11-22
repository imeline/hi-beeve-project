//package beeve.biz.fitness.entity
//
//import beeve.biz.fitness.dto.request.FitnessMeasureRequest
//import beeve.biz.fitness.enum.MeasurePlace
//import beeve.biz.member.enum.Gender
//import org.bson.types.Decimal128
//import org.bson.types.ObjectId
//import org.springframework.data.annotation.CreatedDate
//import org.springframework.data.annotation.Id
//import org.springframework.data.annotation.LastModifiedDate
//import org.springframework.data.mongodb.core.mapping.Document
//import java.time.Instant
//import java.time.LocalDate
//
//@Document(collection = "fitness")
//// 컬럼별 null 가능 여부는 국민체력100 정재데이터의 null 여부도 반영
//data class Fitness(
//    @Id
//    val id: ObjectId? = null,
//    val memberId: Long? = null,
//    val ageRange: Int,
//    val age: Int? = null,
//    val measurePlace: MeasurePlace? = null,
//    val measureDay: LocalDate,
//    val gender: Gender,
//    val height: Decimal128,
//    val weight: Decimal128,
//    val bmi: Decimal128,
//    val strengthWeightedAmount: Decimal128? = null,
//    val strengthLevel: Int? = null,
//    val pushUpReps: Int? = null,
//    val curlUpReps: Int? = null,
//    val stepTestRecoveryBpm: Int? = null,
//    val stepTestVo2max: Decimal128? = null,
//    val sitAndReach: Int? = null,
//    val standingLongJump: Decimal128? = null,
//    val sideStepReps: Int? = null,
//    // null 기본 값이 있어야 자동으로 들어감
//    @CreatedDate
//    val createdAt: Instant? = null,
//    @LastModifiedDate
//    val updatedAt: Instant? = null,
//    val deletedYn: String = "N",
//) {
//    companion object {
//        fun createFitness(
//            memberId: Long,
//            ageRange: Int,
//            age: Int?,
//            measureDay: LocalDate,
//            req: FitnessMeasureRequest,
//            gender: Gender,
//            height: Decimal128,
//            weight: Decimal128,
//            bmi: Decimal128,
//            strengthWeightedAmount: Decimal128,
//            stepTestVo2max: Decimal128,
//        ): Fitness {
//            return Fitness(
//                memberId = memberId,
//                ageRange = ageRange,
//                age = age,
//                measurePlace = req.measurePlace,
//                measureDay = measureDay,
//                gender = gender,
//                height = height,
//                weight = weight,
//                bmi = bmi,
//                strengthWeightedAmount = strengthWeightedAmount,
//                strengthLevel = req.strengthLevel,
//                pushUpReps = req.pushUpReps,
//                curlUpReps = req.curlUpReps,
//                stepTestRecoveryBpm = req.stepTestRecoveryBpm,
//                stepTestVo2max = stepTestVo2max,
//                sitAndReach = req.sitAndReach,
//                standingLongJump = Decimal128(req.standingLongJump),
//                sideStepReps = req.sideStepReps,
//            )
//        }
//    }
//}
