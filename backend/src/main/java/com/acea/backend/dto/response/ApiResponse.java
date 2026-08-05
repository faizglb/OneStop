package com.acea.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Unified API response wrapper for all endpoints.
 *
 * <p>Every response from this API follows this envelope structure:
 * <pre>
 * {
 *   "success": true,
 *   "status": 200,
 *   "message": "Operation successful",
 *   "data": { ... },
 *   "timestamp": "2026-07-13T10:00:00Z",
 *   "path": "/api/v1/auth/login"
 * }
 * </pre>
 *
 * @param <T> Type of the payload data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response envelope")
public record ApiResponse<T>(

        @Schema(description = "Whether the request succeeded")
        boolean success,

        @Schema(description = "HTTP status code", example = "200")
        int status,

        @Schema(description = "Human-readable message", example = "Login successful")
        String message,

        @Schema(description = "Response payload — type varies by endpoint")
        T data,

        @Schema(description = "Field-level validation errors")
        Map<String, List<String>> errors,

        @Schema(description = "Response timestamp (UTC ISO-8601)")
        Instant timestamp,

        @Schema(description = "Request path", example = "/api/v1/auth/login")
        String path
) {

    // ── Factory methods ────────────────────────────────────────────────────

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, HttpStatus.OK.value(), message, data, null, Instant.now(), null);
    }

    public static <T> ApiResponse<T> success(String message, T data, String path) {
        return new ApiResponse<>(true, HttpStatus.OK.value(), message, data, null, Instant.now(), path);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, HttpStatus.CREATED.value(), message, data, null, Instant.now(), null);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, HttpStatus.OK.value(), message, null, null, Instant.now(), null);
    }

    public static ApiResponse<Void> error(int status, String message, String path) {
        return new ApiResponse<>(false, status, message, null, null, Instant.now(), path);
    }

    public static ApiResponse<Void> validationError(
            String message, Map<String, List<String>> errors, String path) {
        return new ApiResponse<>(false, HttpStatus.UNPROCESSABLE_ENTITY.value(), message, null, errors, Instant.now(), path);
    }
}
