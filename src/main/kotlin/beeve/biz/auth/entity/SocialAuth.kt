package beeve.biz.auth.entity

import beeve.biz.auth.enum.Provider
import beeve.common.base.TimeStamped
import jakarta.persistence.*

@Entity
@Table(name = "social_auth")
class SocialAuth(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_auth_id")
    val socialAuthId: Long? = null,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val provider: Provider,

    @Column(name = "provider_user_id", nullable = false, length = 255)
    val providerUserId: String,

    @Column(name = "consent_scope", length = 255)
    val consentScope: String? = null

) : TimeStamped()
