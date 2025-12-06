package beeve.biz.fitness.enum

enum class FitnessType {
    // 근력
    STRENGTH,
    // 심폐지구력 (건강체력)
    CARDIO,
    // 근지구력 (건강체력)
    ENDURANCE,
    // 유연성 (건강체력)
    FLEXIBILITY,
    // 민첩성 (운동체력)
    AGILITY,
    // 순발력 (운동체력)
    QUICKNESS;

//    // 의존 방향을 service -> enum -> entity 로 하고
//    // “타입 → 필드” 매핑이라 타입이 책임지는 구조라 여기 다 정의
//    fun toValue(fitness: Fitness): BigDecimal? =
//        when (this) {
//            STRENGTH -> fitness.strengthWeightedAmount?.bigDecimalValue()
//            ENDURANCE -> fitness.curlUpReps?.toBigDecimal()
//            CARDIO -> fitness.stepTestVo2max?.bigDecimalValue()
//            FLEXIBILITY -> fitness.sitAndReach?.toBigDecimal()
//            QUICKNESS -> fitness.standingLongJump?.bigDecimalValue()
//            AGILITY -> fitness.sideStepReps?.toBigDecimal()
//        }
}