package com.smarthire.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JobPosting
 *
 * Domain Model POJO representing a campus placement drive listing and its eligibility cutoffs.
 */
public class JobPosting implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int companyId;
    private String jobTitle;
    private String jobDescription;
    private String jobLocation;
    private EmploymentType employmentType = EmploymentType.FULL_TIME;
    private BigDecimal packageLpa = BigDecimal.ZERO;
    private BigDecimal stipendMonthly = BigDecimal.ZERO;

    // Standardized Eligibility Criteria
    private BigDecimal minCgpa = new BigDecimal("6.00");
    private BigDecimal minTenthPercentage = new BigDecimal("60.00");
    private BigDecimal minTwelfthPercentage = new BigDecimal("60.00");
    private int maxBacklogsAllowed = 0;
    private String eligibleBranches = "ALL"; // e.g. 'CSE,IT,ECE' or 'ALL'
    private int graduationYear = 2026;

    private LocalDateTime applicationDeadline;
    private LocalDateTime driveDate;
    private JobStatus status = JobStatus.OPEN;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient fields populated via SQL joins for views
    private String companyName;
    private String companyWebsite;
    private String companyIndustry;
    private int applicantCount = 0;

    public JobPosting() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public EmploymentType getEmploymentType() {
        return employmentType != null ? employmentType : EmploymentType.FULL_TIME;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public BigDecimal getPackageLpa() {
        return packageLpa != null ? packageLpa : BigDecimal.ZERO;
    }

    public void setPackageLpa(BigDecimal packageLpa) {
        this.packageLpa = packageLpa;
    }

    public BigDecimal getStipendMonthly() {
        return stipendMonthly != null ? stipendMonthly : BigDecimal.ZERO;
    }

    public void setStipendMonthly(BigDecimal stipendMonthly) {
        this.stipendMonthly = stipendMonthly;
    }

    public BigDecimal getMinCgpa() {
        return minCgpa != null ? minCgpa : BigDecimal.ZERO;
    }

    public void setMinCgpa(BigDecimal minCgpa) {
        this.minCgpa = minCgpa;
    }

    public BigDecimal getMinTenthPercentage() {
        return minTenthPercentage != null ? minTenthPercentage : BigDecimal.ZERO;
    }

    public void setMinTenthPercentage(BigDecimal minTenthPercentage) {
        this.minTenthPercentage = minTenthPercentage;
    }

    public BigDecimal getMinTwelfthPercentage() {
        return minTwelfthPercentage != null ? minTwelfthPercentage : BigDecimal.ZERO;
    }

    public void setMinTwelfthPercentage(BigDecimal minTwelfthPercentage) {
        this.minTwelfthPercentage = minTwelfthPercentage;
    }

    public int getMaxBacklogsAllowed() {
        return maxBacklogsAllowed;
    }

    public void setMaxBacklogsAllowed(int maxBacklogsAllowed) {
        this.maxBacklogsAllowed = maxBacklogsAllowed;
    }

    public String getEligibleBranches() {
        return eligibleBranches != null ? eligibleBranches : "ALL";
    }

    public void setEligibleBranches(String eligibleBranches) {
        this.eligibleBranches = eligibleBranches;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public LocalDateTime getDriveDate() {
        return driveDate;
    }

    public void setDriveDate(LocalDateTime driveDate) {
        this.driveDate = driveDate;
    }

    public JobStatus getStatus() {
        return status != null ? status : JobStatus.OPEN;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyIndustry() {
        return companyIndustry;
    }

    public void setCompanyIndustry(String companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    public int getApplicantCount() {
        return applicantCount;
    }

    public void setApplicantCount(int applicantCount) {
        this.applicantCount = applicantCount;
    }

    public boolean isDeadlinePassed() {
        return applicationDeadline != null && LocalDateTime.now().isAfter(applicationDeadline);
    }

    public boolean isOpenForApplication() {
        return status == JobStatus.OPEN && !isDeadlinePassed();
    }
}
