package beeve.biz.recommend.service

import beeve.biz.recommend.dto.request.PersonalPlanRequest
import beeve.biz.recommend.dto.request.WeekPlanRequest
import beeve.biz.recommend.dto.response.WeekPlanResponse

interface RecommendService {

    fun getRecommendWeekPlan(request: PersonalPlanRequest): WeekPlanResponse;
}