package beeve.biz.auth.repository

import beeve.biz.auth.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByMemberId(memberId: Long): Optional<RefreshToken>
    fun deleteByMemberId(memberId: Long)
}