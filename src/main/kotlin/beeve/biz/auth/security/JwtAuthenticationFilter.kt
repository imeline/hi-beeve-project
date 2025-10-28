package beeve.biz.auth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val BEARER = "Bearer "
    }

    // JWT 유효성 검증 제외 경로
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/refresh")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1) Authorization 헤더가 없거나 Bearer 접두사가 아니면, 인증 시도 없이 통과
        val header = request.getHeader(AUTHORIZATION)
        if (header.isNullOrBlank() || !header.startsWith(BEARER)) {
            filterChain.doFilter(request, response)
            return
        }

        // 2) 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(header)) {
            // 유효하지 않으면 여기서 401을 내지 않고 soft-pass
            // 보호된 경로는 SecurityConfig 인가 규칙에 의해 최종 401/403 처리됨
            filterChain.doFilter(request, response)
            return
        }

        // 3) 아직 인증 컨텍스트가 없으면 주입
        if (SecurityContextHolder.getContext().authentication == null) {
            val memberId = jwtTokenProvider.extractMemberId(header)
            val principal = UserPrincipal.of(memberId)

            val authentication = UsernamePasswordAuthenticationToken(
                principal, null, principal.authorities
            ).apply {
                details = WebAuthenticationDetailsSource().buildDetails(request)
            }

            SecurityContextHolder.getContext().authentication = authentication
        }

        // 4) 다음 필터로 진행
        filterChain.doFilter(request, response)
    }
}
