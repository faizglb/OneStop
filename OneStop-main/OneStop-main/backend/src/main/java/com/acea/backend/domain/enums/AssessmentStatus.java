package com.acea.backend.domain.enums;

/** Lifecycle status of an assessment template. */
public enum AssessmentStatus {
    /** Visible and accessible to students. */
    ACTIVE,
    /** Hidden from students but saved. */
    INACTIVE,
    /** Work-in-progress, not yet published. */
    DRAFT
}
