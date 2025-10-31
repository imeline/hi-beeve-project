package beeve.biz.fitness.service

import beeve.biz.fitness.dto.request.FitnessCreateRequest
import beeve.biz.fitness.entity.Fitness
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
}