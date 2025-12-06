package beeve.biz.fitness.service

import beeve.biz.fitness.dto.request.FitnessMeasureRequest
import beeve.biz.fitness.dto.response.FitnessMeasureDatesResponse
import beeve.biz.fitness.dto.response.FitnessMeasureResponse
import beeve.biz.fitness.entity.FitnessMeasure
import beeve.biz.fitness.entity.FitnessResult
import beeve.biz.fitness.enum.FitnessType
import java.time.LocalDate

interface FitnessMeasureService {

    fun createFitnessMeasure(memberId: Long, req: FitnessMeasureRequest)

    fun getFitnessMeasureDays(memberId: Long): FitnessMeasureDatesResponse

    fun getFitnessMeasure(memberId: Long, measureDay: LocalDate? = null): FitnessMeasureResponse

    fun calculateTotalRank(myMeasure: FitnessMeasure, compMeasures: List<FitnessMeasure>): Int

    fun calculateTotalGrade(results: Map<FitnessType, FitnessResult>): Int
}
