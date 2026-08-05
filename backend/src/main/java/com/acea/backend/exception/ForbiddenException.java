package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AceaException {
    public ForbiddenException(String message) {
        super(ErrorCode.ACCOUNT_SUSPENDED, HttpStatus.FORBIDDEN, message);
    }
}
