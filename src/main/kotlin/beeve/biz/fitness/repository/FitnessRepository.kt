//package beeve.biz.fitness.repository
//
//import beeve.biz.fitness.entity.Fitness
//import org.bson.types.ObjectId
//import org.springframework.data.mongodb.repository.MongoRepository
//import java.time.LocalDate
//
//interface FitnessRepository : MongoRepository<Fitness, ObjectId> {
//
//    fun existsByMemberIdAndMeasureDay(
//        memberId: Long,
//        measureDay: LocalDate
//    ): Boolean
//
//    fun findAllByMemberIdAndDeletedYnOrderByMeasureDayDesc(
//        memberId: Long,
//        deletedYn: String = "N",
//    ): List<Fitness>
//
//    fun findByMemberIdAndMeasureDay(
//        memberId: Long,
//        measureDay: LocalDate,
//    ): Fitness?
//
//    fun findAllByAgeRangeAndDeletedYn(
//        ageRange: Int,
//        deletedYn: String = "N",
//    ): List<Fitness>
//}