package beeve.demo

import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import beeve.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Test", description = "테스트 API")
@RestController
@RequestMapping("/api/v1/test")
class DemoController {

    // ✅ 정상 응답
    // swagger 전역 토큰 설정이 필요없는 메소드 - @Operation(security = []) 추가
    @Operation(summary = "성공 test", description = "정상 응답을 반환합니다."/*, security = []*/)
    @GetMapping("/success")
    fun success(): ApiResponse<String> {
        return ApiResponse.onSuccess("내보낼 데이터(DTO)가 들어가면 됨")
    }

    // ❌ 커스텀 비즈니스 예외 (GlobalException)
    @Operation(summary = "커스텀 예외 test", description = "커스텀 예외를 발생시킵니다.")
    @GetMapping("/biz-error")
    fun bizError(): ApiResponse<Unit> {
        /** 아래 코드를 서비스 로직에서 **/
        throw GlobalException(ErrorStatus.DUPLICATE_LOGIN_EMAIL)
    }

    // 💥 일반 런타임 예외 (핸들러에서 500 처리)
    @Operation(summary = "핸들러 예외 test", description = "핸들러 감지 예외를 발생시킵니다.")
    @GetMapping("/runtime-error")
    fun runtimeError(): ApiResponse<Unit> {
        throw IllegalStateException("예상치 못한 런타임 예외!")
    }
}