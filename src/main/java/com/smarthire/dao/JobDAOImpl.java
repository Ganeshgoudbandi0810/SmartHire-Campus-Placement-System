package com.smarthire.dao;

import com.smarthire.model.EmploymentType;
import com.smarthire.model.JobPosting;
import com.smarthire.model.JobStatus;
import com.smarthire.util.DBConnectionManager;
import com.smarthire.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JobDAOImpl
 *
 * JDBC implementation of JobDAO using PreparedStatement and HikariCP pool.
 */
public class JobDAOImpl implements JobDAO {

    private static final Logger logger = LoggerFactory.getLogger(JobDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT j.*, c.company_name, c.website AS company_website, c.industry AS company_industry, " +
            "(SELECT COUNT(*) FROM job_applications ja WHERE ja.job_id = j.id) AS applicant_count " +
            "FROM job_postings j " +
            "INNER JOIN companies c ON j.company_id = c.id ";

    private static final String SQL_INSERT =
            "INSERT INTO job_postings (company_id, job_title, job_description, job_location, employment_type, " +
            "package_lpa, stipend_monthly, min_cgpa, min_tenth_percentage, min_twelfth_percentage, " +
            "max_backlogs_allowed, eligible_branches, graduation_year, application_deadline, drive_date, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE job_postings SET job_title = ?, job_description = ?, job_location = ?, employment_type = ?, " +
            "package_lpa = ?, stipend_monthly = ?, min_cgpa = ?, min_tenth_percentage = ?, min_twelfth_percentage = ?, " +
            "max_backlogs_allowed = ?, eligible_branches = ?, graduation_year = ?, application_deadline = ?, " +
            "drive_date = ?, status = ? WHERE id = ?";

    private static final String SQL_FIND_BY_ID = SELECT_BASE + "WHERE j.id = ?";
    private static final String SQL_FIND_ALL = SELECT_BASE + "ORDER BY j.id DESC";
    private static final String SQL_FIND_BY_COMPANY = SELECT_BASE + "WHERE j.company_id = ? ORDER BY j.id DESC";
    private static final String SQL_FIND_OPEN = SELECT_BASE + "WHERE j.status = 'OPEN' AND j.application_deadline > CURRENT_TIMESTAMP ORDER BY j.application_deadline ASC";
    private static final String SQL_UPDATE_STATUS = "UPDATE job_postings SET status = ? WHERE id = ?";
    private static final String SQL_COUNT_ACTIVE = "SELECT COUNT(*) FROM job_postings WHERE status = 'OPEN' AND application_deadline > CURRENT_TIMESTAMP";

    @Override
    public JobPosting save(JobPosting job) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, job.getCompanyId());
            stmt.setString(2, job.getJobTitle().trim());
            stmt.setString(3, job.getJobDescription());
            stmt.setString(4, job.getJobLocation());
            stmt.setString(5, job.getEmploymentType().name());
            stmt.setBigDecimal(6, job.getPackageLpa());
            stmt.setBigDecimal(7, job.getStipendMonthly());
            stmt.setBigDecimal(8, job.getMinCgpa());
            stmt.setBigDecimal(9, job.getMinTenthPercentage());
            stmt.setBigDecimal(10, job.getMinTwelfthPercentage());
            stmt.setInt(11, job.getMaxBacklogsAllowed());
            stmt.setString(12, job.getEligibleBranches());
            stmt.setInt(13, job.getGraduationYear());
            stmt.setTimestamp(14, Timestamp.valueOf(job.getApplicationDeadline()));
            stmt.setTimestamp(15, Timestamp.valueOf(job.getDriveDate()));
            stmt.setString(16, job.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating job drive failed, no rows affected.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                job.setId(generatedKeys.getInt(1));
            }
            logger.info("Job drive created with ID: {} for company ID: {}", job.getId(), job.getCompanyId());
            return job;

        } catch (SQLException e) {
            logger.error("Error saving job drive: {}", e.getMessage(), e);
            throw new RuntimeException("Database error saving job drive: " + e.getMessage(), e);
        } finally {
            DBUtil.close(generatedKeys, stmt, conn);
        }
    }

    @Override
    public boolean update(JobPosting job) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, job.getJobTitle().trim());
            stmt.setString(2, job.getJobDescription());
            stmt.setString(3, job.getJobLocation());
            stmt.setString(4, job.getEmploymentType().name());
            stmt.setBigDecimal(5, job.getPackageLpa());
            stmt.setBigDecimal(6, job.getStipendMonthly());
            stmt.setBigDecimal(7, job.getMinCgpa());
            stmt.setBigDecimal(8, job.getMinTenthPercentage());
            stmt.setBigDecimal(9, job.getMinTwelfthPercentage());
            stmt.setInt(10, job.getMaxBacklogsAllowed());
            stmt.setString(11, job.getEligibleBranches());
            stmt.setInt(12, job.getGraduationYear());
            stmt.setTimestamp(13, Timestamp.valueOf(job.getApplicationDeadline()));
            stmt.setTimestamp(14, Timestamp.valueOf(job.getDriveDate()));
            stmt.setString(15, job.getStatus().name());
            stmt.setInt(16, job.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating job drive ID {}: {}", job.getId(), e.getMessage(), e);
            throw new RuntimeException("Database error updating job drive: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public Optional<JobPosting> findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_ID);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToJob(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding job drive by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding job drive: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public List<JobPosting> findAll() {
        List<JobPosting> jobs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            return jobs;

        } catch (SQLException e) {
            logger.error("Error retrieving all job drives: {}", e.getMessage(), e);
            throw new RuntimeException("Database error retrieving job drives: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public List<JobPosting> findByCompanyId(int companyId) {
        List<JobPosting> jobs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_COMPANY);
            stmt.setInt(1, companyId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            return jobs;

        } catch (SQLException e) {
            logger.error("Error finding job drives for company ID {}: {}", companyId, e.getMessage(), e);
            throw new RuntimeException("Database error finding company job drives: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public List<JobPosting> findOpenJobs() {
        List<JobPosting> jobs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_OPEN);
            rs = stmt.executeQuery();

            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            return jobs;

        } catch (SQLException e) {
            logger.error("Error finding open job drives: {}", e.getMessage(), e);
            throw new RuntimeException("Database error finding open drives: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean updateStatus(int jobId, JobStatus status) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_STATUS);
            stmt.setString(1, status.name());
            stmt.setInt(2, jobId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating status for job ID {}: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Database error updating job status: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public int countActiveJobs() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL_COUNT_ACTIVE);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting active job drives: {}", e.getMessage(), e);
            return 0;
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    private JobPosting mapResultSetToJob(ResultSet rs) throws SQLException {
        JobPosting j = new JobPosting();
        j.setId(rs.getInt("id"));
        j.setCompanyId(rs.getInt("company_id"));
        j.setJobTitle(rs.getString("job_title"));
        j.setJobDescription(rs.getString("job_description"));
        j.setJobLocation(rs.getString("job_location"));
        j.setEmploymentType(EmploymentType.fromString(rs.getString("employment_type")));
        j.setPackageLpa(rs.getBigDecimal("package_lpa"));
        j.setStipendMonthly(rs.getBigDecimal("stipend_monthly"));
        j.setMinCgpa(rs.getBigDecimal("min_cgpa"));
        j.setMinTenthPercentage(rs.getBigDecimal("min_tenth_percentage"));
        j.setMinTwelfthPercentage(rs.getBigDecimal("min_twelfth_percentage"));
        j.setMaxBacklogsAllowed(rs.getInt("max_backlogs_allowed"));
        j.setEligibleBranches(rs.getString("eligible_branches"));
        j.setGraduationYear(rs.getInt("graduation_year"));

        Timestamp deadline = rs.getTimestamp("application_deadline");
        if (deadline != null) {
            j.setApplicationDeadline(deadline.toLocalDateTime());
        }

        Timestamp driveDate = rs.getTimestamp("drive_date");
        if (driveDate != null) {
            j.setDriveDate(driveDate.toLocalDateTime());
        }

        j.setStatus(JobStatus.fromString(rs.getString("status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            j.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            j.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        try {
            j.setCompanyName(rs.getString("company_name"));
            j.setCompanyWebsite(rs.getString("company_website"));
            j.setCompanyIndustry(rs.getString("company_industry"));
            j.setApplicantCount(rs.getInt("applicant_count"));
        } catch (SQLException ignored) {
        }

        return j;
    }
}
