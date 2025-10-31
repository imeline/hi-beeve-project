package beeve.biz.fitness.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

@Schema(
    description = """
체력 측정 생성 요청

- 첫 체력 측정 때만 profile 을 보냅니다.
- 이후 요청부터는 profile 을 null 로 보내고, measure 만 전송합니다.
"""
)
data class FitnessCreateRequest(

    @field:Schema(
        description = "회원 프로필 정보. 첫 체력 측정 때만 값으로 전송, " +
                "이후에는 null 혹은 필드 자체를 제외",
        nullable = true
    )
    @field:Valid
    val profile: FitnessProfileRequest? = null,

    @field:Schema(
        description = "실제 체력 측정값. 매 요청마다 필수"
    )
    @field:Valid
    @field:NotNull(message = "measure 는 필수입니다.")
    val measure: FitnessMeasureRequest
)