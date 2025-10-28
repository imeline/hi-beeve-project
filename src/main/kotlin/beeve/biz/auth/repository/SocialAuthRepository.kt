package beeve.biz.auth.repository

import beeve.biz.auth.entity.SocialAuth
import beeve.biz.auth.enum.Provider
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SocialAuthRepository : JpaRepository<SocialAuth, Long> {
    fun findByProviderAndProviderUserId(
        provider: Provider, providerUserId: String): Optional<SocialAuth>
}