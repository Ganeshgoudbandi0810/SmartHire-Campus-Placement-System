package com.smarthire.model;

/**
 * UserStatus
 *
 * Defines the account lifecycle states for a user.
 */
public enum UserStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PENDING_APPROVAL("Pending Approval");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return null;
        }
        for (UserStatus status : UserStatus.values()) {
            if (status.name().equalsIgnoreCase(statusStr.trim())) {
                return status;
            }
        }
        return null;
    }
}
