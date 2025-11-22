package beeve.biz.fitness.controller

import beeve.biz.auth.security.JwtTokenProvider
import beeve.biz.fitness.dto.request.FitnessMeasureRequest
import beeve.biz.fitness.dto.response.FitnessMeasureDatesResponse
import beeve.biz.fitness.service.FitnessMeasureService
import beeve.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "Fitness", description = "체력 측정 API")
@RestController
@RequestMapping("/api/v1/fitness")
class FitnessMeasureController(
    private val fitnessMeasureService: FitnessMeasureService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @Operation(
        summary = "체력 측정값 등록",
        description = """
        회원의 기존 프로필(생년월일/성별/키/몸무게)이 등록되어 있어야 합니다.
        측정일(measureDay)은 서버에서 오늘 날짜로 처리합니다.
        근력 항목(wallPushUpReps, kneePushUpReps, standardPushUpReps) 중 정확히 하나만 전송해야 합니다.
        """,
    )
    @PostMapping
    fun createFitnessMeasure(
        @RequestHeader("Authorization") accessHeader: String,
        @Valid @RequestBody req: FitnessMeasureRequest,
    ): ApiResponse<Void?> {
        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
        fitnessMeasureService.createFitnessMeasure(memberId, req)
        return ApiResponse.onSuccess(null)
    }

    @Operation(
        summary = "체력 측정 결과 조회 가능 날짜 목록",
        description = """
        회원이 체력 측정을 진행한 날짜 목록을 최근순으로 조회합니다.
        """,
    )
    @GetMapping("/measure-days")
    fun getMeasureDays(
        @RequestHeader("Authorization") accessHeader: String,
    ): ApiResponse<FitnessMeasureDatesResponse> {
        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
        val res = fitnessMeasureService.getFitnessMeasureDays(memberId)
        return ApiResponse.onSuccess(res)
    }
}
