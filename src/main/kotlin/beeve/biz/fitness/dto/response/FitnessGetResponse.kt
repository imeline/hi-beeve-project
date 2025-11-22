//package beeve.biz.fitness.dto.response
//
//import beeve.biz.fitness.dto.internal.TypePerRankResult
//import beeve.biz.fitness.entity.Fitness
//import beeve.biz.fitness.enum.MeasurePlace
//import io.swagger.v3.oas.annotations.media.Schema
//import java.math.BigDecimal
//
//@Schema(description = "체력 측정 조회 응답")
//data class FitnessGetResponse(
//
//    @field:Schema(description = "전체 등급 (1~4)")
//    val grade: Int,
//
//    @field:Schema(description = "같은 나이대 내 종합 순위")
//    val rank: Int,
//
//    @field:Schema(description = "같은 나이대에서 비교한 전체 인원 수")
//    val total: Int,
//
//    @field:Schema(description = "측정 장소", nullable = true)
//    val measurePlace: MeasurePlace? = null,
//
//    @field:Schema(description = "키(cm)")
//    val height: BigDecimal,
//
//    @field:Schema(description = "몸무게(kg)")
//    val weight: BigDecimal,
//
//    @field:Schema(description = "실제 나이")
//    val age: Int,
//
//    @field:Schema(description = "체력별 상세 정보")
//    val fitness: List<FitnessItemResponse>,
//) {
//    companion object {
//        fun from(
//            fitness: Fitness,
//            typeResults: List<TypePerRankResult>,
//            finalRank: Int,
//            total: Int,
//            grade: Int,
//            age: Int,
//        ): FitnessGetResponse = FitnessGetResponse(
//            grade = grade,
//            rank = finalRank,
//            total = total,
//            measurePlace = fitness.measurePlace,
//            height = fitness.height.bigDecimalValue(),
//            weight = fitness.weight.bigDecimalValue(),
//            age = age,
//            fitness = typeResults.map(FitnessItemResponse::from),
//        )
//    }
//}