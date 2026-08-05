package com.acea.backend.exception;

import com.acea.backend.dto.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Global exception handler — converts all exceptions to structured {@link ApiResponse} envelopes.
 *
 * <p>Priority order (most specific first):
 * 1. Domain exceptions ({@link AceaException} subclasses)
 * 2. Validation exceptions
 * 3. Spring Security exceptions
 * 4. JWT exceptions
 * 5. Generic fallback
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ── Domain Exceptions ─────────────────────────────────────────────────

    @ExceptionHandler(AceaException.class)
    public ResponseEntity<ApiResponse<Void>> handleAceaException(
            AceaException ex, HttpServletRequest request) {

        log.warn("Domain exception [{}] at [{}]: {}", ex.getErrorCode(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage(), request.getRequestURI()));
    }

    // ── Validation Exceptions ─────────────────────────────────────────────

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
            fieldErrors.computeIfAbsent(fe.getField(), k -> new ArrayList<>())
                       .add(fe.getDefaultMessage())
        );
        ex.getBindingResult().getGlobalErrors().forEach(ge ->
            fieldErrors.computeIfAbsent(ge.getObjectName(), k -> new ArrayList<>())
                       .add(ge.getDefaultMessage())
        );

        log.debug("Validation failed: {}", fieldErrors);
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ApiResponse.validationError("Validation failed", fieldErrors, getPath(request)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        Map<String, List<String>> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.groupingBy(
                cv -> cv.getPropertyPath().toString(),
                Collectors.mapping(cv -> cv.getMessage(), Collectors.toList())
            ));

        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ApiResponse.validationError("Constraint violation", errors, request.getRequestURI()));
    }

    // ── JWT Exceptions ────────────────────────────────────────────────────

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpiredJwt(
            ExpiredJwtException ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(401, "Access token has expired. Please refresh.", request.getRequestURI()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(
            JwtException ex, HttpServletRequest request) {
        log.warn("JWT error at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(401, "Invalid access token.", request.getRequestURI()));
    }

    // ── Spring Security ───────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(403, "Access denied.", request.getRequestURI()));
    }

    // ── Database ──────────────────────────────────────────────────────────

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Data integrity violation at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(409, "A record with this data already exists.", request.getRequestURI()));
    }

    // ── Malformed Request ─────────────────────────────────────────────────

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(400, "Malformed or unreadable request body.", getPath(request)));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(400, "Required parameter missing: " + ex.getParameterName(), getPath(request)));
    }

    // ── Fallback ──────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(500, "An unexpected error occurred. Please try again later.", request.getRequestURI()));
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
