package com.smarthire.dao;

import com.smarthire.model.ApplicationStatus;
import com.smarthire.model.JobApplication;

import java.util.List;
import java.util.Optional;

/**
 * JobApplicationDAO
 *
 * Data Access Object interface for managing student placement drive applications.
 */
public interface JobApplicationDAO {

    /**
     * Persists a new job application submission.
     */
    JobApplication save(JobApplication application);

    /**
     * Finds an application by composite unique key (jobId + studentId).
     */
    Optional<JobApplication> findByJobAndStudent(int jobId, int studentId);

    /**
     * Finds an application by its primary key ID.
     */
    Optional<JobApplication> findById(int id);

    /**
     * Retrieves all applications submitted by a specific student with drive details.
     */
    List<JobApplication> findByStudentId(int studentId);

    /**
     * Retrieves all candidate submissions for a specific placement drive.
     */
    List<JobApplication> findByJobId(int jobId);

    /**
     * Updates candidate evaluation status (APPLIED, SHORTLISTED, REJECTED, OFFERED).
     */
    boolean updateStatus(int applicationId, ApplicationStatus status, String rejectionReason);

    /**
     * Counts how many drives a student has applied for.
     */
    int countByStudentId(int studentId);

    /**
     * Counts total applicants for a job.
     */
    int countByJobId(int jobId);
}
