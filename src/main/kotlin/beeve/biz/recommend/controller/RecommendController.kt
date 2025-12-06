package beeve.biz.recommend.controller

import beeve.biz.recommend.dto.request.PersonalPlanRequest
import beeve.biz.recommend.dto.request.WeekPlanRequest
import beeve.biz.recommend.dto.response.WeekPlanResponse
import beeve.biz.recommend.service.RecommendService
import beeve.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Recommend", description = "운동 추천 API")
@RestController
@RequestMapping("/api/v1/recommend")
class RecommendController(
    private val recommendService: RecommendService
) {

    @Operation(
        summary = "운동 추천",
        description = """
        AI를 통해 추천 운동 리스트 조회
        """,
    )
    @PostMapping
    fun getRecommendWeekPlan(
        @RequestBody request: PersonalPlanRequest
    ): ApiResponse<WeekPlanResponse> {
        val list = recommendService.getRecommendWeekPlan(request);
        return ApiResponse.onSuccess(list)
    }


}
