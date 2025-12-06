package beeve.biz.rank.controller

import beeve.biz.auth.security.JwtTokenProvider
import beeve.biz.fitness.dto.request.FitnessMeasureRequest
import beeve.biz.fitness.dto.response.FitnessMeasureDatesResponse
import beeve.biz.fitness.service.FitnessMeasureService
import beeve.biz.rank.dto.response.FitnessGradeSelectResponse
import beeve.biz.rank.dto.response.FitnessRankSelectResponse
import beeve.biz.rank.service.RankService
import beeve.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Rank", description = "체력 등급/순위 조회 API")
@RestController
@RequestMapping("/api/v1/rank")
class FitnessRankController (
    private val rankService: RankService,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Operation(
        summary = "체력 등급 조회",
        description = """ """,
    )
    @GetMapping("/grade-list")
    fun getGradeHistories(
        @RequestHeader("Authorization") accessHeader: String
    ): ApiResponse<FitnessGradeSelectResponse> {
        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
        val result = rankService.getGradeHistories(memberId)
        return ApiResponse.onSuccess(result)
    }

    @Operation(
        summary = "체력 순위 조회",
        description = """ """,
    )
    @GetMapping("/list")
    fun getRankHistories(
        @RequestHeader("Authorization") accessHeader: String
    ): ApiResponse<FitnessRankSelectResponse> {
        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
        val result = rankService.getRankHistories(memberId)
        return ApiResponse.onSuccess(result)
    }
}