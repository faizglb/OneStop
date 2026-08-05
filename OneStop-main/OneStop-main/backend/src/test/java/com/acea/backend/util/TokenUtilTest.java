package com.acea.backend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TokenUtil Unit Tests")
class TokenUtilTest {

    @Test
    @DisplayName("generateSecureToken: should produce non-null, non-blank token")
    void generateSecureToken_shouldReturnNonBlank() {
        String token = TokenUtil.generateSecureToken();
        assertThat(token).isNotNull().isNotBlank();
    }

    @RepeatedTest(5)
    @DisplayName("generateSecureToken: repeated calls should produce unique tokens")
    void generateSecureToken_shouldBeUnique() {
        assertThat(TokenUtil.generateSecureToken())
            .isNotEqualTo(TokenUtil.generateSecureToken());
    }

    @Test
    @DisplayName("generateSecureToken: should be URL-safe (no + or /)")
    void generateSecureToken_shouldBeUrlSafe() {
        for (int i = 0; i < 20; i++) {
            String token = TokenUtil.generateSecureToken();
            assertThat(token).doesNotContain("+", "/", "=");
        }
    }

    @Test
    @DisplayName("sha256Hash: same input should always produce same hash")
    void sha256Hash_shouldBeDeterministic() {
        String input = "test-token-abc123";
        assertThat(TokenUtil.sha256Hash(input)).isEqualTo(TokenUtil.sha256Hash(input));
    }

    @Test
    @DisplayName("sha256Hash: different inputs should produce different hashes")
    void sha256Hash_differentInputs_shouldDiffer() {
        assertThat(TokenUtil.sha256Hash("token-a"))
            .isNotEqualTo(TokenUtil.sha256Hash("token-b"));
    }

    @Test
    @DisplayName("sha256Hash: output should be 64-char hex string")
    void sha256Hash_shouldBe64CharHex() {
        String hash = TokenUtil.sha256Hash("some-token");
        assertThat(hash).hasSize(64).matches("[0-9a-f]+");
    }
}
