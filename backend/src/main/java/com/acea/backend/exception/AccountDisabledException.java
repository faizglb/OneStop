package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

public class AccountDisabledException extends AceaException {
    public AccountDisabledException() {
        super(ErrorCode.ACCOUNT_DISABLED, HttpStatus.FORBIDDEN, "Your account has been disabled. Please contact support.");
    }
}
