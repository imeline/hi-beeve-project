package beeve.biz.rank.service

import beeve.biz.fitness.entity.FitnessMeasure
import beeve.biz.fitness.enum.FitnessType
import beeve.biz.fitness.repository.FitnessMeasureRepository
import beeve.biz.rank.dto.response.FitnessGradeSelectResponse
import beeve.biz.rank.dto.response.FitnessRankHistoryResponse
import beeve.biz.rank.dto.response.FitnessRankItemResponse
import beeve.biz.rank.dto.response.FitnessRankSelectResponse
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class RankServiceImpl (
    private val fitnessMeasureRepository: FitnessMeasureRepository
) : RankService {

    @Transactional
    override fun getGradeHistories(memberId: Long): FitnessGradeSelectResponse {
        val list = fitnessMeasureRepository.findTop5ByMemberIdOrderByMeasureDayDesc(memberId, deletedYn = "N")
        val response = FitnessGradeSelectResponse.from(list)

        return response
    }

    @Transactional
    override fun getRankHistories(memberId: Long): FitnessRankSelectResponse {

        val myMeasuredList = fitnessMeasureRepository.findTop5ByMemberIdOrderByMeasureDayDesc(memberId, deletedYn = "N")

        if (myMeasuredList.isEmpty()) {
            return FitnessRankSelectResponse.from(emptyList(), emptyList())
        }

        val myLatestMeasure = myMeasuredList[0];

        // 2) 같은 성별 + 같은 연령대의 모든 기록 조회 (날짜 제한 없음)
        val allMeasures = fitnessMeasureRepository
            .findByGenderAndAgeRangeAndDeletedYn(
                gender = myLatestMeasure.gender,
                ageRange = myLatestMeasure.ageRange,
                deletedYn = "N",
            )

        // 3) 회원별로 "가장 최근 측정" 1개씩만 남기기
//        val latestPerMember: List<FitnessMeasure> = allMeasures
//            .filter { it.memberId != null }              // 혹시 모를 null 방어
//            .groupBy { it.memberId!! }                   // memberId별로 묶고
//            .mapValues { (_, measures) ->
//                measures.maxByOrNull { it.measureDay }!! // measureDay 가장 큰 것 (최신)
//            }
//            .values
//            .toList()
//
//        // 4) 비교군에서 "나"는 제외
//        val compMeasures = latestPerMember
//            .filter { it.memberId != myLatestMeasure.memberId }

        // 현재 빅데이터에 회원ID가 없어서 비교군 추출 불가해 임시 처리
        val compMeasures = allMeasures
            .filter { it.memberId != myLatestMeasure.memberId }

        // 체력별 순위
        val fitnessRankList = calculateFitnessRank(myLatestMeasure, compMeasures)

        // 개인별 순위 이력
        val rankHistoryList = myMeasuredList.map { fitnessRankItem ->
            val rank = calculateTotalRank(fitnessRankItem, compMeasures)
            FitnessRankHistoryResponse(
                rank,
                date = fitnessRankItem.createdAt.toString()
            )
        }.toList()

        val response = FitnessRankSelectResponse.from(rankHistoryList, fitnessRankList)

        return response
    }

    fun calculateFitnessRank(
        myMeasure: FitnessMeasure,
        compMeasures: List<FitnessMeasure>,
    ): List<FitnessRankItemResponse> {

        // 같은 조건의 다른 사람이 아무도 없으면 1등으로 처리
//        if (compMeasures.isEmpty()) return

        val types = FitnessType.entries.toTypedArray()
        val ranksPerType: List<FitnessRankItemResponse> = types.map { type ->
            val myValue = myMeasure.fitnessResult[type]?.value
                ?: throw GlobalException(ErrorStatus.FITNESS_TYPE_INVALID)

            // 1) 비교 대상들의 value 리스트
            val values = compMeasures.mapNotNull { it.fitnessResult[type]?.value }
            if (values.isEmpty()) {
                // 비교 목록에 이 타입 값이 하나도 없다? 비정상
                throw GlobalException(ErrorStatus.FITNESS_COMPARISON_DATA_INVALID)
            }

            // (2) 각 FitnessType별 value 기준으로 정렬
            // 정렬 방향: AGILITY(반응시간)만 작을수록 좋으니 오름차순, 나머지는 내림차순
            val sorted = when (type) {
                FitnessType.AGILITY -> values.sorted()   // 작은 값이 앞
                else -> values.sortedDescending()        // 큰 값이 앞
            }

            // (3) 내 rankIndex 추출 (0-base)
            val rawIndex = when (type) {
                FitnessType.AGILITY -> {
                    // 반응시간(작을수록 좋음)
                    sorted.indexOfFirst { myValue <= it }
                }

                else -> {
                    // 나머지(클수록 좋음)
                    sorted.indexOfFirst { myValue >= it }
                }
            }
            val rankIndex = rawIndex.takeIf { it != -1 } ?: sorted.lastIndex

            // (4) 0-base 순위를 1~100 등수로 바꾸기
            val n = sorted.size // 비교 대상 수
            val scaledRank = (rankIndex * 100 / n) + 1 // 1~100 등수로 변환

            FitnessRankItemResponse(
                type,
                rank = scaledRank
            )
        }.toList()

        return ranksPerType
    }

    fun calculateTotalRank(
        myMeasure: FitnessMeasure,
        compMeasures: List<FitnessMeasure>,
    ): Int {
        // 같은 조건의 다른 사람이 아무도 없으면 1등으로 처리
        if (compMeasures.isEmpty()) return 1

        val types = FitnessType.entries.toTypedArray()

        val ranksPerType: List<Int> = types.map { type ->
            val myValue = myMeasure.fitnessResult[type]?.value
                ?: throw GlobalException(ErrorStatus.FITNESS_TYPE_INVALID)

            // 1) 비교 대상들의 value 리스트
            val values = compMeasures.mapNotNull { it.fitnessResult[type]?.value }
            if (values.isEmpty()) {
                // 비교 목록에 이 타입 값이 하나도 없다? 비정상
                throw GlobalException(ErrorStatus.FITNESS_COMPARISON_DATA_INVALID)
            }

            // (2) 각 FitnessType별 value 기준으로 정렬
            // 정렬 방향: AGILITY(반응시간)만 작을수록 좋으니 오름차순, 나머지는 내림차순
            val sorted = when (type) {
                FitnessType.AGILITY -> values.sorted()   // 작은 값이 앞
                else -> values.sortedDescending()        // 큰 값이 앞
            }

            // (3) 내 rankIndex 추출 (0-base)
            val rawIndex = when (type) {
                FitnessType.AGILITY -> {
                    // 반응시간(작을수록 좋음)
                    sorted.indexOfFirst { myValue <= it }
                }

                else -> {
                    // 나머지(클수록 좋음)
                    sorted.indexOfFirst { myValue >= it }
                }
            }
            val rankIndex = rawIndex.takeIf { it != -1 } ?: sorted.lastIndex

            // (4) 0-base 순위를 1~100 등수로 바꾸기
            val n = sorted.size // 비교 대상 수
            val scaledRank = (rankIndex * 100 / n) + 1 // 1~100 등수로 변환
            // Kotlin에서는 return 을 안 쓰면, 블록의 마지막 줄이 자동으로 반환값
            scaledRank
        }

        // (5) 6개 타입의 scaledRank 평균 계산
        val sum = ranksPerType.sum()
        return sum / ranksPerType.size   // Int 나눗셈이라 자동으로 버림
    }

}