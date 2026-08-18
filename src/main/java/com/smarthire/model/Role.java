package com.smarthire.model;

/**
 * Role
 *
 * Defines the user authorization roles in the SmartHire system.
 */
public enum Role {
    STUDENT("Student"),
    TPO_ADMIN("Placement Officer (TPO)"),
    RECRUITER("Corporate Recruiter");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            return null;
        }
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(roleStr.trim())) {
                return role;
            }
        }
        return null;
    }
}
