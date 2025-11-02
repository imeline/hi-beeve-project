package beeve.biz.fitness.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "체력 측정 결과 조회 가능한 날짜 목록 응답")
data class FitnessMeasureDatesResponse(

    @field:Schema(description = "조회 가능한 전체 날짜 개수")
    val totalCount: Int,

    @field:Schema(
        description = "측정한 날짜 리스트 (최근순 정렬)"
    )
    val measureDates: List<LocalDate>,
) {
    companion object {
        fun from(measureDays: List<LocalDate>) = FitnessMeasureDatesResponse(
            totalCount = measureDays.size,
            measureDates = measureDays
        )
    }
}