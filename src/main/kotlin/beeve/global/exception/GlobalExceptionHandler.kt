package beeve.global.exception

import beeve.global.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // 왜 default로 prod 프로필을 사용하지 않는지?
    // 로컬에서 prod로 연결되어 버리면 실제 운영 DB에 연결될 수 있기 때문
    @Value("\${spring.profiles.active:dev}")
    private lateinit var activeProfile: String

    /**
     * 비즈니스 예외 처리 (커스텀 4xx 등)
     */
    @ExceptionHandler(GlobalException::class)
    fun handleGlobalException(
        ex: GlobalException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResponse<Unit> {
        val es = ex.status

        response.status = es.httpStatus.value()
        return ApiResponse.onFailure(es, ex.message ?: es.message)
    }

    /**
     * @Valid @RequestBody 검증 실패 → 400
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResponse<Unit> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { e -> "${e.field}: ${e.defaultMessage}" }

        response.status = HttpStatus.BAD_REQUEST.value()
        return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST, message)
    }

    /**
     * @Valid @ModelAttribute, @RequestParam 검증 실패 → 400
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(
        ex: BindException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResponse<Unit> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { e -> "${e.field}: ${e.defaultMessage}" }

        response.status = HttpStatus.BAD_REQUEST.value()
        return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST, message)
    }

    /**
     * JSON 파싱 실패 / 요청 본문 읽기 실패 → 400
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResponse<Unit> {
        response.status = HttpStatus.BAD_REQUEST.value()
        return ApiResponse.onFailure(ErrorStatus.NOT_READABLE)
    }

    /**
     * 예상치 못한 모든 런타임 예외 처리 (내부 메시지 숨김)
     */
    @ExceptionHandler(RuntimeException::class)
    protected fun handleRuntimeException(
        ex: RuntimeException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResponse<Unit> {
        if (ex is GlobalException) {
            return handleGlobalException(ex, request, response)
        }

        val isProd = "prod".equals(activeProfile, ignoreCase = true)
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        // 배포 환경에서는 내부 오류 메시지를 숨김
        return if (isProd) {
            ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR)
        } else {
            ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR, ex.message ?: "서버 오류")
        }
    }
}
