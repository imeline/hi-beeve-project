package beeve.biz.recommend.service

import beeve.biz.fitness.enum.MeasurePlace
import beeve.biz.recommend.dto.request.PersonalPlanRequest
import beeve.biz.recommend.dto.request.WeekPlanRequest
import beeve.biz.recommend.dto.response.WeekPlanResponse
import beeve.biz.recommend.rest.GeminiApiClient
import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class RecommendServiceImpl(
    private val aiApiClient: GeminiApiClient
) : RecommendService {

    private val log = KotlinLogging.logger {}

    override fun getRecommendWeekPlan(request: PersonalPlanRequest) : WeekPlanResponse {
        val req = WeekPlanRequest.toGeminiRequest(
            gender = request.gender,
            age = request.age,
            measurePlace = request.measurePlace ?: MeasurePlace.HOME,
            purpose = request.purpose,
            contraindications = request.contraindications ?: ""
        )

        try {
            val res = aiApiClient.generateWeekPlan("gemini-2.5-flash-lite", req);
            log.info {"gemini response: $res"}
            return res
        } catch (e: Exception) {
            log.error(e) { "gemini failed" }
            throw GlobalException(ErrorStatus.FAILED_TO_RECOMMEND_PROGRAM)
        }
    }
}
