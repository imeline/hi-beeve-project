package beeve.common.exception

import beeve.common.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
open class GlobalExceptionHandler {

    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

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

        log.warn(
            "code: {}, uri: {}, msg: {}",
            es.name, request.requestURI, ex.message ?: es.message
        )
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
        log.warn("uri: {}, fields: {}", request.requestURI, message)
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
        log.warn("uri: {}, fields: {}", request.requestURI, message)
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
        log.warn("uri: {}, fields: {}", request.requestURI, ex.message)
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
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()

        log.warn("uri: {}, fields: {}", request.requestURI, ex.message, ex)
        return ApiResponse.onFailure(
            ErrorStatus.INTERNAL_SERVER_ERROR, ex.message ?: "서버 오류"
        )
    }
}
