package com.smarthire.service;

import com.smarthire.dao.CompanyDAO;
import com.smarthire.dao.CompanyDAOImpl;
import com.smarthire.model.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * CompanyServiceImpl
 *
 * Implements business logic for managing corporate recruiter organizations.
 */
public class CompanyServiceImpl implements CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);
    private final CompanyDAO companyDAO;

    public CompanyServiceImpl() {
        this.companyDAO = new CompanyDAOImpl();
    }

    public CompanyServiceImpl(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    @Override
    public Company getCompanyByUserId(int userId) {
        Optional<Company> companyOpt = companyDAO.findByUserId(userId);
        if (companyOpt.isPresent()) {
            return companyOpt.get();
        }

        // Initialize default company placeholder
        logger.info("Initializing company profile for user ID: {}", userId);
        Company c = new Company();
        c.setUserId(userId);
        c.setCompanyName("New Hiring Organization");
        c.setIndustry("Technology & Services");
        c.setVerified(true);

        return companyDAO.save(c);
    }

    @Override
    public Company getCompanyById(int id) {
        return companyDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Company record not found with ID: " + id));
    }

    @Override
    public void updateCompany(Company company) {
        if (company == null || company.getCompanyName() == null || company.getCompanyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be blank.");
        }
        companyDAO.update(company);
        logger.info("Updated company details for ID: {}", company.getId());
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyDAO.findAll();
    }

    @Override
    public void verifyCompany(int companyId, boolean isVerified) {
        companyDAO.updateVerificationStatus(companyId, isVerified);
        logger.info("TPO updated verification status for company ID {}: {}", companyId, isVerified);
    }

    @Override
    public int countTotalCompanies() {
        return companyDAO.countTotalCompanies();
    }
}
