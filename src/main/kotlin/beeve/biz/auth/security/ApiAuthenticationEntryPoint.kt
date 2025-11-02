package beeve.biz.auth.security

import beeve.common.exception.ErrorStatus
import beeve.common.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class ApiAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json;charset=UTF-8"

        val tag = request.getAttribute("AUTH_ERROR") as? String
        val body: ApiResponse<Unit> = when (tag) {
            "EXPIRED_ACCESS_TOKEN" -> ApiResponse.Companion.onFailure(
                ErrorStatus.EXPIRED_ACCESS_TOKEN)
            else                   -> ApiResponse.Companion.onFailure(
                ErrorStatus.UNAUTHORIZED)
        }

        response.writer.write(objectMapper.writeValueAsString(body))
    }
}