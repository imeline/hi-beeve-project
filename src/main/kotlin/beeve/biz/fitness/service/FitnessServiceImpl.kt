package beeve.biz.fitness.service

import beeve.biz.fitness.dto.internal.TypePerRankResult
import beeve.biz.fitness.dto.request.FitnessCreateRequest
import beeve.biz.fitness.dto.response.FitnessGetResponse
import beeve.biz.fitness.dto.response.FitnessItemResponse
import beeve.biz.fitness.entity.Fitness
import beeve.biz.fitness.enum.FitnessType
import beeve.biz.fitness.repository.FitnessRepository
import beeve.biz.member.enum.Gender
import beeve.biz.member.service.MemberService
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import org.bson.types.Decimal128
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import kotlin.math.roundToInt

@Service
class FitnessServiceImpl(
    private val memberService: MemberService,
    private val fitnessRepository: FitnessRepository,
) : FitnessService {

    private val KST = ZoneId.of("Asia/Seoul")

    @Transactional
    override fun createFitness(memberId: Long, request: FitnessCreateRequest) {
        // 측정일은 "한국 시간" 기준 오늘
        val measureDay = LocalDate.now(KST)

        // 측정은 하루에 1번 가능
        if (fitnessRepository.existsByMemberIdAndMeasureDay(memberId, measureDay)) {
            throw GlobalException(ErrorStatus.FITNESS_TODAY_ALREADY_EXISTS)
        }

        // 1. profile 이 있으면 member 에 반영, 없으면 기존 member
        val member =
            memberService.mergeProfileFromFitness(memberId, request.profile)

        // 2. 필수 프로필 필드 보장 검증
        val validMember = memberService.validateProfileIfPresent(member)

        val birthDate = validMember.birthDate!!
        val gender = validMember.gender!!
        val height = validMember.height!!
        val weight = validMember.weight!!

        // 실제 나이(VO2max에 필요)
        val ageYears = Period.between(birthDate, measureDay).years
        // 10단위 나이대
        val ageRange = toAgeRange(ageYears)
        // BMI
        val bmi = calculateBmi(height, weight)
        // 근력가중치
        val strengthWeighted = calculateStrengthWeightedAmount(
            strengthLevel = request.measure.strengthLevel,
            pushUpReps = request.measure.pushUpReps
        )
        // 스텝테스트 VO2max
        val stepTestVo2max = calculateStepTestVo2max(
            gender = gender,
            ageYears = ageYears,
            height = height,
            weight = weight,
            recoveryBpm = request.measure.stepTestRecoveryBpm
        )

        val newFitness = Fitness.of(
            memberId = memberId,
            age = ageYears,
            ageRange = ageRange,
            measureDay = measureDay,
            request = request,
            gender = gender,
            height = Decimal128(height),
            weight = Decimal128(weight),
            bmi = Decimal128(bmi),
            strengthWeightedAmount = Decimal128(strengthWeighted),
            stepTestVo2max = Decimal128(stepTestVo2max),
        )

        fitnessRepository.save(newFitness)
    }

    @Transactional(readOnly = true)
    override fun getFitnessByDate(memberId: Long, measureDay: LocalDate): FitnessGetResponse {
        // 1) 내가 그 날짜에 측정한 데이터
        val myFitness = fitnessRepository.findByMemberIdAndMeasureDay(memberId, measureDay)
            ?: throw GlobalException(ErrorStatus.FITNESS_NOT_FOUND)

        val myAge = myFitness.age
            ?: throw GlobalException(ErrorStatus.MEMBER_PROFILE_NOT_FOUND)

        // 2) 같은 나이대 데이터 전체 조회
        val sameAgeRangeFitnessList = fitnessRepository.findAllByAgeRangeAndDeletedYn(
            ageRange = myFitness.ageRange,
        )
        if (sameAgeRangeFitnessList.isEmpty()) {
            throw GlobalException(ErrorStatus.FITNESS_NOT_FOUND)
        }

        // 3) 회원별 최신 1개만 남기기, 국민체력100 데이터는 전부 반영
        // (나 자신은 이번 measureDay 걸로 강제)
        val latestPerMember = latestPerMember(
            sameAgeRangeFitnessList = sameAgeRangeFitnessList,
            myFitness = myFitness,
        )
        // 총 순위 인원 수
        val total = latestPerMember.size

        // 4) enum 한 번만 돌면서 타입별 순위 계산
        val typePerResults = FitnessType.entries
            .mapNotNull { type -> // null이 아니면 리스트에 넣어라
                calcTypeRank(
                    me = myFitness,
                    all = latestPerMember,
                    type = type,
                )
            }

        if (typePerResults.isEmpty()) {
            throw GlobalException(ErrorStatus.FITNESS_NOT_FOUND)
        }

        // 5) 종합 순위 = 타입별 rank 평균
        val finalRank = typePerResults
            .map { it.rank }
            .average()
            .toInt()

        val grade = rankToGrade(finalRank, total)

        // 6) DTO 변환
        return FitnessGetResponse(
            grade = grade,
            rank = finalRank,
            total = total,
            measurePlace = myFitness.measurePlace,
            height = myFitness.height.bigDecimalValue(),
            weight = myFitness.weight.bigDecimalValue(),
            age = myAge,
            fitness = typePerResults.map { r ->
                FitnessItemResponse(
                    fitnessType = r.fitnessType,
                    strengthLevel = r.strengthLevel,
                    value = r.value,
                    graphPosition = r.graphPosition,
                )
            },
        )
    }

    private fun toAgeRange(ageYears: Int): Int =
        (ageYears / 10) * 10

    private fun calculateBmi(
        height: BigDecimal,
        weight: BigDecimal,
    ): BigDecimal {
        if (height.compareTo(BigDecimal.ZERO) == 0) {
            throw GlobalException(ErrorStatus.MEMBER_PROFILE_NOT_FOUND) // 적당한 에러로 교체
        }

        val heightMeter =
            height.divide(BigDecimal(100), 4, RoundingMode.HALF_UP)
        val heightSquare = heightMeter.multiply(heightMeter)
        if (heightSquare.compareTo(BigDecimal.ZERO) == 0) {
            throw GlobalException(ErrorStatus.MEMBER_PROFILE_NOT_FOUND)
        }

        return weight.divide(heightSquare, 2, RoundingMode.HALF_UP)
    }

    /**
     * strengthLevel 이 1이면 pushUpReps × 0.3
     * 2면 pushUpReps × 0.6
     * 3이면 pushUpReps 그대로
     */
    private fun calculateStrengthWeightedAmount(
        strengthLevel: Int,
        pushUpReps: Int,
    ): BigDecimal {
        val reps = BigDecimal(pushUpReps)
        val result = when (strengthLevel) {
            1 -> reps.multiply(BigDecimal("0.3"))
            2 -> reps.multiply(BigDecimal("0.6"))
            3 -> reps
            else -> throw GlobalException(ErrorStatus.MEMBER_PROFILE_NOT_FOUND)
        }
        return result.setScale(2, RoundingMode.HALF_UP)
    }

    /**
     * 성별에 따른 VO2max 계산
     */
    private fun calculateStepTestVo2max(
        gender: Gender,
        ageYears: Int,
        height: BigDecimal,
        weight: BigDecimal,
        recoveryBpm: Int,
    ): BigDecimal {
        val age = ageYears.toDouble()
        val h = height.toDouble()
        val w = weight.toDouble()
        val r = recoveryBpm.toDouble()

        val vo2 = when (gender) {
            Gender.M -> 70.597 - 0.246 * age + 0.077 * h - 0.222 * w - 0.147 * r
            Gender.F -> 54.337 - 0.185 * age + 0.097 * h - 0.246 * w - 0.122 * r
        }

        return BigDecimal.valueOf(vo2).setScale(2, RoundingMode.HALF_UP)
    }

    /**
     * 한 회원이 여러 번 측정했을 때 가장 최신 1개만 남김.
     * 나 자신은 measureDay 기준으로 강제 고정.
     * 국민체력100에서 수집한 정제데이터는 memberId 가 없으므로 전부 포함.
     */
    private fun latestPerMember(
        sameAgeRangeFitnessList: List<Fitness>,
        myFitness: Fitness,
    ): List<Fitness> {
        // 국민체력100에서 수집한 정제데이터 (memberId 없음) → 전부 순위에 포함
        val fitness100Data = sameAgeRangeFitnessList.filter { it.memberId == null }

        // 실제 회원 데이터만 모아서 "회원당 최신 1개"로 압축
        val latestPerMember = sameAgeRangeFitnessList
            .filter { it.memberId != null }
            .groupBy { it.memberId!! } // 회원 별로 리스트 묶기
            .map { (memberIdKey, memberFitnessList) ->
                if (memberIdKey == myFitness.memberId) {
                    // 조회 요청한 회원은 그날 측정한 값으로 고정
                    myFitness
                } else {
                    // 그 회원이 가진 측정 중 가장 최근 것
                    memberFitnessList.maxBy { it.measureDay }
                }
            }

        // 국민체력100 데이터 + 실제 회원 최신 데이터
        return fitness100Data + latestPerMember
    }

    /**
     * 체력 타입 1개(STRENGTH 등)에 대해
     * - 내 값 뽑고
     * - 같은 나이대 리스트에서 그 타입 값 있는 사람만 모아서
     * - 내 순위, 그래프 계산
     * 값이 없으면 null
     */
    private fun calcTypeRank(
        me: Fitness,
        all: List<Fitness>,
        type: FitnessType,
    ): TypePerRankResult? {
        // fitness type 별 나의 측정 값 (횟수, cm, VO2max 등)
        val myValue = type.toValue(me) ?: return null
        // 같은 나이대에서 그 타입 값 있는 사람만 모음
        val compValues = all.mapNotNull { type.toValue(it) }

        val sorted = compValues.sortedByDescending { it }
        // indexOfFirst: 첫 번째로 조건에 맞는 요소의 인덱스 반환, 없으면 -1 반환
        val myRank = sorted.indexOfFirst { it.compareTo(myValue) == 0 } + 1
        val total = sorted.size

        val graphPosition = if (total <= 1) {
            1.0
        } else {
            val raw = 1.0 - (myRank - 1).toDouble() / (total - 1).toDouble()
            (raw * 10_000).roundToInt() / 10_000.0
        }

        return TypePerRankResult(
            fitnessType = type,
            strengthLevel = if (type == FitnessType.STRENGTH) me.strengthLevel else null,
            value = myValue,
            rank = myRank,
            total = total,
            graphPosition = graphPosition,
        )
    }


    private fun rankToGrade(rank: Int, total: Int): Int {
        if (total <= 0) return 4
        val ratio = rank.toDouble() / total.toDouble()
        return when {
            ratio <= 0.30 -> 1
            ratio <= 0.50 -> 2
            ratio <= 0.70 -> 3
            else -> 4
        }
    }
}
