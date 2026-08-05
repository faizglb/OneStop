package com.acea.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.acea.backend.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Auditing configuration.
 * Provides the current authenticated user's ID for @CreatedBy / @LastModifiedBy.
 * Referenced by @EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
 */
@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<UUID> auditorAwareImpl() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User user)) {
                return Optional.empty();
            }
            return Optional.of(user.getId());
        };
    }
}
