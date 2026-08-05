package com.acea.backend.security;

import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * JWT Authentication Filter — runs once per request.
 *
 * <p>Flow:
 * 1. Extract Bearer token from Authorization header
 * 2. Validate token signature, expiry, and issuer
 * 3. Load user from DB (ensures account is still active)
 * 4. Set authentication in SecurityContext
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX        = "Bearer ";

    private final JwtService       jwtService;
    private final UserRepository   userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // Skip if no Bearer token present
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(BEARER_PREFIX.length());

        try {
            final String userIdStr = jwtService.extractSubject(jwt);

            // Only authenticate if not already authenticated in this request
            if (StringUtils.hasText(userIdStr) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UUID userId = UUID.fromString(userIdStr);
                User user = userRepository.findById(userId).orElse(null);

                if (user != null && jwtService.isTokenValid(jwt, user) && user.isEnabled()) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Authenticated user [{}] for request [{}]", user.getEmail(), request.getRequestURI());
                }
            }
        } catch (Exception e) {
            log.warn("JWT filter error for request [{}]: {}", request.getRequestURI(), e.getMessage());
            // Do NOT throw — let SecurityContext remain empty; downstream 401 will handle it
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip JWT check for public actuator/health endpoints
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/actuator/health");
    }
}
