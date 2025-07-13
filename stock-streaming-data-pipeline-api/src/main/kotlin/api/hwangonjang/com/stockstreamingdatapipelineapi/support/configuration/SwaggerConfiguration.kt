package api.hwangonjang.com.stockstreamingdatapipelineapi.support.configuration

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.DateSchema
import io.swagger.v3.oas.models.media.DateTimeSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.awt.Color
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Configuration
class SwaggerConfiguration {
    private val securityScheme = SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .`in`(SecurityScheme.In.HEADER)
        .name("Authorization")

    private val securityRequirementName = "bearerAuth"

    private val version = "0.0.0"

    init {
        SpringDocUtils
            .getConfig()
            .replaceWithSchema(
                Color::class.java,
                Schema<String>()
                    .type("string")
                    .format("color")
                    .example("#FFFFFFFF"),
            )
            .replaceWithSchema(
                LocalTime::class.java,
                Schema<LocalTime>()
                    .type("string")
                    .format("time")
                    .example(
                        LocalTime
                            .now()
                            .format(DateTimeFormatter.ofPattern("HH:mm")),
                    ),
            )
    }

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .servers(listOf(Server().apply { url = "/" }))
            .security(
                listOf(
                    SecurityRequirement()
                        .addList(securityRequirementName),
                ),
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        securityRequirementName,
                        securityScheme,
                    ),
            )
            .info(
                Info()
                    .title("Stock Streaming Data Pipeline API")
                    .description(description)
                    .version(version),
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Stock Streaming Data Pipeline API"),
            )
    }

    @Bean
    fun customOpenApiCustomiser(): OpenApiCustomizer = OpenApiCustomizer { openApi ->
        openApi.components?.schemas?.forEach { (_, schema) ->
            schema.properties?.values?.forEach { propertySchema ->
                when {
                    propertySchema is DateSchema -> {
                        propertySchema.type = "string"
                        propertySchema.format = "date"
                        propertySchema.example = "2024-07-18"
                    }

                    propertySchema is DateTimeSchema -> {
                        propertySchema.type = "string"
                        propertySchema.format = "date-time"
                        propertySchema.example = "2024-07-18T10:15:30"
                    }
                }
            }
        }
    }

    private val apiRootUrl = "http://localhost:8080"
    private val description = """
        <h3>요청 헤더</h3>
         
        필요한 요청 헤더 2개는 다음과 같습니다. <br />
        이때 "{토큰}" 대신 발급 받은 토큰을 넣어주세요. <br />
        
        * "Authorization: Bearer {토큰}"
        * "Content-Type: application/json"
        
        <h3>cURL 예시</h3> 
        <code>
        curl -X 'POST' <br />
        &nbsp;  '$apiRootUrl' <br />
        &nbsp;  -H 'Authorization: Bearer {토큰}' <br />
        &nbsp;  -H 'Content-Type: application/json' <br />
        </code>
        """.trimIndent()
}
