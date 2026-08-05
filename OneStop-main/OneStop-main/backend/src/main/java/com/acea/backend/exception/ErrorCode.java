package com.acea.backend.exception;

/**
 * Structured error codes used across exception classes.
 * Each code maps to a specific error condition for programmatic handling in the frontend.
 */
public enum ErrorCode {

    // Auth
    EMAIL_ALREADY_EXISTS,
    INVALID_CREDENTIALS,
    ACCOUNT_DISABLED,
    ACCOUNT_LOCKED,
    ACCOUNT_SUSPENDED,

    // Token
    INVALID_REFRESH_TOKEN,
    REFRESH_TOKEN_EXPIRED,
    INVALID_VERIFICATION_TOKEN,
    VERIFICATION_TOKEN_EXPIRED,
    INVALID_RESET_TOKEN,
    RESET_TOKEN_EXPIRED,
    INVALID_ACCESS_TOKEN,
    TOKEN_EXPIRED,

    // Email
    EMAIL_ALREADY_VERIFIED,
    EMAIL_SEND_FAILED,

    // Password
    SAME_PASSWORD,
    WEAK_PASSWORD,

    // Resource
    RESOURCE_NOT_FOUND,
    USER_NOT_FOUND,

    // Validation
    VALIDATION_FAILED,

    // Server
    INTERNAL_ERROR,
    SERVICE_UNAVAILABLE,

    // Rate Limiting
    TOO_MANY_REQUESTS,

    // Assessment — Sprint 3
    ASSESSMENT_NOT_FOUND,
    ASSESSMENT_SESSION_NOT_FOUND,
    ASSESSMENT_ALREADY_COMPLETED,
    ASSESSMENT_ALREADY_ACTIVE,
    ASSESSMENT_TIME_EXPIRED,
    INVALID_ANSWER_SUBMISSION,
    UNAUTHORIZED_RESULT_ACCESS;
}
