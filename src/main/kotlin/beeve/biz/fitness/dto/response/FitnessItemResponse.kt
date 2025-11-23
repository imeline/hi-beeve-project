package beeve.biz.fitness.dto.response

import beeve.biz.fitness.enum.FitnessProgram
import beeve.biz.fitness.enum.FitnessType
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "개별 체력 요소 측정 결과")
data class FitnessItemResponse(

    @field:Schema(
        description = "체력 요소 타입. " +
                "STRENGTH(근력), CARDIO(심폐지구력), ENDURANCE(근지구력), " +
                "FLEXIBILITY(유연성), AGILITY(민첩성), QUICKNESS(순발력)"
    )
    val fitnessType: FitnessType,

    @field:Schema(description = "측정에 사용된 세부 프로그램")
    val program: FitnessProgram,

    @field:Schema(
        description = "측정 값(횟수/거리/시간/가중값 등). " +
                "근력(STRENGTH) 시 가중값, 심폐지구력(CARDIO) 시 VO2max, " +
                "그 외에는 실제 측정값"
    )
    val value: BigDecimal,

    @field:Schema(
        description = "원시 측정 값. " +
                "STRENGTH 시 실제 횟수(raw reps), CARDIO 시 회복기 심박수(recoveryBpm)만 존재하며, " +
                "그 외 타입은 필드 자체를 주지 않음",
        nullable = true
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val rawValue: BigDecimal? = null,

    @field:Schema(description = "해당 체력 요소의 등급")
    val grade: Int,
)