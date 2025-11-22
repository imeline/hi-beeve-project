package beeve.biz.fitness.service

import beeve.biz.fitness.dto.request.FitnessMeasureRequest

interface FitnessMeasureService {

    fun createFitnessMeasure(memberId: Long, req: FitnessMeasureRequest)
}