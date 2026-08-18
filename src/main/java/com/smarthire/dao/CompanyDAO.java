package com.smarthire.dao;

import com.smarthire.model.Company;

import java.util.List;
import java.util.Optional;

/**
 * CompanyDAO
 *
 * Data Access Object interface for managing partner companies and corporate recruiters.
 */
public interface CompanyDAO {

    /**
     * Persists a new company record and assigns its generated primary key ID.
     */
    Company save(Company company);

    /**
     * Updates company profile details.
     */
    boolean update(Company company);

    /**
     * Finds a company by the recruiter's user ID.
     */
    Optional<Company> findByUserId(int userId);

    /**
     * Finds a company by its primary key ID.
     */
    Optional<Company> findById(int id);

    /**
     * Updates company verification status by TPO.
     */
    boolean updateVerificationStatus(int companyId, boolean isVerified);

    /**
     * Retrieves all companies for TPO administration.
     */
    List<Company> findAll();

    /**
     * Counts the total number of verified partner companies.
     */
    int countTotalCompanies();
}
