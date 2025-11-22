package beeve.biz.fitness.service

import beeve.biz.fitness.dto.request.FitnessMeasureRequest
import beeve.biz.fitness.dto.response.FitnessMeasureDatesResponse

interface FitnessMeasureService {

    fun createFitnessMeasure(memberId: Long, req: FitnessMeasureRequest)

    fun getFitnessMeasureDays(memberId: Long): FitnessMeasureDatesResponse
}