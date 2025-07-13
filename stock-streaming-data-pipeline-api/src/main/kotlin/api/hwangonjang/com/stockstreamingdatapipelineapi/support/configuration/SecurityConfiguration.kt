package api.hwangonjang.com.stockstreamingdatapipelineapi.support.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration(
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .formLogin {
                it.disable()
            }.httpBasic {
                it.disable()
            }.csrf {
                it.disable()
            }.headers {
                it.frameOptions { frameOptionsConfig ->
                    frameOptionsConfig.disable()
                }
            }.sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.authorizeHttpRequests {
                it.requestMatchers("/", "/swagger-ui/**", "/oauth2/**", "/v3/api-docs/**", "/v1/auth/tokens/refresh", "/v1/users/logout", "/dev/**", "/v1/auth/mobile/google", "/stomp/chat").permitAll()
                    .anyRequest().permitAll()
            }

        return http.build()
    }
}
