package beeve.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.core.jackson.ModelResolver
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(title = "Beeve API", version = "v1"),
    security = [SecurityRequirement(name = "BearerAuth")]
)
@SecurityScheme(
    name = "BearerToken (Bearer 제외하고 토큰만 입력)",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@Configuration
class SwaggerConfig {

    @Bean
    fun modelResolver(objectMapper: ObjectMapper): ModelResolver =
        // springdoc가 스키마 생성 시 애플리케이션의 Jackson 설정을 반영하도록 연결
        ModelResolver(objectMapper)
}