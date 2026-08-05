package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends AceaException {
    public ConflictException(ErrorCode code, String message) {
        super(code, HttpStatus.CONFLICT, message);
    }
}
