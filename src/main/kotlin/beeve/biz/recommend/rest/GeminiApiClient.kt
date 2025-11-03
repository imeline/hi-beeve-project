package beeve.biz.recommend.rest

import beeve.biz.recommend.dto.request.WeekPlanRequest
import beeve.biz.recommend.dto.response.WeekPlanResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "geminiApi",
    url = "https://generativelanguage.googleapis.com",
    configuration = [beeve.common.config.FeignConfig::class])
interface GeminiApiClient {

    @PostMapping("/v1beta/models/{model}:generateContent")
    fun generateWeekPlan(
        @PathVariable model: String,
        @RequestBody req: WeekPlanRequest
    ): WeekPlanResponse
}
