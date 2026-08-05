package com.acea.backend.security;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.domain.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * JWT Service: generation, validation, and claim extraction.
 *
 * <p>Algorithm: HS512 (HMAC-SHA512)
 * <p>Claims embedded: sub (userId), email, role, jti (unique token ID)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final AppProperties appProperties;

    // ── Token Generation ──────────────────────────────────────────────────

    /**
     * Generate an access token for the given user.
     * Token is signed with HS512 and includes standard + custom claims.
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role",  user.getRole().name());
        claims.put("tier",  user.getAccountTier());
        claims.put("emailVerified", user.isEmailVerified());
        return buildToken(claims, user.getId().toString(), appProperties.jwt().accessTokenExpiryMs());
    }

    /**
     * Generate an access token from a set of existing claims (used during refresh).
     */
    public String generateAccessToken(User user, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("email", user.getEmail());
        claims.put("role",  user.getRole().name());
        return buildToken(claims, user.getId().toString(), appProperties.jwt().accessTokenExpiryMs());
    }

    // ── Token Validation ──────────────────────────────────────────────────

    public boolean isTokenValid(String token, User user) {
        try {
            final String subject = extractSubject(token);
            return subject.equals(user.getId().toString()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ── Claim Extraction ──────────────────────────────────────────────────

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public long getAccessTokenExpiryMs() {
        return appProperties.jwt().accessTokenExpiryMs();
    }

    // ── Internal ──────────────────────────────────────────────────────────

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiryMs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuer(appProperties.jwt().issuer())
                .audience().add(appProperties.jwt().audience()).and()
                .id(UUID.randomUUID().toString())        // jti — unique token ID
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiryMs)))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer(appProperties.jwt().issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
            java.util.Base64.getEncoder().encodeToString(
                appProperties.jwt().secret().getBytes()
            )
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
