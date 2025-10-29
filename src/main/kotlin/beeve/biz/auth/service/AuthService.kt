package beeve.biz.auth.service

import beeve.biz.auth.dto.request.SocialLoginRequest
import beeve.biz.auth.dto.request.RefreshTokenRequest
import beeve.biz.auth.dto.response.TokenResponse


interface AuthService {
    fun socialLogin(request: SocialLoginRequest): TokenResponse
    fun refresh(request: RefreshTokenRequest): TokenResponse
    fun logout(memberId: Long, request: RefreshTokenRequest)
}