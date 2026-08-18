package com.smarthire.dao;

import com.smarthire.model.*;
import com.smarthire.util.DBConnectionManager;
import com.smarthire.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RoundDAOImpl
 *
 * JDBC implementation of RoundDAO using PreparedStatement and HikariCP pool.
 */
public class RoundDAOImpl implements RoundDAO {

    private static final Logger logger = LoggerFactory.getLogger(RoundDAOImpl.class);

    private static final String SQL_INSERT_ROUND =
            "INSERT INTO selection_rounds (job_id, round_number, round_name, round_type, description, " +
            "scheduled_date, venue_or_link, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_ROUND =
            "UPDATE selection_rounds SET round_number = ?, round_name = ?, round_type = ?, description = ?, " +
            "scheduled_date = ?, venue_or_link = ?, status = ? WHERE id = ?";

    private static final String SQL_FIND_ROUNDS_BY_JOB =
            "SELECT r.*, j.job_title, " +
            "(SELECT COUNT(*) FROM application_round_history arh WHERE arh.round_id = r.id) AS candidate_count " +
            "FROM selection_rounds r " +
            "INNER JOIN job_postings j ON r.job_id = j.id " +
            "WHERE r.job_id = ? ORDER BY r.round_number ASC";

    private static final String SQL_FIND_ROUND_BY_ID =
            "SELECT r.*, j.job_title, " +
            "(SELECT COUNT(*) FROM application_round_history arh WHERE arh.round_id = r.id) AS candidate_count " +
            "FROM selection_rounds r " +
            "INNER JOIN job_postings j ON r.job_id = j.id " +
            "WHERE r.id = ?";

    private static final String SQL_DELETE_ROUND =
            "DELETE FROM selection_rounds WHERE id = ?";

    private static final String SQL_INSERT_HISTORY =
            "INSERT INTO application_round_history (application_id, round_id, status, score, interviewer_feedback) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_HISTORY =
            "UPDATE application_round_history SET status = ?, score = ?, interviewer_feedback = ? WHERE id = ?";

    private static final String SQL_HISTORY_BASE =
            "SELECT arh.*, sr.round_name, sr.round_number, sr.round_type, " +
            "CONCAT(sp.first_name, ' ', sp.last_name) AS student_full_name, sp.roll_number " +
            "FROM application_round_history arh " +
            "INNER JOIN selection_rounds sr ON arh.round_id = sr.id " +
            "INNER JOIN job_applications ja ON arh.application_id = ja.id " +
            "INNER JOIN student_profiles sp ON ja.student_id = sp.id ";

    private static final String SQL_FIND_HISTORY_BY_APP =
            SQL_HISTORY_BASE + "WHERE arh.application_id = ? ORDER BY sr.round_number ASC";

    private static final String SQL_FIND_HISTORY_BY_ROUND =
            SQL_HISTORY_BASE + "WHERE arh.round_id = ? ORDER BY arh.id ASC";

    private static final String SQL_FIND_HISTORY_BY_ROUND_AND_APP =
            SQL_HISTORY_BASE + "WHERE arh.round_id = ? AND arh.application_id = ?";

    @Override
    public SelectionRound saveRound(SelectionRound round) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_ROUND, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, round.getJobId());
            stmt.setInt(2, round.getRoundNumber());
            stmt.setString(3, round.getRoundName().trim());
            stmt.setString(4, round.getRoundType().name());
            stmt.setString(5, round.getDescription());
            stmt.setTimestamp(6, round.getScheduledDate() != null ? Timestamp.valueOf(round.getScheduledDate()) : null);
            stmt.setString(7, round.getVenueOrLink());
            stmt.setString(8, round.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating selection round failed, no rows affected.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                round.setId(generatedKeys.getInt(1));
            }
            logger.info("Selection round created with ID: {} for job ID: {}", round.getId(), round.getJobId());
            return round;

        } catch (SQLException e) {
            logger.error("Error saving selection round: {}", e.getMessage(), e);
            throw new RuntimeException("Database error creating selection round: " + e.getMessage(), e);
        } finally {
            DBUtil.close(generatedKeys, stmt, conn);
        }
    }

    @Override
    public boolean updateRound(SelectionRound round) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_ROUND);
            stmt.setInt(1, round.getRoundNumber());
            stmt.setString(2, round.getRoundName().trim());
            stmt.setString(3, round.getRoundType().name());
            stmt.setString(4, round.getDescription());
            stmt.setTimestamp(5, round.getScheduledDate() != null ? Timestamp.valueOf(round.getScheduledDate()) : null);
            stmt.setString(6, round.getVenueOrLink());
            stmt.setString(7, round.getStatus().name());
            stmt.setInt(8, round.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating round ID {}: {}", round.getId(), e.getMessage(), e);
            throw new RuntimeException("Database error updating selection round: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public Optional<SelectionRound> findRoundById(int roundId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ROUND_BY_ID);
            stmt.setInt(1, roundId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToRound(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding round by ID {}: {}", roundId, e.getMessage(), e);
            throw new RuntimeException("Database error finding round: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public List<SelectionRound> findRoundsByJobId(int jobId) {
        List<SelectionRound> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ROUNDS_BY_JOB);
            stmt.setInt(1, jobId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToRound(rs));
            }
            return list;

        } catch (SQLException e) {
            logger.error("Error finding rounds for job ID {}: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Database error finding rounds: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean deleteRound(int roundId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_ROUND);
            stmt.setInt(1, roundId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error deleting round ID {}: {}", roundId, e.getMessage(), e);
            throw new RuntimeException("Database error deleting round: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public ApplicationRoundHistory saveRoundHistory(ApplicationRoundHistory history) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_HISTORY, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, history.getApplicationId());
            stmt.setInt(2, history.getRoundId());
            stmt.setString(3, history.getStatus().name());
            stmt.setString(4, history.getScore());
            stmt.setString(5, history.getInterviewerFeedback());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating round history record failed, no rows affected.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                history.setId(generatedKeys.getInt(1));
            }
            logger.info("Round history created ID: {} for app: {} and round: {}", 
                    history.getId(), history.getApplicationId(), history.getRoundId());
            return history;

        } catch (SQLException e) {
            logger.error("Error saving round history: {}", e.getMessage(), e);
            throw new RuntimeException("Database error recording round score: " + e.getMessage(), e);
        } finally {
            DBUtil.close(generatedKeys, stmt, conn);
        }
    }

    @Override
    public boolean updateRoundHistory(int historyId, RoundResultStatus status, String score, String feedback) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_HISTORY);
            stmt.setString(1, status.name());
            stmt.setString(2, score);
            stmt.setString(3, feedback);
            stmt.setInt(4, historyId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating round history ID {}: {}", historyId, e.getMessage(), e);
            throw new RuntimeException("Database error updating round score: " + e.getMessage(), e);
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @Override
    public List<ApplicationRoundHistory> findHistoryByApplicationId(int applicationId) {
        List<ApplicationRoundHistory> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_HISTORY_BY_APP);
            stmt.setInt(1, applicationId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToHistory(rs));
            }
            return list;

        } catch (SQLException e) {
            logger.error("Error finding history for app ID {}: {}", applicationId, e.getMessage(), e);
            throw new RuntimeException("Database error finding round history: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public List<ApplicationRoundHistory> findHistoryByRoundId(int roundId) {
        List<ApplicationRoundHistory> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_HISTORY_BY_ROUND);
            stmt.setInt(1, roundId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToHistory(rs));
            }
            return list;

        } catch (SQLException e) {
            logger.error("Error finding history for round ID {}: {}", roundId, e.getMessage(), e);
            throw new RuntimeException("Database error finding round history: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<ApplicationRoundHistory> findHistoryByRoundAndApplication(int roundId, int applicationId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SQL_FIND_HISTORY_BY_ROUND_AND_APP);
            stmt.setInt(1, roundId);
            stmt.setInt(2, applicationId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToHistory(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding history for round {} and app {}: {}", roundId, applicationId, e.getMessage(), e);
            throw new RuntimeException("Database error finding round history: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    private SelectionRound mapResultSetToRound(ResultSet rs) throws SQLException {
        SelectionRound r = new SelectionRound();
        r.setId(rs.getInt("id"));
        r.setJobId(rs.getInt("job_id"));
        r.setRoundNumber(rs.getInt("round_number"));
        r.setRoundName(rs.getString("round_name"));
        r.setRoundType(RoundType.fromString(rs.getString("round_type")));
        r.setDescription(rs.getString("description"));

        Timestamp scheduledDate = rs.getTimestamp("scheduled_date");
        if (scheduledDate != null) {
            r.setScheduledDate(scheduledDate.toLocalDateTime());
        }

        r.setVenueOrLink(rs.getString("venue_or_link"));
        r.setStatus(RoundStatus.fromString(rs.getString("status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            r.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            r.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        try {
            r.setJobTitle(rs.getString("job_title"));
            r.setCandidateCount(rs.getInt("candidate_count"));
        } catch (SQLException ignored) {
        }

        return r;
    }

    private ApplicationRoundHistory mapResultSetToHistory(ResultSet rs) throws SQLException {
        ApplicationRoundHistory h = new ApplicationRoundHistory();
        h.setId(rs.getInt("id"));
        h.setApplicationId(rs.getInt("application_id"));
        h.setRoundId(rs.getInt("round_id"));
        h.setStatus(RoundResultStatus.fromString(rs.getString("status")));
        h.setScore(rs.getString("score"));
        h.setInterviewerFeedback(rs.getString("interviewer_feedback"));

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            h.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        try {
            h.setRoundName(rs.getString("round_name"));
            h.setRoundNumber(rs.getInt("round_number"));
            h.setRoundType(RoundType.fromString(rs.getString("round_type")));
            h.setStudentFullName(rs.getString("student_full_name"));
            h.setRollNumber(rs.getString("roll_number"));
        } catch (SQLException ignored) {
        }

        return h;
    }
}
