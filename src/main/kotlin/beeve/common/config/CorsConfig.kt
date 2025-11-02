package beeve.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig(

    @Value("\${cors.allowed-origins}")
    private val allowedOrigins: String
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val origins = allowedOrigins
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val config = CorsConfiguration().apply {
            allowedOriginPatterns = origins
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With")
            exposedHeaders = listOf("Authorization")
            allowCredentials = false
            maxAge = 3600
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }
}
