package beeve.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import beeve.common.exception.ErrorStatus
import org.springframework.http.HttpStatus

data class ApiResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T? = null
) {
    companion object {
        // 성공 - 응답 값이 있는 경우
        fun <T> onSuccess(data: T): ApiResponse<T> =
            ApiResponse(true, HttpStatus.OK.value().toString(), data = data)

        // 성공 - 응답 값이 없는 경우
        fun onSuccess(): ApiResponse<Void?> =
            ApiResponse(true, HttpStatus.OK.value().toString(), data = null)

        // 실패 - 커스텀 에러 처리
        fun <T> onFailure(status: ErrorStatus): ApiResponse<T> =
            ApiResponse(false, status.code, status.message, null)

        // 실패 - RestControllerAdvice 에러 처리
        fun <T> onFailure(status: ErrorStatus, message: String): ApiResponse<T> =
            ApiResponse(false, status.code, message, null)
    }
}
