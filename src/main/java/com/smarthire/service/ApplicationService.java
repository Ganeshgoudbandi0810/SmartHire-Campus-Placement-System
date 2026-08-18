package com.smarthire.service;

import com.smarthire.exception.ValidationException;
import com.smarthire.model.*;

import java.util.List;

/**
 * ApplicationService
 *
 * Business service interface managing candidate applications, eligibility checks,
 * and status updates.
 */
public interface ApplicationService {

    /**
     * Evaluates a candidate's eligibility against a specific placement drive.
     */
    EligibilityResult evaluateEligibility(StudentProfile student, JobPosting job);

    /**
     * Processes a 1-click application submission with complete eligibility verification
     * and duplicate application prevention.
     *
     * @throws ValidationException if student is ineligible, profile incomplete, or already applied
     */
    JobApplication applyForJob(int studentId, int jobId) throws ValidationException;

    /**
     * Retrieves all applications submitted by a specific student.
     */
    List<JobApplication> getStudentApplications(int studentId);

    /**
     * Retrieves all candidate submissions for a specific placement drive.
     */
    List<JobApplication> getJobApplicants(int jobId);

    /**
     * Checks if a student has already applied for a specific job drive.
     */
    boolean hasStudentApplied(int jobId, int studentId);

    /**
     * Updates an application status (e.g. SHORTLISTED, REJECTED, OFFERED).
     */
    void updateApplicationStatus(int applicationId, ApplicationStatus status, String rejectionReason);

    /**
     * Counts how many drives a student has applied for.
     */
    int countStudentApplications(int studentId);
}
