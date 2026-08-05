package com.acea.backend.security;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.enums.Role;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JwtService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    @Mock  private AppProperties appProperties;
    @Mock  private AppProperties.JwtProperties jwtProps;
    @InjectMocks private JwtService jwtService;

    private User testUser;
    private static final String SECRET =
        "acea-super-secret-jwt-key-must-be-at-least-512-bits-long-for-HS512-algorithm-test-only";
    private static final long ACCESS_EXPIRY_MS  = 900_000L;  // 15 min
    private static final long REFRESH_EXPIRY_MS = 604_800_000L; // 7 days

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(UUID.randomUUID())
            .email("rahul@test.com")
            .firstName("Rahul")
            .lastName("Sharma")
            .role(Role.STUDENT)
            .accountTier("FREE")
            .emailVerified(true)
            .active(true)
            .build();

        when(appProperties.jwt()).thenReturn(jwtProps);
        when(jwtProps.secret()).thenReturn(SECRET);
        when(jwtProps.issuer()).thenReturn("acea-platform");
        when(jwtProps.audience()).thenReturn("acea-clients");
        when(jwtProps.accessTokenExpiryMs()).thenReturn(ACCESS_EXPIRY_MS);
    }

    @Test
    @DisplayName("generateAccessToken: should produce a non-null, non-blank token")
    void generateAccessToken_shouldReturnValidToken() {
        String token = jwtService.generateAccessToken(testUser);
        assertThat(token).isNotNull().isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // JWT = header.payload.signature
    }

    @Test
    @DisplayName("extractSubject: should return the user ID as subject")
    void extractSubject_shouldReturnUserId() {
        String token = jwtService.generateAccessToken(testUser);
        String subject = jwtService.extractSubject(token);
        assertThat(subject).isEqualTo(testUser.getId().toString());
    }

    @Test
    @DisplayName("extractEmail: should return user email from claims")
    void extractEmail_shouldReturnCorrectEmail() {
        String token = jwtService.generateAccessToken(testUser);
        assertThat(jwtService.extractEmail(token)).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("isTokenValid: valid token for same user should return true")
    void isTokenValid_withValidToken_shouldReturnTrue() {
        String token = jwtService.generateAccessToken(testUser);
        assertThat(jwtService.isTokenValid(token, testUser)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid: token from different user should return false")
    void isTokenValid_withDifferentUser_shouldReturnFalse() {
        User otherUser = User.builder()
            .id(UUID.randomUUID())
            .email("other@test.com")
            .role(Role.STUDENT)
            .accountTier("FREE")
            .active(true)
            .build();

        String token = jwtService.generateAccessToken(testUser);
        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    @DisplayName("isTokenExpired: valid token should not be expired")
    void isTokenExpired_withFreshToken_shouldReturnFalse() {
        String token = jwtService.generateAccessToken(testUser);
        assertThat(jwtService.isTokenExpired(token)).isFalse();
    }

    @Test
    @DisplayName("extractRole: should return the user role")
    void extractRole_shouldReturnRole() {
        String token = jwtService.generateAccessToken(testUser);
        assertThat(jwtService.extractRole(token)).isEqualTo(Role.STUDENT.name());
    }

    @Test
    @DisplayName("Two tokens for same user should have different JTIs")
    void twoTokens_shouldHaveDifferentJtis() {
        String token1 = jwtService.generateAccessToken(testUser);
        String token2 = jwtService.generateAccessToken(testUser);
        assertThat(jwtService.extractJti(token1)).isNotEqualTo(jwtService.extractJti(token2));
    }
}
