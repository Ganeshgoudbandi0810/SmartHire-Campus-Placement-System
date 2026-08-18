package com.smarthire.dao;

import com.smarthire.model.Role;
import com.smarthire.model.User;
import com.smarthire.model.UserStatus;
import com.smarthire.util.DBConnectionManager;
import com.smarthire.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserDAOImpl
 *
 * JDBC implementation of UserDAO using PreparedStatement and HikariCP connection pool.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private static final String SQL_INSERT = 
            "INSERT INTO users (email, password_hash, role, status) VALUES (?, ?, ?, ?)";
    
    private static final String SQL_FIND_BY_EMAIL = 
            "SELECT id, email, password_hash, role, status, created_at, updated_at FROM users WHERE email = ?";
    
    private static final String SQL_FIND_BY_ID = 
            "SELECT id, email, password_hash, role, status, created_at, updated_at FROM users WHERE id = ?";
    
    private static final String SQL_EXISTS_BY_EMAIL = 
            "SELECT 1 FROM users WHERE email = ?";
    
    private static final String SQL_UPDATE_PASSWORD = 
            "UPDATE users SET password_hash = ? WHERE id = ?";
    
    private static final String SQL_UPDATE_STATUS = 
            "UPDATE users SET status = ? WHERE id = ?";
    
    private static final String SQL_FIND_ALL = 
            "SELECT id, email, password_hash, role, status, created_at, updated_at FROM users ORDER BY id DESC";

    @Override
    public User save(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail().trim().toLowerCase());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getStatus() != null ? user.getStatus().name() : UserStatus.ACTIVE.name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
            logger.info("User created successfully with ID: {} and email: {}", user.getId(), user.getEmail());
            return user;

        } catch (SQLException e) {
            logger.error("Error saving user {}: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Database error saving user: " + e.getMessage(), e);
        } finally {
            DBUtil.close(generatedKeys, stmt, conn);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL);
            stmt.setString(1, email.trim().toLowerCase());
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Database error finding user by email: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<User> findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_ID);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding user by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding user by ID: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_EXISTS_BY_EMAIL);
            stmt.setString(1, email.trim().toLowerCase());
            rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            logger.error("Error checking user existence by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Database error checking email existence: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean updatePassword(int id, String newPasswordHash) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_PASSWORD);
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating password for user ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error updating password: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public boolean updateStatus(int id, UserStatus status) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_STATUS);
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating status for user ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error updating status: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;

        } catch (SQLException e) {
            logger.error("Error retrieving all users: {}", e.getMessage(), e);
            throw new RuntimeException("Database error retrieving users: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    /**
     * Helper to map a single SQL ResultSet row to a User POJO.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setStatus(UserStatus.fromString(rs.getString("status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }
}
