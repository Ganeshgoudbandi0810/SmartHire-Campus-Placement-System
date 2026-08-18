package com.smarthire.model;

/**
 * JobStatus
 *
 * Defines the lifecycle state of a campus recruitment drive.
 */
public enum JobStatus {
    DRAFT("Draft"),
    OPEN("Open for Applications"),
    CLOSED("Applications Closed"),
    COMPLETED("Drive Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    JobStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static JobStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return OPEN;
        }
        for (JobStatus s : JobStatus.values()) {
            if (s.name().equalsIgnoreCase(statusStr.trim())) {
                return s;
            }
        }
        return OPEN;
    }
}
