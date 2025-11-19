package beeve.biz.auth.service

import beeve.biz.auth.dto.request.RefreshTokenRequest
import beeve.biz.auth.dto.request.SocialLoginRequest
import beeve.biz.auth.dto.response.AccessTokenResponse
import beeve.biz.auth.dto.response.LoginResponse


interface AuthService {
    fun socialLogin(req: SocialLoginRequest): LoginResponse
    fun refresh(req: RefreshTokenRequest): AccessTokenResponse
    fun logout(memberId: Long, req: RefreshTokenRequest)
}