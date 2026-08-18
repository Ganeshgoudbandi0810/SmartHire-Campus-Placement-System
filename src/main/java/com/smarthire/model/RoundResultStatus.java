package com.smarthire.model;

/**
 * RoundResultStatus
 *
 * Defines the evaluation outcome for a specific candidate in a selection round.
 */
public enum RoundResultStatus {
    PENDING("Evaluation Pending"),
    QUALIFIED("Qualified / Advanced"),
    DISQUALIFIED("Not Selected"),
    ABSENT("Absent");

    private final String displayName;

    RoundResultStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoundResultStatus fromString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return PENDING;
        }
        for (RoundResultStatus s : RoundResultStatus.values()) {
            if (s.name().equalsIgnoreCase(str.trim())) {
                return s;
            }
        }
        return PENDING;
    }
}
