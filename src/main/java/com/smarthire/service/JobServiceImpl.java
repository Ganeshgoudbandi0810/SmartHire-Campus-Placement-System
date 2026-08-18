package com.smarthire.service;

import com.smarthire.dao.JobDAO;
import com.smarthire.dao.JobDAOImpl;
import com.smarthire.exception.ValidationException;
import com.smarthire.model.JobPosting;
import com.smarthire.model.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JobServiceImpl
 *
 * Implements business validation and lifecycle operations for campus placement drives.
 */
public class JobServiceImpl implements JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
    private final JobDAO jobDAO;

    public JobServiceImpl() {
        this.jobDAO = new JobDAOImpl();
    }

    public JobServiceImpl(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

    @Override
    public JobPosting createJobDrive(JobPosting job) throws ValidationException {
        validateJobPosting(job);
        job.setStatus(JobStatus.OPEN);
        return jobDAO.save(job);
    }

    @Override
    public void updateJobDrive(JobPosting job) throws ValidationException {
        if (job == null || job.getId() <= 0) {
            throw new ValidationException("Invalid job drive ID for update.");
        }
        validateJobPosting(job);
        jobDAO.update(job);
        logger.info("Updated placement drive ID: {}", job.getId());
    }

    @Override
    public JobPosting getJobById(int id) {
        return jobDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Placement drive not found with ID: " + id));
    }

    @Override
    public List<JobPosting> getAllJobs() {
        return jobDAO.findAll();
    }

    @Override
    public List<JobPosting> getJobsByCompany(int companyId) {
        return jobDAO.findByCompanyId(companyId);
    }

    @Override
    public List<JobPosting> getOpenJobs() {
        return jobDAO.findOpenJobs();
    }

    @Override
    public void setJobStatus(int jobId, JobStatus status) {
        jobDAO.updateStatus(jobId, status);
        logger.info("Updated status of job ID {} to: {}", jobId, status);
    }

    @Override
    public int countActiveJobs() {
        return jobDAO.countActiveJobs();
    }

    /**
     * Validates domain constraints on salary, criteria cutoffs, and drive scheduling.
     */
    private void validateJobPosting(JobPosting job) throws ValidationException {
        if (job == null) {
            throw new ValidationException("Job posting payload cannot be null.");
        }

        if (job.getJobTitle() == null || job.getJobTitle().trim().isEmpty()) {
            throw new ValidationException("Job Title is required.");
        }

        if (job.getPackageLpa() == null || job.getPackageLpa().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Package (LPA) must be greater than 0.00.");
        }

        // CGPA Cutoff validation
        if (job.getMinCgpa() == null || 
            job.getMinCgpa().compareTo(BigDecimal.ZERO) < 0 || 
            job.getMinCgpa().compareTo(new BigDecimal("10.00")) > 0) {
            throw new ValidationException("Minimum CGPA criteria must be between 0.00 and 10.00.");
        }

        // Backlog Threshold validation
        if (job.getMaxBacklogsAllowed() < 0) {
            throw new ValidationException("Maximum backlogs threshold cannot be negative.");
        }

        // Deadline validation
        if (job.getApplicationDeadline() == null) {
            throw new ValidationException("Application deadline is required.");
        }

        if (job.getApplicationDeadline().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new ValidationException("Application deadline must be set in the future.");
        }

        if (job.getDriveDate() == null) {
            job.setDriveDate(job.getApplicationDeadline().plusDays(3));
        }

        if (job.getEligibleBranches() == null || job.getEligibleBranches().trim().isEmpty()) {
            job.setEligibleBranches("ALL");
        }
    }
}
