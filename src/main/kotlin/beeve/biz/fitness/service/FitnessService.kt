//package beeve.biz.fitness.service
//
//import beeve.biz.fitness.dto.request.FitnessCreateRequest
//import beeve.biz.fitness.dto.response.FitnessGetResponse
//import beeve.biz.fitness.dto.response.FitnessMeasureDatesResponse
//import java.time.LocalDate
//
//interface FitnessService {
//
//    fun createFitness(memberId: Long, req: FitnessCreateRequest)
//
//    fun getMeasureDates(memberId: Long): FitnessMeasureDatesResponse
//
//    fun getFitnessByDate(memberId: Long, measureDay: LocalDate): FitnessGetResponse
//}