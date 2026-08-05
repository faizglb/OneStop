package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

/** Base exception for all ACEA domain exceptions. */
public abstract class AceaException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    protected AceaException(ErrorCode errorCode, HttpStatus httpStatus, String message) {
        super(message);
        this.errorCode  = errorCode;
        this.httpStatus = httpStatus;
    }

    public ErrorCode getErrorCode()   { return errorCode; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}
