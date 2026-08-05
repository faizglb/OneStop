package com.acea.backend.domain.enums;

/**
 * Platform roles following the Principle of Least Privilege.
 *
 * <p>Hierarchy:
 * SUPER_ADMIN > ADMIN > MENTOR > HR_MANAGER > PREMIUM_STUDENT > STUDENT
 *
 * <p>Spring Security convention: roles must be prefixed with "ROLE_"
 * for hasRole() expressions to work correctly.
 */
public enum Role {

    STUDENT("ROLE_STUDENT", "Standard student account — full access to learning features"),
    PREMIUM_STUDENT("ROLE_PREMIUM_STUDENT", "Premium student — unlimited AI queries, advanced features"),
    MENTOR("ROLE_MENTOR", "Industry professional providing mentorship sessions"),
    HR_MANAGER("ROLE_HR_MANAGER", "Enterprise HR — manages team learning plans"),
    CONTENT_MANAGER("ROLE_CONTENT_MANAGER", "Content manager — content management only"),
    ADMIN("ROLE_ADMIN", "Platform administrator — content and user management"),
    SUPER_ADMIN("ROLE_SUPER_ADMIN", "Superadmin — full platform access including system config");

    private final String springRole;
    private final String description;

    Role(String springRole, String description) {
        this.springRole = springRole;
        this.description = description;
    }

    public String getSpringRole() {
        return springRole;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns true if this role has at least admin-level access.
     */
    public boolean isAdminLevel() {
        return this == CONTENT_MANAGER || this == ADMIN || this == SUPER_ADMIN;
    }

    /**
     * Returns true if this role has premium or higher access.
     */
    public boolean isPremiumOrHigher() {
        return this == PREMIUM_STUDENT || this == MENTOR
                || this == HR_MANAGER || isAdminLevel();
    }
}
