package com.smarthire.model;

import java.io.Serializable;

/**
 * JobDriveViewDTO
 *
 * Composite Data Transfer Object combining a JobPosting, its evaluated EligibilityResult
 * for the current student, and whether the student has already applied.
 */
public class JobDriveViewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final JobPosting job;
    private final EligibilityResult eligibility;
    private final boolean applied;

    public JobDriveViewDTO(JobPosting job, EligibilityResult eligibility, boolean applied) {
        this.job = job;
        this.eligibility = eligibility;
        this.applied = applied;
    }

    public JobPosting getJob() {
        return job;
    }

    public EligibilityResult getEligibility() {
        return eligibility;
    }

    public boolean isApplied() {
        return applied;
    }

    public boolean isEligibleToApply() {
        return eligibility.isEligible() && !applied && job.isOpenForApplication();
    }
}
