package beeve.common.config

import beeve.common.exception.ErrorStatus
import beeve.common.exception.GlobalException
import feign.FeignException
import feign.Logger
import feign.Request
import feign.RequestInterceptor
import feign.Retryer
import feign.codec.ErrorDecoder
import okhttp3.Interceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FeignConfig(
    @Value("\${google.ai.api-key}") private val apiKey: String
) {

    // 로깅 레벨 (NONE/BASIC/HEADERS/FULL)
    @Bean
    fun feignLoggerLevel(): Logger.Level = Logger.Level.BASIC

    // 리트라이 전략 (원치 않으면 NEVER_RETRY)
    @Bean
    fun retryer(): Retryer = Retryer.NEVER_RETRY

    // 에러 디코더
    @Bean
    fun errorDecoder(): ErrorDecoder = ErrorDecoder { methodKey, response ->
        val body = response.body()?.asReader()?.readText().orEmpty()
        when (response.status()) {
            400 -> GlobalException(ErrorStatus.BAD_REQUEST, body)
            401, 403 -> GlobalException(ErrorStatus.EXPIRED_ACCESS_TOKEN)
            404 -> GlobalException(ErrorStatus.NOT_FOUND)
            else -> FeignException.errorStatus(methodKey, response)
        }
    }

    @Bean
    fun requestInterceptor(): RequestInterceptor =
        RequestInterceptor { t ->
            t.header("x-goog-api-key", "apiKey")
            t.header("Content-Type", "application/json")
        }

}