package beeve.demo

import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import beeve.common.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/test")
class DemoController {

    // ✅ 정상 응답
    @GetMapping("/success")
    fun success(): ApiResponse<String> {
        return ApiResponse.onSuccess("내보낼 데이터(DTO)가 들어가면 됨")
    }

    // ❌ 커스텀 비즈니스 예외 (GlobalException)
    @GetMapping("/biz-error")
    fun bizError(): ApiResponse<Unit> {
        /** 아래 코드를 서비스 로직에서 **/
        throw GlobalException(ErrorStatus.DUPLICATE_LOGIN_EMAIL)
    }

    // 💥 일반 런타임 예외 (핸들러에서 500 처리)
    @GetMapping("/runtime-error")
    fun runtimeError(): ApiResponse<Unit> {
        throw IllegalStateException("예상치 못한 런타임 예외!")
    }
}