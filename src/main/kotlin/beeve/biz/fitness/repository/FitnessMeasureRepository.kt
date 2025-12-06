package beeve.biz.fitness.repository

import beeve.biz.fitness.entity.AgeRange
import beeve.biz.fitness.entity.FitnessMeasure
import beeve.biz.member.enum.Gender
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

    // 회원별 전체 조회 (최근순)
    fun findByMemberIdAndDeletedYnOrderByMeasureDayDesc(
        memberId: Long,
        deletedYn: String = "N",
    ): List<FitnessMeasure>

    // 날짜 + 회원별 한 건 조회
    fun findByMemberIdAndMeasureDayAndDeletedYn(
        memberId: Long,
        measureDay: LocalDate,
        deletedYn: String = "N",
    ): FitnessMeasure?

    // 회원별 가장 최근 측정 기록
    fun findTopByMemberIdAndDeletedYnOrderByMeasureDayDesc(
        memberId: Long,
        deletedYn: String = "N",
    ): FitnessMeasure?

    // 같은 성별 + 같은 연령대 조회 (totalRank 계산용)
    fun findByGenderAndAgeRangeAndDeletedYn(
        gender: Gender,
        ageRange: AgeRange,
        deletedYn: String = "N",
    ): List<FitnessMeasure>

    // 체력 측정 이력 최신등록순 5개 조회
    fun findTop5ByMemberIdOrderByMeasureDayDesc(
        memberId: Long,
        deletedYn: String = "N",
    ): List<FitnessMeasure>

}