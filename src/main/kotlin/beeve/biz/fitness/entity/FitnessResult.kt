package beeve.biz.fitness.entity

import beeve.biz.fitness.enum.FitnessProgram
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import java.math.BigDecimal

data class FitnessResult(
    val grade: Int?,                    // 등급 (건강체력: 1~4, 운동체력: 1~3)
    @Field(targetType = FieldType.DECIMAL128)
    val value: BigDecimal,              // 측정 값 (근력은 가중값)
    val program: FitnessProgram,        // 측정 프로그램
    @Field(targetType = FieldType.DECIMAL128)
    val rawValue: BigDecimal? = null    // 원시 측정 값. 근력, 심폐지구력(stepTestRecoveryBpm)만 가짐
)