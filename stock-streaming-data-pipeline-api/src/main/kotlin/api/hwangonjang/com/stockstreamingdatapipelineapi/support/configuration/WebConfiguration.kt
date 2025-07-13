package api.hwangonjang.com.stockstreamingdatapipelineapi.support.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerSentEventHttpMessageWriter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebConfiguration: WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("*")
            .allowCredentials(true)
    }

    @Bean
    fun serverSentEventHttpMessageWriter(): ServerSentEventHttpMessageWriter {
        return ServerSentEventHttpMessageWriter()
    }
}