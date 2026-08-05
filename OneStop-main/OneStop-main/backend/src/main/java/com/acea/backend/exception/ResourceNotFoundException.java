package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AceaException {
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, message);
    }
}
