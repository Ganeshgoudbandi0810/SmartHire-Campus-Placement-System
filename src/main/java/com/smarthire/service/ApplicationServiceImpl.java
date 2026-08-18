package com.smarthire.service;

import com.smarthire.dao.*;
import com.smarthire.exception.ValidationException;
import com.smarthire.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * ApplicationServiceImpl
 *
 * Implements business operations for candidate placement drive applications,
 * eligibility enforcement, and selection outcomes.
 */
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final JobApplicationDAO applicationDAO;
    private final StudentDAO studentDAO;
    private final JobDAO jobDAO;
    private final EligibilityEngine eligibilityEngine;

    public ApplicationServiceImpl() {
        this.applicationDAO = new JobApplicationDAOImpl();
        this.studentDAO = new StudentDAOImpl();
        this.jobDAO = new JobDAOImpl();
        this.eligibilityEngine = new EligibilityEngine();
    }

    public ApplicationServiceImpl(JobApplicationDAO applicationDAO, StudentDAO studentDAO, JobDAO jobDAO) {
        this.applicationDAO = applicationDAO;
        this.studentDAO = studentDAO;
        this.jobDAO = jobDAO;
        this.eligibilityEngine = new EligibilityEngine();
    }

    @Override
    public EligibilityResult evaluateEligibility(StudentProfile student, JobPosting job) {
        return eligibilityEngine.evaluate(student, job);
    }

    @Override
    public JobApplication applyForJob(int studentId, int jobId) throws ValidationException {
        // 1. Retrieve Student Profile
        StudentProfile student = studentDAO.findById(studentId)
                .orElseThrow(() -> new ValidationException("Candidate profile record not found."));

        // 2. Retrieve Job Drive Details
        JobPosting job = jobDAO.findById(jobId)
                .orElseThrow(() -> new ValidationException("Placement drive not found."));

        // 3. Check for Duplicate Application
        Optional<JobApplication> existingApp = applicationDAO.findByJobAndStudent(jobId, studentId);
        if (existingApp.isPresent()) {
            throw new ValidationException("You have already submitted an application for this placement drive.");
        }

        // 4. Execute Automated Eligibility Verification
        EligibilityResult eligibility = eligibilityEngine.evaluate(student, job);
        if (!eligibility.isEligible()) {
            logger.warn("Eligibility rejected for student {} on job {}: {}", studentId, jobId, eligibility.getPrimaryReason());
            throw new ValidationException("Eligibility criteria not met: " + eligibility.getPrimaryReason());
        }

        // 5. Persist Application
        JobApplication application = new JobApplication();
        application.setJobId(jobId);
        application.setStudentId(studentId);
        application.setCurrentStatus(ApplicationStatus.APPLIED);

        JobApplication saved = applicationDAO.save(application);
        logger.info("Successfully registered application ID {} for student {} to job {}", saved.getId(), studentId, jobId);
        return saved;
    }

    @Override
    public List<JobApplication> getStudentApplications(int studentId) {
        return applicationDAO.findByStudentId(studentId);
    }

    @Override
    public List<JobApplication> getJobApplicants(int jobId) {
        return applicationDAO.findByJobId(jobId);
    }

    @Override
    public boolean hasStudentApplied(int jobId, int studentId) {
        return applicationDAO.findByJobAndStudent(jobId, studentId).isPresent();
    }

    @Override
    public void updateApplicationStatus(int applicationId, ApplicationStatus status, String rejectionReason) {
        applicationDAO.updateStatus(applicationId, status, rejectionReason);
        logger.info("Updated status for application ID {} to {}", applicationId, status);
    }

    @Override
    public int countStudentApplications(int studentId) {
        return applicationDAO.countByStudentId(studentId);
    }
}
