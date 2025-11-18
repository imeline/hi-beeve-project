package beeve.biz.auth.service

import beeve.biz.auth.dto.request.RefreshTokenRequest
import beeve.biz.auth.dto.request.SocialLoginRequest
import beeve.biz.auth.dto.response.TokenResponse


interface AuthService {
    fun socialLogin(req: SocialLoginRequest): TokenResponse
    fun refresh(req: RefreshTokenRequest): TokenResponse
    fun logout(memberId: Long, req: RefreshTokenRequest)
    fun deleteRefreshToken(memberId: Long)
}