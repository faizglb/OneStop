package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

/** Thrown when an operation would create a duplicate or conflicting assessment session. */
public class AssessmentConflictException extends AceaException {

    public AssessmentConflictException(String message) {
        super(ErrorCode.ASSESSMENT_ALREADY_ACTIVE, HttpStatus.CONFLICT, message);
    }
}
