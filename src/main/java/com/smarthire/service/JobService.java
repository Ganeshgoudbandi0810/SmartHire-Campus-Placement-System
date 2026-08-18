package com.smarthire.service;

import com.smarthire.exception.ValidationException;
import com.smarthire.model.JobPosting;
import com.smarthire.model.JobStatus;

import java.util.List;

/**
 * JobService
 *
 * Business service interface governing placement drives, eligibility rules, and drive lifecycles.
 */
public interface JobService {

    /**
     * Validates eligibility parameters and creates a new placement drive.
     *
     * @throws ValidationException if criteria bounds or deadlines are invalid
     */
    JobPosting createJobDrive(JobPosting job) throws ValidationException;

    /**
     * Updates placement drive details.
     */
    void updateJobDrive(JobPosting job) throws ValidationException;

    /**
     * Retrieves a placement drive by its primary key ID.
     */
    JobPosting getJobById(int id);

    /**
     * Retrieves all placement drives across all organizations.
     */
    List<JobPosting> getAllJobs();

    /**
     * Retrieves drives posted by a specific company.
     */
    List<JobPosting> getJobsByCompany(int companyId);

    /**
     * Retrieves all currently OPEN drives that have not passed deadline.
     */
    List<JobPosting> getOpenJobs();

    /**
     * Transitions a drive's status (OPEN, CLOSED, COMPLETED, CANCELLED).
     */
    void setJobStatus(int jobId, JobStatus status);

    /**
     * Counts active open placement drives.
     */
    int countActiveJobs();
}
