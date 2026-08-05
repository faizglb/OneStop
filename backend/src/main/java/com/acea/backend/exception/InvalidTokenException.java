package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AceaException {
    public InvalidTokenException(ErrorCode code, String message) {
        super(code, HttpStatus.UNAUTHORIZED, message);
    }
}
