package beeve.biz.auth.controller

import beeve.biz.auth.dto.request.RefreshTokenRequest
import beeve.biz.auth.dto.request.SocialLoginRequest
import beeve.biz.auth.dto.response.TokenResponse
import beeve.biz.auth.service.AuthService
import beeve.biz.auth.security.JwtTokenProvider
import beeve.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Operation(
        summary = "소셜 로그인",
        description = "provider(KAKAO|GOOGLE|APPLE)와 providerUserId로 로그인합니다. " +
                "성공 시 accessToken/refreshToken을 바디로 반환합니다.", security = []
    )
    @PostMapping("/login")
    fun login(@Valid @RequestBody req: SocialLoginRequest): ApiResponse<TokenResponse> {
        val res = authService.socialLogin(req)
        return ApiResponse.onSuccess(res)
    }

    @Operation(
        summary = "accessToken 재발급",
        description = "바디의 refreshToken을 검증하여 새 accessToken을 발급합니다.", security = []
    )
    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody req: RefreshTokenRequest): ApiResponse<TokenResponse> {
        val res = authService.refresh(req)
        return ApiResponse.onSuccess(res)
    }

    @Operation(
        summary = "로그아웃",
        description = "Authorization 헤더의 accessToken에서 memberId를 추출하고, " +
                "바디의 refreshToken이 DB 값과 일치하면 해당 refreshToken을 폐기(삭제)합니다."
    )
    @PostMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") accessHeader: String,
        @Valid @RequestBody req: RefreshTokenRequest
    ): ApiResponse<Void?> {
        val memberId = jwtTokenProvider.extractMemberId(accessHeader)
        authService.logout(memberId, req)
        return ApiResponse.onSuccess(null)
    }
}
