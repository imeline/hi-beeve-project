//package beeve.biz.fitness.controller
//
//import beeve.biz.auth.security.JwtTokenProvider
//import beeve.biz.fitness.dto.request.FitnessCreateRequest
//import beeve.biz.fitness.dto.response.FitnessGetResponse
//import beeve.biz.fitness.dto.response.FitnessMeasureDatesResponse
//import beeve.biz.fitness.service.FitnessService
//import beeve.common.response.ApiResponse
//import io.swagger.v3.oas.annotations.Operation
//import io.swagger.v3.oas.annotations.tags.Tag
//import jakarta.validation.Valid
//import org.springframework.web.bind.annotation.*
//import java.time.LocalDate
//
//@Tag(name = "Fitness", description = "체력 측정 API")
//@RestController
//@RequestMapping("/api/v1/fitness")
//class FitnessController(
//    private val fitnessService: FitnessService,
//    private val jwtTokenProvider: JwtTokenProvider,
//) {
//
//    @Operation(
//        summary = "체력 측정값 등록",
//        description = """
//        회원의 기존 프로필(생년월일/성별/키/몸무게)이 등록되어 있어야 합니다.
//        측정일(measureDay)은 서버에서 오늘 날짜로 처리합니다.
//        """,
//    )
//    @PostMapping
//    fun createFitness(
//        @RequestHeader("Authorization") accessHeader: String,
//        @Valid @RequestBody req: FitnessCreateRequest,
//    ): ApiResponse<Void?> {
//        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
//        fitnessService.createFitness(memberId, req)
//        return ApiResponse.onSuccess(null)
//    }
//
//    @Operation(
//        summary = "체력 측정 가능 날짜 목록 조회",
//        description = """
//        사용자가 지금까지 체력을 측정한 날짜를 최신순으로 내려줍니다.
//        화면 캘린더에 뿌릴 때 사용합니다.
//        """,
//    )
//    @GetMapping("/measure-days")
//    fun getMeasureDates(
//        @RequestHeader("Authorization") accessHeader: String,
//    ): ApiResponse<FitnessMeasureDatesResponse> {
//        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
//        val res = fitnessService.getMeasureDates(memberId)
//        return ApiResponse.onSuccess(res)
//    }
//
//    @Operation(
//        summary = "체력 측정값 조회(날짜별)",
//        description = """
//        특정 날짜에 사용자가 측정한 체력 데이터를 조회합니다.
//        이때 순위/등급은 같은 나이대의 최신 측정 데이터(국민체력100 포함)를 기준으로 계산합니다.
//        """,
//    )
//    @GetMapping
//    fun getFitnessByDate(
//        @RequestHeader("Authorization") accessHeader: String,
//        @RequestParam measureDay: LocalDate,
//    ): ApiResponse<FitnessGetResponse> {
//        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
//        val res = fitnessService.getFitnessByDate(memberId, measureDay)
//        return ApiResponse.onSuccess(res)
//    }
//
//}