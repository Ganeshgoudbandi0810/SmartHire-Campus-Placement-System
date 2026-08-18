package com.smarthire.service;

import com.smarthire.model.EligibilityResult;
import com.smarthire.model.JobPosting;
import com.smarthire.model.JobStatus;
import com.smarthire.model.StudentProfile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * EligibilityEngine
 *
 * Core algorithmic business engine that evaluates candidate eligibility against
 * multi-dimensional placement drive criteria.
 */
public class EligibilityEngine {

    /**
     * Evaluates whether a student satisfies all academic and policy cutoffs for a placement drive.
     *
     * @param student Candidate's academic profile
     * @param job     Placement drive listing with criteria
     * @return EligibilityResult containing the boolean decision and detailed diagnostic reasons
     */
    public EligibilityResult evaluate(StudentProfile student, JobPosting job) {
        if (student == null) {
            return EligibilityResult.failure("Candidate academic profile record is missing.");
        }
        if (job == null) {
            return EligibilityResult.failure("Placement drive details are missing.");
        }

        EligibilityResult result = new EligibilityResult(true);

        // 1. Check Profile Completeness (Min 80% & Resume presence)
        if (student.getCompletionPercentage() < 80) {
            result.addFailed("Profile is only " + student.getCompletionPercentage() + "% complete. Minimum 80% completeness required.");
        } else {
            result.addPassed("Profile completeness: " + student.getCompletionPercentage() + "%");
        }

        if (student.getResumeFilePath() == null || student.getResumeFilePath().trim().isEmpty()) {
            result.addFailed("No PDF resume document uploaded. Please attach your resume.");
        } else {
            result.addPassed("Resume attached");
        }

        // 2. Check Drive Availability & Deadline
        if (job.getStatus() != JobStatus.OPEN) {
            result.addFailed("Placement drive is currently " + job.getStatus().getDisplayName() + ".");
        }

        if (job.isDeadlinePassed()) {
            result.addFailed("Application deadline has passed (" + job.getApplicationDeadline() + ").");
        }

        // 3. Evaluate CGPA Cutoff
        if (student.getCgpa().compareTo(job.getMinCgpa()) < 0) {
            result.addFailed("Minimum CGPA required is " + job.getMinCgpa() + " (Your CGPA: " + student.getCgpa() + ")");
        } else {
            result.addPassed("CGPA cutoff met (" + student.getCgpa() + " >= " + job.getMinCgpa() + ")");
        }

        // 4. Evaluate Active Backlogs Limit
        if (student.getActiveBacklogs() > job.getMaxBacklogsAllowed()) {
            result.addFailed("Maximum allowed active backlogs is " + job.getMaxBacklogsAllowed() + " (You have " + student.getActiveBacklogs() + ")");
        } else {
            result.addPassed("Backlog criteria met (" + student.getActiveBacklogs() + " active)");
        }

        // 5. Evaluate Department / Branch Eligibility
        String eligibleBranches = job.getEligibleBranches();
        if (eligibleBranches != null && !eligibleBranches.equalsIgnoreCase("ALL")) {
            List<String> allowedList = Arrays.stream(eligibleBranches.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .toList();

            String studentDept = student.getDepartment() != null ? student.getDepartment().trim().toUpperCase() : "";
            if (!allowedList.contains(studentDept)) {
                result.addFailed("Open to " + eligibleBranches + " branches only (Your branch: " + student.getDepartment() + ")");
            } else {
                result.addPassed("Department eligible (" + student.getDepartment() + ")");
            }
        } else {
            result.addPassed("Open to all engineering branches");
        }

        // 6. Evaluate 10th Standard Percentage
        if (job.getMinTenthPercentage().compareTo(BigDecimal.ZERO) > 0) {
            if (student.getTenthPercentage().compareTo(job.getMinTenthPercentage()) < 0) {
                result.addFailed("Min 10th marks required: " + job.getMinTenthPercentage() + "% (Your marks: " + student.getTenthPercentage() + "%)");
            } else {
                result.addPassed("10th percentage criteria met");
            }
        }

        // 7. Evaluate 12th / Diploma Percentage
        if (job.getMinTwelfthPercentage().compareTo(BigDecimal.ZERO) > 0) {
            if (student.getTwelfthPercentage().compareTo(job.getMinTwelfthPercentage()) < 0) {
                result.addFailed("Min 12th marks required: " + job.getMinTwelfthPercentage() + "% (Your marks: " + student.getTwelfthPercentage() + "%)");
            } else {
                result.addPassed("12th percentage criteria met");
            }
        }

        // 8. Evaluate Target Graduation Year Batch
        if (job.getGraduationYear() > 0 && student.getGraduationYear() != job.getGraduationYear()) {
            result.addFailed("Targeted at " + job.getGraduationYear() + " batch only (Your batch: " + student.getGraduationYear() + ")");
        } else {
            result.addPassed("Graduation batch matched (" + student.getGraduationYear() + ")");
        }

        // If any failure occurred, marked as ineligible
        boolean finalEligible = result.getFailedReasons().isEmpty();
        return new EligibilityResultWrapper(finalEligible, result.getPassedCriteria(), result.getFailedReasons());
    }

    /**
     * Inner helper to preserve immutability and precise decision results.
     */
    private static class EligibilityResultWrapper extends EligibilityResult {
        public EligibilityResultWrapper(boolean eligible, List<String> passed, List<String> failed) {
            super(eligible);
            this.getPassedCriteria().addAll(passed);
            this.getFailedReasons().addAll(failed);
        }
    }
}
