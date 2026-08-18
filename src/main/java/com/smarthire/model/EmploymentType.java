package com.smarthire.model;

/**
 * EmploymentType
 *
 * Defines the nature of employment offered in a campus placement drive.
 */
public enum EmploymentType {
    FULL_TIME("Full Time (FTE)"),
    INTERNSHIP("Internship Only"),
    INTERN_TO_FTE("Internship to Full-Time");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EmploymentType fromString(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return FULL_TIME;
        }
        for (EmploymentType t : EmploymentType.values()) {
            if (t.name().equalsIgnoreCase(typeStr.trim())) {
                return t;
            }
        }
        return FULL_TIME;
    }
}
