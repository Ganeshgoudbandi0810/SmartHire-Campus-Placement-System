package com.smarthire.model;

/**
 * RoundStatus
 *
 * Defines the operational execution state of an interview round.
 */
public enum RoundStatus {
    SCHEDULED("Scheduled"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    RoundStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoundStatus fromString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return SCHEDULED;
        }
        for (RoundStatus s : RoundStatus.values()) {
            if (s.name().equalsIgnoreCase(str.trim())) {
                return s;
            }
        }
        return SCHEDULED;
    }
}
