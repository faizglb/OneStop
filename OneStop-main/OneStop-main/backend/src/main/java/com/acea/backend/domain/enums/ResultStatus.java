package com.acea.backend.domain.enums;

/** Status of a student's assessment result/attempt. */
public enum ResultStatus {
    /** Assessment has been started but not yet submitted. */
    IN_PROGRESS,
    /** Assessment has been fully submitted and scored. */
    COMPLETED,
    /** Assessment was abandoned (time expired or explicit abandon). */
    ABANDONED
}
