package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

public class AccountLockedException extends AceaException {
    public AccountLockedException() {
        super(ErrorCode.ACCOUNT_LOCKED, HttpStatus.LOCKED, "Your account has been temporarily locked due to too many failed login attempts. Please try again later.");
    }
}
