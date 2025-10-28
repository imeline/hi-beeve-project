package beeve.common.config

import beeve.biz.auth.security.ApiAuthenticationEntryPoint
import beeve.biz.auth.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity,
                            apiAuthenticationEntryPoint: ApiAuthenticationEntryPoint
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/v1/auth/logout").authenticated()
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers(
                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { eh ->
                eh.authenticationEntryPoint(apiAuthenticationEntryPoint)
            }
            // addFilterBefore는 Class<? extends Filter> 타입 파라미터를 요구
            // 따라서 코틀린 타입에서 자바 Class 객체를 꺼내는 ::class.java 사용
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .cors { }

        return http.build()
    }
}