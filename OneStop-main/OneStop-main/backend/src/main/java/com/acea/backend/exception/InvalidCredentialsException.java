package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AceaException {
    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
}
