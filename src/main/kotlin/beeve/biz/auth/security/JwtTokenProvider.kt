package beeve.biz.auth.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret-key}") secretKey: String,
    @Value("\${jwt.access-expiration}") private val accessExpiration: Long,
    @Value("\${jwt.refresh-expiration}") private val refreshExpiration: Long
) {
    private val signingKey: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    companion object { private const val BEARER_PREFIX = "Bearer " }

    fun stripBearer(s: String): String = s.removePrefix(BEARER_PREFIX).trim()

    fun generateAccessToken(userDetails: UserDetails): String = generateToken(userDetails, accessExpiration)
    fun generateRefreshToken(userDetails: UserDetails): String = generateToken(userDetails, refreshExpiration)

    private fun generateToken(userDetails: UserDetails, expirationMillis: Long): String {
        val now = Date()
        val token = Jwts.builder()
            .setSubject(userDetails.username)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + expirationMillis))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()
        return BEARER_PREFIX + token
    }

    fun validateToken(token: String): Boolean =
        try { !isTokenExpired(stripBearer(token)) } catch (_: Exception) { false }

    fun extractMemberId(token: String): Long =
        extractClaim(stripBearer(token)) { it.subject }.toLong()

    private fun isTokenExpired(token: String): Boolean =
        extractExpiration(token).before(Date())

    private fun extractExpiration(token: String): Date =
        extractClaim(token, Claims::getExpiration)

    private fun <T> extractClaim(token: String, resolver: (Claims) -> T): T =
        resolver(
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token)
                .body
        )
}
