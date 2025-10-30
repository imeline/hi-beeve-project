package beeve.biz.fitness.controller

import beeve.biz.auth.security.JwtTokenProvider
import beeve.biz.fitness.dto.request.FitnessCreateRequest
import beeve.biz.fitness.service.FitnessService
import beeve.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "Fitness", description = "체력 측정 API")
@RestController
@RequestMapping("/api/v1/fitness")
class FitnessController(
    private val fitnessService: FitnessService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @Operation(
        summary = "체력 측정값 등록",
        description = """
        첫 회원 정보 등록 시에는 profile 필드를 값으로 보내고,
        그 이후 요청부터는 null 로 보냅니다.
        측정일(measureDay)은 서버에서 오늘 날짜로 처리합니다.
        """,
    )
    @PostMapping
    fun createFitness(
        @RequestHeader("Authorization") accessHeader: String,
        @Valid @RequestBody req: FitnessCreateRequest,
    ): ApiResponse<Void?> {
        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
        fitnessService.createFitness(memberId, req)
        return ApiResponse.onSuccess(null)
    }
}