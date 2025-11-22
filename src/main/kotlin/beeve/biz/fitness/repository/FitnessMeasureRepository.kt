package beeve.biz.fitness.repository

import beeve.biz.fitness.entity.FitnessMeasure
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDate

interface FitnessMeasureRepository : MongoRepository<FitnessMeasure, ObjectId> {

    // 하루 1회 제한 체크용
    fun existsByMemberIdAndMeasureDayAndDeletedYn(
        memberId: Long,
        measureDay: LocalDate,
        deletedYn: String = "N",
    ): Boolean
}