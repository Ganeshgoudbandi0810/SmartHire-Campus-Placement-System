package com.smarthire.model;

/**
 * RoundType
 *
 * Defines the nature and format of a recruitment selection stage.
 */
public enum RoundType {
    ONLINE_ASSESSMENT("Online Assessment / Aptitude"),
    CODING_TEST("Technical Coding Challenge"),
    TECHNICAL_INTERVIEW_1("Technical Interview 1"),
    TECHNICAL_INTERVIEW_2("Technical Interview 2"),
    HR_INTERVIEW("HR & Culture Fit Interview"),
    GROUP_DISCUSSION("Group Discussion (GD)"),
    OTHER("Other Assessment");

    private final String displayName;

    RoundType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoundType fromString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return TECHNICAL_INTERVIEW_1;
        }
        for (RoundType t : RoundType.values()) {
            if (t.name().equalsIgnoreCase(str.trim())) {
                return t;
            }
        }
        return TECHNICAL_INTERVIEW_1;
    }
}
