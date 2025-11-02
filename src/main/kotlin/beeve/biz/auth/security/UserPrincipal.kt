package beeve.biz.auth.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserPrincipal(
    val memberId: Long
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    override fun getPassword(): String? = null
    override fun getUsername(): String = memberId.toString()
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true

    companion object {
        fun of(memberId: Long) = UserPrincipal(memberId)
    }
}
