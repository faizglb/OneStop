package com.acea.backend.exception;

import org.springframework.http.HttpStatus;

/** Thrown when a student tries to submit after the assessment time has expired. */
public class AssessmentExpiredException extends AceaException {

    public AssessmentExpiredException(String message) {
        super(ErrorCode.ASSESSMENT_TIME_EXPIRED, HttpStatus.GONE, message);
    }
}
