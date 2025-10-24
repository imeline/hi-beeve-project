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
        // 성공
        fun <T> onSuccess(data: T): ApiResponse<T> {
            return ApiResponse(true, HttpStatus.OK.value().toString(), null, data)
        }

        // 실패 - 커스텀 에러 처리
        fun <T> onFailure(status: ErrorStatus): ApiResponse<T> {
            return ApiResponse(false, status.code, status.message, null)
        }

        // 실패 - RestControllerAdvice 에러 처리
        fun <T> onFailure(status: ErrorStatus, message: String): ApiResponse<T> {
            return ApiResponse(false, status.code, message, null)
        }
    }
}
