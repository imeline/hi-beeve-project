package beeve.biz.auth.repository

import beeve.biz.auth.entity.SocialAuth
import beeve.biz.auth.enum.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SocialAuthRepository : JpaRepository<SocialAuth, Long> {
    fun findByProviderAndProviderUserId(
        provider: Provider, providerUserId: String
    ): Optional<SocialAuth>

    @Modifying
    @Query(
        """
        UPDATE SocialAuth s
        SET s.deletedYn = 'Y'
        WHERE s.memberId = :memberId
          AND s.deletedYn = 'N'
    """
    )
    fun softDeleteAllByMemberId(memberId: Long): Int
}