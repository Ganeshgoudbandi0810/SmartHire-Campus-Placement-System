package com.smarthire.dao;

import com.smarthire.model.Company;
import com.smarthire.util.DBConnectionManager;
import com.smarthire.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CompanyDAOImpl
 *
 * JDBC implementation of CompanyDAO using PreparedStatement and HikariCP pool.
 */
public class CompanyDAOImpl implements CompanyDAO {

    private static final Logger logger = LoggerFactory.getLogger(CompanyDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO companies (user_id, company_name, industry, website, description, headquarters, " +
            "contact_person_name, contact_phone, is_verified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE companies SET company_name = ?, industry = ?, website = ?, description = ?, " +
            "headquarters = ?, contact_person_name = ?, contact_phone = ? WHERE id = ?";

    private static final String SQL_FIND_BY_USER_ID =
            "SELECT c.*, u.email AS contact_email FROM companies c " +
            "INNER JOIN users u ON c.user_id = u.id WHERE c.user_id = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT c.*, u.email AS contact_email FROM companies c " +
            "INNER JOIN users u ON c.user_id = u.id WHERE c.id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT c.*, u.email AS contact_email FROM companies c " +
            "INNER JOIN users u ON c.user_id = u.id ORDER BY c.id DESC";

    private static final String SQL_UPDATE_VERIFIED =
            "UPDATE companies SET is_verified = ? WHERE id = ?";

    private static final String SQL_COUNT =
            "SELECT COUNT(*) FROM companies";

    @Override
    public Company save(Company company) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, company.getUserId());
            stmt.setString(2, company.getCompanyName().trim());
            stmt.setString(3, company.getIndustry());
            stmt.setString(4, company.getWebsite());
            stmt.setString(5, company.getDescription());
            stmt.setString(6, company.getHeadquarters());
            stmt.setString(7, company.getContactPersonName());
            stmt.setString(8, company.getContactPhone());
            stmt.setBoolean(9, company.isVerified());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating company failed, no rows affected.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                company.setId(generatedKeys.getInt(1));
            }
            logger.info("Company saved with ID: {} for user ID: {}", company.getId(), company.getUserId());
            return company;

        } catch (SQLException e) {
            logger.error("Error saving company for user ID {}: {}", company.getUserId(), e.getMessage(), e);
            throw new RuntimeException("Database error saving company: " + e.getMessage(), e);
        } finally {
            DBUtil.close(generatedKeys, stmt, conn);
        }
    }

    @Override
    public boolean update(Company company) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, company.getCompanyName().trim());
            stmt.setString(2, company.getIndustry());
            stmt.setString(3, company.getWebsite());
            stmt.setString(4, company.getDescription());
            stmt.setString(5, company.getHeadquarters());
            stmt.setString(6, company.getContactPersonName());
            stmt.setString(7, company.getContactPhone());
            stmt.setInt(8, company.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating company ID {}: {}", company.getId(), e.getMessage(), e);
            throw new RuntimeException("Database error updating company: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public Optional<Company> findByUserId(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_USER_ID);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCompany(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding company by user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Database error finding company: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Company> findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_ID);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCompany(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding company by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding company: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean updateVerificationStatus(int companyId, boolean isVerified) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_VERIFIED);
            stmt.setBoolean(1, isVerified);
            stmt.setInt(2, companyId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating verification for company ID {}: {}", companyId, e.getMessage(), e);
            throw new RuntimeException("Database error updating company verification: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public List<Company> findAll() {
        List<Company> companies = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }
            return companies;

        } catch (SQLException e) {
            logger.error("Error retrieving all companies: {}", e.getMessage(), e);
            throw new RuntimeException("Database error retrieving companies: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public int countTotalCompanies() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL_COUNT);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting companies: {}", e.getMessage(), e);
            return 0;
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCompanyName(rs.getString("company_name"));
        c.setIndustry(rs.getString("industry"));
        c.setWebsite(rs.getString("website"));
        c.setDescription(rs.getString("description"));
        c.setHeadquarters(rs.getString("headquarters"));
        c.setContactPersonName(rs.getString("contact_person_name"));
        c.setContactPhone(rs.getString("contact_phone"));
        c.setVerified(rs.getBoolean("is_verified"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            c.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            c.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        try {
            c.setContactEmail(rs.getString("contact_email"));
        } catch (SQLException ignored) {
        }

        return c;
    }
}
