package com.smarthire.dao;

import com.smarthire.model.ApplicationRoundHistory;
import com.smarthire.model.RoundResultStatus;
import com.smarthire.model.SelectionRound;

import java.util.List;
import java.util.Optional;

/**
 * RoundDAO
 *
 * Data Access Object interface for managing selection rounds and candidate scorecards.
 */
public interface RoundDAO {

    /**
     * Persists a newly configured selection round.
     */
    SelectionRound saveRound(SelectionRound round);

    /**
     * Updates round configuration details.
     */
    boolean updateRound(SelectionRound round);

    /**
     * Finds a selection round by its primary key ID.
     */
    Optional<SelectionRound> findRoundById(int roundId);

    /**
     * Retrieves all scheduled selection rounds for a placement drive in sequence order.
     */
    List<SelectionRound> findRoundsByJobId(int jobId);

    /**
     * Deletes a selection round.
     */
    boolean deleteRound(int roundId);

    /**
     * Persists or initialises a candidate's scorecard entry for a specific round.
     */
    ApplicationRoundHistory saveRoundHistory(ApplicationRoundHistory history);

    /**
     * Updates a candidate's evaluation status, test score, and interviewer remarks.
     */
    boolean updateRoundHistory(int historyId, RoundResultStatus status, String score, String feedback);

    /**
     * Retrieves the complete multi-round progression history for a candidate application.
     */
    List<ApplicationRoundHistory> findHistoryByApplicationId(int applicationId);

    /**
     * Retrieves all candidate evaluations for a specific round.
     */
    List<ApplicationRoundHistory> findHistoryByRoundId(int roundId);

    /**
     * Finds a candidate's evaluation for a specific round.
     */
    Optional<ApplicationRoundHistory> findHistoryByRoundAndApplication(int roundId, int applicationId);
}
