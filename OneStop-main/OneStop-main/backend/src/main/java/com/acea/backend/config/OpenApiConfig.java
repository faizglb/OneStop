package com.acea.backend.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 / Swagger configuration.
 * Swagger UI accessible at: /api/v1/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Bean
    public OpenAPI aceaOpenAPI() {
        return new OpenAPI()
            .info(apiInfo())
            .servers(List.of(
                new Server().url("http://localhost:8080" + contextPath).description("Local Development"),
                new Server().url("https://api.acea.io" + contextPath).description("Production")
            ))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Enter your JWT access token")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Info apiInfo() {
        return new Info()
            .title("ACEA — AI Career & Education Advisor API")
            .description("""
                ## ACEA Platform REST API
                
                Production-ready REST API for the AI Career & Education Advisor platform.
                
                ### Authentication
                - Register → `POST /auth/register`
                - Login → `POST /auth/login` (returns `accessToken` + `refreshToken`)
                - Include `Authorization: Bearer <accessToken>` in protected requests
                - Refresh → `POST /auth/refresh` with `refreshToken`
                
                ### Token Lifetimes
                - Access Token: **15 minutes** (HS512 JWT)
                - Refresh Token: **7 days** (opaque, stored as SHA-256 hash)
                
                ### Response Format
                All responses follow the `ApiResponse<T>` envelope:
                ```json
                {
                  "success": true,
                  "status": 200,
                  "message": "...",
                  "data": { ... },
                  "timestamp": "2026-07-13T10:00:00Z"
                }
                ```
                """)
            .version("1.0.0")
            .contact(new Contact()
                .name("ACEA Platform Team")
                .email("api@acea.io")
                .url("https://acea.io"))
            .license(new License()
                .name("Proprietary")
                .url("https://acea.io/terms"));
    }
}
