package com.smarthire.dao;

import com.smarthire.model.StudentProfile;
import com.smarthire.util.DBConnectionManager;
import com.smarthire.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * StudentDAOImpl
 *
 * JDBC implementation of StudentDAO using PreparedStatement and HikariCP pool.
 */
public class StudentDAOImpl implements StudentDAO {

    private static final Logger logger = LoggerFactory.getLogger(StudentDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO student_profiles (user_id, roll_number, first_name, last_name, phone, gender, " +
            "department, cgpa, tenth_percentage, twelfth_percentage, active_backlogs, total_backlogs_history, " +
            "graduation_year, resume_file_path, skills, is_verified) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE student_profiles SET roll_number = ?, first_name = ?, last_name = ?, phone = ?, " +
            "gender = ?, department = ?, cgpa = ?, tenth_percentage = ?, twelfth_percentage = ?, " +
            "active_backlogs = ?, total_backlogs_history = ?, graduation_year = ?, skills = ? " +
            "WHERE id = ?";

    private static final String SQL_FIND_BY_USER_ID =
            "SELECT sp.*, u.email AS user_email FROM student_profiles sp " +
            "INNER JOIN users u ON sp.user_id = u.id WHERE sp.user_id = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT sp.*, u.email AS user_email FROM student_profiles sp " +
            "INNER JOIN users u ON sp.user_id = u.id WHERE sp.id = ?";

    private static final String SQL_FIND_BY_ROLL_NO =
            "SELECT sp.*, u.email AS user_email FROM student_profiles sp " +
            "INNER JOIN users u ON sp.user_id = u.id WHERE sp.roll_number = ?";

    private static final String SQL_UPDATE_RESUME =
            "UPDATE student_profiles SET resume_file_path = ? WHERE id = ?";

    private static final String SQL_UPDATE_VERIFIED =
            "UPDATE student_profiles SET is_verified = ?, verified_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT sp.*, u.email AS user_email FROM student_profiles sp " +
            "INNER JOIN users u ON sp.user_id = u.id ORDER BY sp.id DESC";

    private static final String SQL_FIND_BY_DEPT =
            "SELECT sp.*, u.email AS user_email FROM student_profiles sp " +
            "INNER JOIN users u ON sp.user_id = u.id WHERE sp.department = ? ORDER BY sp.cgpa DESC";

    @Override
    public StudentProfile save(StudentProfile profile) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getRollNumber() != null ? profile.getRollNumber().trim() : "");
            stmt.setString(3, profile.getFirstName() != null ? profile.getFirstName().trim() : "");
            stmt.setString(4, profile.getLastName() != null ? profile.getLastName().trim() : "");
            stmt.setString(5, profile.getPhone());
            stmt.setString(6, profile.getGender());
            stmt.setString(7, profile.getDepartment() != null ? profile.getDepartment().trim() : "");
            stmt.setBigDecimal(8, profile.getCgpa());
            stmt.setBigDecimal(9, profile.getTenthPercentage());
            stmt.setBigDecimal(10, profile.getTwelfthPercentage());
            stmt.setInt(11, profile.getActiveBacklogs());
            stmt.setInt(12, profile.getTotalBacklogsHistory());
            stmt.setInt(13, profile.getGraduationYear());
            stmt.setString(14, profile.getResumeFilePath());
            stmt.setString(15, profile.getSkills());
            stmt.setBoolean(16, profile.isVerified());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating student profile failed, no rows affected.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                profile.setId(generatedKeys.getInt(1));
            }
            logger.info("Student profile saved for user ID: {} with profile ID: {}", profile.getUserId(), profile.getId());
            return profile;

        } catch (SQLException e) {
            logger.error("Error saving student profile for user ID {}: {}", profile.getUserId(), e.getMessage(), e);
            throw new RuntimeException("Database error saving student profile: " + e.getMessage(), e);
        } finally {
            DBUtil.close(generatedKeys, stmt, conn);
        }
    }

    @Override
    public boolean update(StudentProfile profile) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, profile.getRollNumber() != null ? profile.getRollNumber().trim() : "");
            stmt.setString(2, profile.getFirstName() != null ? profile.getFirstName().trim() : "");
            stmt.setString(3, profile.getLastName() != null ? profile.getLastName().trim() : "");
            stmt.setString(4, profile.getPhone());
            stmt.setString(5, profile.getGender());
            stmt.setString(6, profile.getDepartment() != null ? profile.getDepartment().trim() : "");
            stmt.setBigDecimal(7, profile.getCgpa());
            stmt.setBigDecimal(8, profile.getTenthPercentage());
            stmt.setBigDecimal(9, profile.getTwelfthPercentage());
            stmt.setInt(10, profile.getActiveBacklogs());
            stmt.setInt(11, profile.getTotalBacklogsHistory());
            stmt.setInt(12, profile.getGraduationYear());
            stmt.setString(13, profile.getSkills());
            stmt.setInt(14, profile.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating student profile ID {}: {}", profile.getId(), e.getMessage(), e);
            throw new RuntimeException("Database error updating student profile: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public Optional<StudentProfile> findByUserId(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_USER_ID);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProfile(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding student profile by user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Database error finding student profile: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<StudentProfile> findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_ID);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProfile(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding student profile by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding student profile: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<StudentProfile> findByRollNumber(String rollNumber) {
        if (rollNumber == null) return Optional.empty();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_ROLL_NO);
            stmt.setString(1, rollNumber.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProfile(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding student profile by roll number {}: {}", rollNumber, e.getMessage(), e);
            throw new RuntimeException("Database error finding student profile by roll number: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean updateResumePath(int studentId, String resumePath) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_RESUME);
            stmt.setString(1, resumePath);
            stmt.setInt(2, studentId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating resume path for student ID {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Database error updating resume path: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public boolean updateVerificationStatus(int studentId, boolean isVerified) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_VERIFIED);
            stmt.setBoolean(1, isVerified);
            stmt.setInt(2, studentId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating verification for student ID {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Database error updating verification: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public List<StudentProfile> findAll() {
        List<StudentProfile> profiles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                profiles.add(mapResultSetToProfile(rs));
            }
            return profiles;

        } catch (SQLException e) {
            logger.error("Error retrieving all student profiles: {}", e.getMessage(), e);
            throw new RuntimeException("Database error retrieving student profiles: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public List<StudentProfile> findByDepartment(String department) {
        List<StudentProfile> profiles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_DEPT);
            stmt.setString(1, department.trim());
            rs = stmt.executeQuery();

            while (rs.next()) {
                profiles.add(mapResultSetToProfile(rs));
            }
            return profiles;

        } catch (SQLException e) {
            logger.error("Error retrieving students for department {}: {}", department, e.getMessage(), e);
            throw new RuntimeException("Database error retrieving students by department: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    /**
     * Helper to map a SQL ResultSet row to a StudentProfile POJO.
     */
    private StudentProfile mapResultSetToProfile(ResultSet rs) throws SQLException {
        StudentProfile p = new StudentProfile();
        p.setId(rs.getInt("id"));
        p.setUserId(rs.getInt("user_id"));
        p.setRollNumber(rs.getString("roll_number"));
        p.setFirstName(rs.getString("first_name"));
        p.setLastName(rs.getString("last_name"));
        p.setPhone(rs.getString("phone"));
        p.setGender(rs.getString("gender"));
        p.setDepartment(rs.getString("department"));
        p.setCgpa(rs.getBigDecimal("cgpa"));
        p.setTenthPercentage(rs.getBigDecimal("tenth_percentage"));
        p.setTwelfthPercentage(rs.getBigDecimal("twelfth_percentage"));
        p.setActiveBacklogs(rs.getInt("active_backlogs"));
        p.setTotalBacklogsHistory(rs.getInt("total_backlogs_history"));
        p.setGraduationYear(rs.getInt("graduation_year"));
        p.setResumeFilePath(rs.getString("resume_file_path"));
        p.setSkills(rs.getString("skills"));
        p.setVerified(rs.getBoolean("is_verified"));

        Timestamp verifiedAt = rs.getTimestamp("verified_at");
        if (verifiedAt != null) {
            p.setVerifiedAt(verifiedAt.toLocalDateTime());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            p.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            p.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        try {
            p.setUserEmail(rs.getString("user_email"));
        } catch (SQLException ignored) {
            // column not present in some queries
        }

        return p;
    }
}
