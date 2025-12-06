package beeve.biz.recommend.rest

import org.springframework.http.MediaType
import beeve.biz.recommend.dto.request.WeekPlanRequest
import beeve.biz.recommend.dto.response.GeminiGenerateContentResponse
import beeve.biz.recommend.dto.response.WeekPlanResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "geminiApi",
    url = "https://generativelanguage.googleapis.com",
    configuration = [beeve.common.config.FeignConfig::class])
interface GeminiApiClient {

    @PostMapping(
        value = ["/v1beta/models/{model}:generateContent"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun generateWeekPlan(
        @PathVariable model: String,
        @RequestBody req: WeekPlanRequest
    ): GeminiGenerateContentResponse
}
