package com.acea.backend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for secure token generation and hashing.
 *
 * <p>Tokens are URL-safe Base64-encoded 32-byte random values (256-bit entropy).
 * Only SHA-256 hashes are stored in the database.
 */
public final class TokenUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32; // 256-bit entropy

    private TokenUtil() {}

    /**
     * Generates a cryptographically secure, URL-safe random token.
     * Format: base64url-encoded 32-byte random value (~43 chars)
     */
    public static String generateSecureToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Returns the SHA-256 hex digest of the given token.
     * Used to store a non-reversible hash of issued tokens.
     */
    public static String sha256Hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
