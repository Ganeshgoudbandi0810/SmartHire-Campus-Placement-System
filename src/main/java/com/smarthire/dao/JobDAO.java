package com.smarthire.dao;

import com.smarthire.model.JobPosting;
import com.smarthire.model.JobStatus;

import java.util.List;
import java.util.Optional;

/**
 * JobDAO
 *
 * Data Access Object interface for campus placement drives and job postings.
 */
public interface JobDAO {

    /**
     * Persists a new placement drive and populates its auto-generated primary key ID.
     */
    JobPosting save(JobPosting job);

    /**
     * Updates an existing placement drive's details and eligibility criteria.
     */
    boolean update(JobPosting job);

    /**
     * Retrieves a placement drive by its primary key ID with company details.
     */
    Optional<JobPosting> findById(int id);

    /**
     * Retrieves all placement drives (for TPO administration).
     */
    List<JobPosting> findAll();

    /**
     * Retrieves all drives posted by a specific company.
     */
    List<JobPosting> findByCompanyId(int companyId);

    /**
     * Retrieves all currently active and OPEN placement drives.
     */
    List<JobPosting> findOpenJobs();

    /**
     * Updates the lifecycle status of a drive (OPEN, CLOSED, COMPLETED, CANCELLED).
     */
    boolean updateStatus(int jobId, JobStatus status);

    /**
     * Counts the total number of OPEN placement drives.
     */
    int countActiveJobs();
}
