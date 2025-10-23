package beeve

import beeve.global.exception.GlobalException
import beeve.global.exception.ErrorStatus
import beeve.global.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/test")
class TestController {

    // ✅ 정상 응답
    @GetMapping("/success")
    fun success(): ApiResponse<String> {
        return ApiResponse.onSuccess("정상 응답입니다!")
    }

    // ❌ 커스텀 비즈니스 예외 (GlobalException)
    @GetMapping("/biz-error")
    fun bizError(): ApiResponse<Unit> {
        throw GlobalException(ErrorStatus.DUPLICATE_LOGIN_EMAIL, "이미 사용중인 이메일입니다.")
    }

    // 💥 일반 런타임 예외 (핸들러에서 500 처리)
    @GetMapping("/runtime-error")
    fun runtimeError(): ApiResponse<Unit> {
        throw IllegalStateException("예상치 못한 런타임 예외!")
    }
}
