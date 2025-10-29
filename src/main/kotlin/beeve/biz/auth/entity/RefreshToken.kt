package beeve.biz.auth.entity

import beeve.common.base.TimeStamped
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "refresh_token")
class RefreshToken(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    val refreshTokenId: Long? = null,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Column(name = "token", columnDefinition = "TEXT", nullable = false)
    var token: String? = null

) : TimeStamped() {

    fun updateToken(newToken: String?) {
        this.token = newToken
    }

    companion object {
        fun of(memberId: Long, token: String?): RefreshToken =
            RefreshToken(memberId = memberId, token = token)
    }
}