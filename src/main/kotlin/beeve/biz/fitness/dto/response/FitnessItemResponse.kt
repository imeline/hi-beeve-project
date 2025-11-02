package beeve.biz.fitness.dto.response

import beeve.biz.fitness.enum.FitnessType
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "체력별 상세 정보")
data class FitnessItemResponse(

    @field:Schema(
        description = "체력 타입(STRENGTH, ENDURANCE, CARDIO, " +
                "FLEXIBILITY, QUICKNESS, AGILITY)"
    )
    val fitnessType: FitnessType,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:Schema(
        description = "STRRENGTH 일 때만 null이 아닌 근력 난이도 (1~3). " +
                "null일 땐, 필드 자체를 제외", nullable = true
    )
    val strengthLevel: Int? = null,

    @field:Schema(description = "해당 체력의 실제 측정값 (횟수, cm, VO2max 등)")
    val value: BigDecimal,

    @field:Schema(description = "그래프에 표시할 상대 순위 값 (0.0 ~ 1.0)")
    val graphPosition: Double,
)