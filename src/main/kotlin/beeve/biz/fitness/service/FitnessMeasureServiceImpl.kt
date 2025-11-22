package beeve.biz.fitness.service

import beeve.biz.fitness.dto.request.FitnessMeasureRequest
import beeve.biz.fitness.entity.AgeRange
import beeve.biz.fitness.entity.FitnessMeasure
import beeve.biz.fitness.entity.FitnessResult
import beeve.biz.fitness.entity.RankStandard
import beeve.biz.fitness.enum.FitnessProgram
import beeve.biz.fitness.enum.FitnessType
import beeve.biz.fitness.repository.FitnessMeasureRepository
import beeve.biz.fitness.repository.RankStandardRepository
import beeve.biz.member.entity.Member
import beeve.biz.member.enum.Gender
import beeve.biz.member.service.MemberService
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Period

@Service
class FitnessMeasureServiceImpl(
    private val memberService: MemberService,
    private val rankStandardRepository: RankStandardRepository,
    private val fitnessMeasureRepository: FitnessMeasureRepository,
) : FitnessMeasureService {

    @Transactional
    override fun createFitnessMeasure(memberId: Long, req: FitnessMeasureRequest) {
        val today = LocalDate.now()

        // 1) 하루 1회 측정 제한
        if (fitnessMeasureRepository.existsByMemberIdAndMeasureDayAndDeletedYn(
                memberId, today, "N"
            )
        ) {
            throw GlobalException(ErrorStatus.FITNESS_TODAY_ALREADY_EXISTS)
        }

        // 2) 회원 조회
        val member = memberService.getActiveMemberById(memberId)

        // 3) 만 나이 계산
        val age = calculateAge(member.birthDate, today)

        // 4) RankStandard 조회 (해당 성별 + 나이대)
        val rankStandards = rankStandardRepository
            .findByGenderAndAge(member.gender, age)
            .sortedBy { it.grade }

        if (rankStandards.isEmpty()) {
            throw GlobalException(ErrorStatus.RANK_STANDARD_NOT_FOUND)
        }

        val ageRange = AgeRange(
            min = rankStandards.first().ageMin,
            max = rankStandards.first().ageMax,
        )

        // 5) FitnessResult map 생성
        val fitnessResults = createFitnessResults(member, age, req, rankStandards)

        // 6) totalGrade 계산
        val totalGrade = calculateTotalGrade(fitnessResults)

        // 7) FitnessMeasure 도큐먼트 생성 (팩토리 메소드 사용)
        val fitnessMeasure = FitnessMeasure.from(
            memberId = memberId,
            member = member,
            ageRange = ageRange,
            age = age,
            totalGrade = totalGrade,
            measurePlace = req.measurePlace,
            measureDay = today,
            fitnessResult = fitnessResults,
        )

        fitnessMeasureRepository.save(fitnessMeasure)
    }

    /**
     * FitnessResult map 생성
     */
    private fun createFitnessResults(
        member: Member,
        age: Int,
        req: FitnessMeasureRequest,
        rankStandards: List<RankStandard>,
    ): Map<FitnessType, FitnessResult> {

        val results = mutableMapOf<FitnessType, FitnessResult>()

        // --- STRENGTH (근력) ---
        val strengthResult = createStrengthResult(req, rankStandards)
        results[FitnessType.STRENGTH] = strengthResult

        // --- CARDIO (심폐지구력 – VO2max) ---
        val cardioResult = createCardioResult(member, age, req.stepTestRecoveryBpm, rankStandards)
        results[FitnessType.CARDIO] = cardioResult

        // --- ENDURANCE (근지구력 – 교차윗몸일으키기) ---
        val enduranceValue = BigDecimal.valueOf(req.crossCrunchReps.toLong())
        val enduranceGrade = resolveHealthGrade(
            type = FitnessType.ENDURANCE,
            value = enduranceValue,
            rankStandards = rankStandards,
        )
        results[FitnessType.ENDURANCE] = FitnessResult(
            grade = enduranceGrade,
            value = enduranceValue,
            program = FitnessProgram.CROSS_CRUNCH,
            rawValue = null,
        )

        // --- FLEXIBILITY (유연성 – 앉아윗몸앞으로굽히기) ---
        val flexibilityValue = req.sitAndReach
        val flexibilityGrade = resolveHealthGrade(
            type = FitnessType.FLEXIBILITY,
            value = flexibilityValue,
            rankStandards = rankStandards,
        )
        results[FitnessType.FLEXIBILITY] = FitnessResult(
            grade = flexibilityGrade,
            value = flexibilityValue,
            program = FitnessProgram.SIT_AND_REACH,
            rawValue = null,
        )

        // --- AGILITY (민첩성 – 반응시간, 작을수록 좋음) ---
        val agilityValue = req.reactionTime
        val agilityGrade = resolveAgilityGrade(agilityValue, rankStandards)
        results[FitnessType.AGILITY] = FitnessResult(
            grade = agilityGrade,
            value = agilityValue,
            program = FitnessProgram.REACTION_TIME,
            rawValue = null,
        )

        // --- QUICKNESS (순발력 – 체공시간, 클수록 좋음) ---
        val quicknessValue = req.flightTime
        val quicknessGrade = resolveQuicknessGrade(quicknessValue, rankStandards)
        results[FitnessType.QUICKNESS] = FitnessResult(
            grade = quicknessGrade,
            value = quicknessValue,
            program = FitnessProgram.FLIGHT_TIME,
            rawValue = null,
        )

        return results
    }

    /**
     * 근력(STRENGTH) FitnessResult 생성
     * - wall: value = raw * 0.3
     * - knee: value = raw * 0.6
     * - standard: value = raw
     */
    private fun createStrengthResult(
        req: FitnessMeasureRequest,
        rankStandards: List<RankStandard>,
    ): FitnessResult {
        val wall = req.wallPushUpReps
        val knee = req.kneePushUpReps
        val standard = req.standardPushUpReps

        val nonNullCount = listOf(wall, knee, standard).count { it != null }
        if (nonNullCount != 1) {
            // 세 개 중 정확히 하나만 있어야 함
            throw GlobalException(ErrorStatus.FITNESS_STRENGTH_INVALID)
        }

        val (program, rawValue, weightedValue) = when {
            wall != null -> {
                val raw = BigDecimal.valueOf(wall.toLong())
                Triple(
                    FitnessProgram.WALL_PUSH_UP,
                    raw,
                    raw.multiply(BigDecimal("0.3")),
                )
            }

            knee != null -> {
                val raw = BigDecimal.valueOf(knee.toLong())
                Triple(
                    FitnessProgram.KNEE_PUSH_UP,
                    raw,
                    raw.multiply(BigDecimal("0.6")),
                )
            }

            standard != null -> {
                val raw = BigDecimal.valueOf(standard.toLong())
                Triple(
                    FitnessProgram.STANDARD_PUSH_UP,
                    raw,
                    raw,
                )
            }

            else -> throw GlobalException(ErrorStatus.FITNESS_STRENGTH_INVALID)
        }

        val grade = resolveHealthGrade(
            type = FitnessType.STRENGTH,
            value = weightedValue,
            rankStandards = rankStandards,
        )

        return FitnessResult(
            grade = grade,
            value = weightedValue,
            program = program,
            rawValue = rawValue,
        )
    }

    /**
     * CARDIO – 스텝검사 VO2max 계산 + grade
     */
    private fun createCardioResult(
        member: Member,
        age: Int,
        recoveryBpm: Int,
        rankStandards: List<RankStandard>,
    ): FitnessResult {
        val vo2max = calculateStepTestVo2max(
            gender = member.gender,
            ageYears = age,
            height = member.height,
            weight = member.weight,
            recoveryBpm = recoveryBpm,
        )

        val grade = resolveHealthGrade(
            type = FitnessType.CARDIO,
            value = vo2max,
            rankStandards = rankStandards,
        )

        return FitnessResult(
            grade = grade,
            value = vo2max,
            program = FitnessProgram.VO2MAX,
            rawValue = BigDecimal.valueOf(recoveryBpm.toLong()),
        )
    }

    /**
     * 성별별 VO2max 계산식
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

    // ============================================================
    // 등급 판정 로직
    // ============================================================

    /**
     * 건강체력(STRENGTH, CARDIO, ENDURANCE, FLEXIBILITY) 등급 판정
     * - 1~3등급 기준은 rank_standard 사용
     * - 기준 미달 값은 4등급
     * - 값이 클수록 좋은 항목들
     */
    private fun resolveHealthGrade(
        type: FitnessType,
        value: BigDecimal,
        rankStandards: List<RankStandard>,
    ): Int {
        // 각 체력별 (grade, value) 등급, 기준값 리스트 생성
        val gradeBoundaries: List<Pair<Int, BigDecimal>> = when (type) {
            FitnessType.STRENGTH ->
                rankStandards.map { it.grade to BigDecimal.valueOf(it.pushUpReps.toLong()) }

            FitnessType.CARDIO ->
                rankStandards.map { it.grade to it.stepTestVo2max }

            FitnessType.ENDURANCE ->
                rankStandards.map { it.grade to BigDecimal.valueOf(it.crossCrunchReps.toLong()) }

            FitnessType.FLEXIBILITY ->
                rankStandards.map { it.grade to it.sitAndReach }

            else -> throw GlobalException(ErrorStatus.FITNESS_TYPE_INVALID)
        }

        // grade 오름차순(1 → 2 → 3) 기준으로 정렬
        val sorted = gradeBoundaries.sortedBy { it.first }

        // 값이 클수록 좋은 구조
        sorted.forEach { (grade, boundary) ->
            if (value >= boundary) return grade
        }

        // 어떤 기준도 충족 못하면 4등급
        return 4
    }

    /**
     * AGILITY(민첩성) – reactionTime: 작을수록 좋음
     * - rank_standard에는 1,2등급만 존재
     * - 둘 다 충족 못하면 3등급
     */
    private fun resolveAgilityGrade(
        value: BigDecimal,
        rankStandards: List<RankStandard>,
    ): Int {
        val gradeBoundaries = rankStandards
            .filter { it.reactionTime != null }
            .map { it.grade to it.reactionTime!! }
            .sortedBy { it.first }

        gradeBoundaries.forEach { (grade, boundary) ->
            if (value <= boundary) return grade
        }

        // 기준보다 느리면 3등급
        return 3
    }

    /**
     * QUICKNESS(순발력) – flightTime: 클수록 좋음
     * - rank_standard에는 1,2등급만 존재
     * - 둘 다 충족 못하면 3등급
     */
    private fun resolveQuicknessGrade(
        value: BigDecimal,
        rankStandards: List<RankStandard>,
    ): Int {
        val gradeBoundaries = rankStandards
            .filter { it.flightTime != null }
            .map { it.grade to it.flightTime!! }
            .sortedBy { it.first }

        gradeBoundaries.forEach { (grade, boundary) ->
            if (value >= boundary) return grade
        }

        return 3
    }

    /**
     * totalGrade 계산
     *
     * 건강체력: STRENGTH, CARDIO, ENDURANCE, FLEXIBILITY
     * 운동체력: AGILITY, QUICKNESS
     */
    private fun calculateTotalGrade(results: Map<FitnessType, FitnessResult>): Int {
        val strength = results.requireGrade(FitnessType.STRENGTH)
        val cardio = results.requireGrade(FitnessType.CARDIO)
        val endurance = results.requireGrade(FitnessType.ENDURANCE)
        val flexibility = results.requireGrade(FitnessType.FLEXIBILITY)
        val agility = results.requireGrade(FitnessType.AGILITY)
        val quickness = results.requireGrade(FitnessType.QUICKNESS)

        val healthGrades = listOf(strength, cardio, endurance, flexibility)
        val exerciseGrades = listOf(agility, quickness)

        // 1등급
        if (healthGrades.all { it == 1 } && exerciseGrades.any { it == 1 }) {
            return 1
        }

        // 2등급: 건강체력 모두 2등급 이상(=1 or 2), 운동체력 중 하나 2등급 이상
        if (healthGrades.all { it <= 2 } && exerciseGrades.any { it <= 2 }) {
            return 2
        }

        // 3등급: 건강체력 모두 3등급 이상(=1,2,3)
        if (healthGrades.all { it <= 3 }) {
            return 3
        }

        // 4등급: 심폐지구력 + 근력 둘 다 3등급 이상(=1,2,3)
        if (cardio <= 3 && strength <= 3) {
            return 4
        }

        // 나머지
        return 5
    }


    /**
     * Map에서 해당 FitnessType의 grade 값이 존재하는지 검증
     */
    private fun Map<FitnessType, FitnessResult>.requireGrade(type: FitnessType): Int {
        return this[type]?.grade
            ?: throw GlobalException(ErrorStatus.FITNESS_GRADE_MISSING)
    }

    private fun calculateAge(birthDate: LocalDate, today: LocalDate): Int =
        Period.between(birthDate, today).years
}