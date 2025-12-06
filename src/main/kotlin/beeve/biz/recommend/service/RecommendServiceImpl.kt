package beeve.biz.recommend.service

import beeve.biz.fitness.enum.MeasurePlace
import beeve.biz.recommend.dto.request.PersonalPlanRequest
import beeve.biz.recommend.dto.request.WeekPlanRequest
import beeve.biz.recommend.dto.response.WeekPlanResponse
import beeve.biz.recommend.rest.GeminiApiClient
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RecommendServiceImpl(
    private val aiApiClient: GeminiApiClient,
    private val objectMapper: ObjectMapper,
) : RecommendService {

    private val log = KotlinLogging.logger {}

    override fun getRecommendWeekPlan(request: PersonalPlanRequest) : WeekPlanResponse {
        val request = WeekPlanRequest.toGeminiRequest(
            gender = request.gender,
            age = request.age,
            measurePlace = request.measurePlace ?: MeasurePlace.HOME,
            purpose = request.purpose,
            contraindications = request.contraindications ?: ""
        )

        try {
            val geminiRes = aiApiClient.generateWeekPlan(
                model = "gemini-2.5-flash-lite",
                request);

            log.info("gemini response: {}", geminiRes)
            val response = WeekPlanResponse.from(geminiRes, objectMapper)
            return response
        } catch (e: Exception) {
            log.error(e) { "gemini failed" }
            throw GlobalException(ErrorStatus.FAILED_TO_RECOMMEND_PROGRAM)
        }
    }
}
