package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AceaException {
    public BadRequestException(ErrorCode code, String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
