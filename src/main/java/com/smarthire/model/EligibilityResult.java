package com.smarthire.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * EligibilityResult
 *
 * Value Object DTO encapsulating the automated decision and detailed reasoning
 * produced by the EligibilityEngine.
 */
public class EligibilityResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean eligible;
    private final List<String> passedCriteria = new ArrayList<>();
    private final List<String> failedReasons = new ArrayList<>();

    public EligibilityResult(boolean eligible) {
        this.eligible = eligible;
    }

    public boolean isEligible() {
        return eligible;
    }

    public List<String> getPassedCriteria() {
        return passedCriteria;
    }

    public List<String> getFailedReasons() {
        return failedReasons;
    }

    public void addPassed(String criteria) {
        this.passedCriteria.add(criteria);
    }

    public void addFailed(String reason) {
        this.failedReasons.add(reason);
    }

    public String getPrimaryReason() {
        if (eligible) {
            return "All academic cutoffs and criteria met successfully.";
        }
        return !failedReasons.isEmpty() ? failedReasons.get(0) : "Criteria not satisfied.";
    }

    public static EligibilityResult success() {
        EligibilityResult res = new EligibilityResult(true);
        res.addPassed("Profile meets all required criteria.");
        return res;
    }

    public static EligibilityResult failure(String reason) {
        EligibilityResult res = new EligibilityResult(false);
        res.addFailed(reason);
        return res;
    }
}
