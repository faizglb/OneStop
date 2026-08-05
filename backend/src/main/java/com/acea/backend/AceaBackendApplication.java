package com.acea.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ACEA Backend Application Entry Point.
 *
 * <p>AI Career & Education Advisor Platform — Spring Boot 3.x
 *
 * <p>Architecture: Clean Architecture + SOLID Principles
 * - Controller Layer: HTTP request handling, input validation
 * - Service Layer:    Business logic, orchestration
 * - Repository Layer: Data access via Spring Data JPA
 * - Domain Layer:     Entities, value objects, enums
 *
 * @author ACEA Platform Team
 * @version 1.0.0
 * @since 2026-07-12
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan("com.acea.backend.config.properties")
public class AceaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AceaBackendApplication.class, args);
    }
}
