package com.smarthire.service;

import com.smarthire.model.Company;

import java.util.List;

/**
 * CompanyService
 *
 * Business service interface governing corporate partner onboarding and verification.
 */
public interface CompanyService {

    /**
     * Retrieves company profile by recruiter's user ID, initializing if missing.
     */
    Company getCompanyByUserId(int userId);

    /**
     * Retrieves company by primary key ID.
     */
    Company getCompanyById(int id);

    /**
     * Updates company profile information.
     */
    void updateCompany(Company company);

    /**
     * Retrieves all partner companies for TPO administration.
     */
    List<Company> getAllCompanies();

    /**
     * Approves/verifies or revokes company standing.
     */
    void verifyCompany(int companyId, boolean isVerified);

    /**
     * Counts the total number of partner companies.
     */
    int countTotalCompanies();
}
