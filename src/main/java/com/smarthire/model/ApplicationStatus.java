package com.smarthire.model;

/**
 * ApplicationStatus
 *
 * Defines the recruitment evaluation state of a student's job application.
 */
public enum ApplicationStatus {
    APPLIED("Applied / Under Review"),
    SHORTLISTED("Shortlisted for Assessment"),
    IN_PROCESS("Interview in Progress"),
    REJECTED("Not Selected"),
    OFFERED("Offer Released"),
    ACCEPTED("Offer Accepted"),
    DECLINED("Offer Declined");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ApplicationStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return APPLIED;
        }
        for (ApplicationStatus s : ApplicationStatus.values()) {
            if (s.name().equalsIgnoreCase(statusStr.trim())) {
                return s;
            }
        }
        return APPLIED;
    }
}
