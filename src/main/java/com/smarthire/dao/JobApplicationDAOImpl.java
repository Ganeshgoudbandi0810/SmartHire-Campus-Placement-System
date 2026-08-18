package com.smarthire.dao;

import com.smarthire.model.ApplicationStatus;
import com.smarthire.model.JobApplication;
import com.smarthire.util.DBConnectionManager;
import com.smarthire.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JobApplicationDAOImpl
 *
 * JDBC implementation of JobApplicationDAO using PreparedStatement and joins.
 */
public class JobApplicationDAOImpl implements JobApplicationDAO {

    private static final Logger logger = LoggerFactory.getLogger(JobApplicationDAOImpl.class);

    private static final String SELECT_JOINED =
            "SELECT ja.*, " +
            "j.job_title, j.package_lpa, j.job_location, " +
            "c.company_name, c.website AS company_website, " +
            "CONCAT(sp.first_name, ' ', sp.last_name) AS student_full_name, " +
            "sp.roll_number, sp.cgpa AS student_cgpa, sp.department AS student_department, " +
            "sp.resume_file_path, u.email AS student_email " +
            "FROM job_applications ja " +
            "INNER JOIN job_postings j ON ja.job_id = j.id " +
            "INNER JOIN companies c ON j.company_id = c.id " +
            "INNER JOIN student_profiles sp ON ja.student_id = sp.id " +
            "INNER JOIN users u ON sp.user_id = u.id ";

    private static final String SQL_INSERT =
            "INSERT INTO job_applications (job_id, student_id, current_status) VALUES (?, ?, ?)";

    private static final String SQL_FIND_BY_JOB_AND_STUDENT =
            SELECT_JOINED + "WHERE ja.job_id = ? AND ja.student_id = ?";

    private static final String SQL_FIND_BY_ID =
            SELECT_JOINED + "WHERE ja.id = ?";

    private static final String SQL_FIND_BY_STUDENT =
            SELECT_JOINED + "WHERE ja.student_id = ? ORDER BY ja.applied_at DESC";

    private static final String SQL_FIND_BY_JOB =
            SELECT_JOINED + "WHERE ja.job_id = ? ORDER BY ja.applied_at ASC";

    private static final String SQL_UPDATE_STATUS =
            "UPDATE job_applications SET current_status = ?, rejection_reason = ? WHERE id = ?";

    private static final String SQL_COUNT_BY_STUDENT =
            "SELECT COUNT(*) FROM job_applications WHERE student_id = ?";

    private static final String SQL_COUNT_BY_JOB =
            "SELECT COUNT(*) FROM job_applications WHERE job_id = ?";

    @Override
    public JobApplication save(JobApplication app) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, app.getJobId());
            stmt.setInt(2, app.getStudentId());
            stmt.setString(3, app.getCurrentStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating job application failed, no rows affected.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                app.setId(generatedKeys.getInt(1));
            }
            logger.info("Application saved with ID: {} for student ID: {} on job ID: {}", 
                    app.getId(), app.getStudentId(), app.getJobId());
            return app;

        } catch (SQLException e) {
            logger.error("Error saving job application: {}", e.getMessage(), e);
            throw new RuntimeException("Database error submitting application: " + e.getMessage(), e);
        } finally {
            DBUtil.close(generatedKeys, stmt, conn);
        }
    }

    @Override
    public Optional<JobApplication> findByJobAndStudent(int jobId, int studentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_JOB_AND_STUDENT);
            stmt.setInt(1, jobId);
            stmt.setInt(2, studentId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToApplication(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding application for job {} and student {}: {}", jobId, studentId, e.getMessage(), e);
            throw new RuntimeException("Database error finding application: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<JobApplication> findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_ID);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToApplication(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding application by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding application by ID: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public List<JobApplication> findByStudentId(int studentId) {
        List<JobApplication> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_STUDENT);
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToApplication(rs));
            }
            return list;

        } catch (SQLException e) {
            logger.error("Error finding applications for student ID {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Database error retrieving student applications: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public List<JobApplication> findByJobId(int jobId) {
        List<JobApplication> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_JOB);
            stmt.setInt(1, jobId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToApplication(rs));
            }
            return list;

        } catch (SQLException e) {
            logger.error("Error finding applicants for job ID {}: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Database error retrieving applicants: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean updateStatus(int applicationId, ApplicationStatus status, String rejectionReason) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_STATUS);
            stmt.setString(1, status.name());
            stmt.setString(2, rejectionReason);
            stmt.setInt(3, applicationId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating application status for ID {}: {}", applicationId, e.getMessage(), e);
            throw new RuntimeException("Database error updating status: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public int countByStudentId(int studentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_COUNT_BY_STUDENT);
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting applications for student ID {}: {}", studentId, e.getMessage(), e);
            return 0;
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public int countByJobId(int jobId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_COUNT_BY_JOB);
            stmt.setInt(1, jobId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting applications for job ID {}: {}", jobId, e.getMessage(), e);
            return 0;
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    private JobApplication mapResultSetToApplication(ResultSet rs) throws SQLException {
        JobApplication app = new JobApplication();
        app.setId(rs.getInt("id"));
        app.setJobId(rs.getInt("job_id"));
        app.setStudentId(rs.getInt("student_id"));
        app.setCurrentStatus(ApplicationStatus.fromString(rs.getString("current_status")));
        app.setRejectionReason(rs.getString("rejection_reason"));

        Timestamp appliedAt = rs.getTimestamp("applied_at");
        if (appliedAt != null) {
            app.setAppliedAt(appliedAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            app.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        try {
            app.setJobTitle(rs.getString("job_title"));
            app.setPackageLpa(rs.getBigDecimal("package_lpa"));
            app.setJobLocation(rs.getString("job_location"));
            app.setCompanyName(rs.getString("company_name"));
            app.setCompanyWebsite(rs.getString("company_website"));

            app.setStudentFullName(rs.getString("student_full_name"));
            app.setRollNumber(rs.getString("roll_number"));
            app.setStudentCgpa(rs.getBigDecimal("student_cgpa"));
            app.setStudentDepartment(rs.getString("student_department"));
            app.setStudentEmail(rs.getString("student_email"));
            app.setResumeFilePath(rs.getString("resume_file_path"));
        } catch (SQLException ignored) {
        }

        return app;
    }
}
