package beeve.biz.fitness.service

import beeve.biz.fitness.dto.request.FitnessMeasureRequest
import beeve.biz.fitness.dto.response.FitnessMeasureDatesResponse
import beeve.biz.fitness.dto.response.FitnessMeasureResponse
import java.time.LocalDate

interface FitnessMeasureService {

    fun createFitnessMeasure(memberId: Long, req: FitnessMeasureRequest)

    fun getFitnessMeasureDays(memberId: Long): FitnessMeasureDatesResponse

    fun getFitnessMeasure(memberId: Long, measureDay: LocalDate? = null): FitnessMeasureResponse
}
