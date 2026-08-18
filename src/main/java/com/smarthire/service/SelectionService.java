package com.smarthire.service;

import com.smarthire.exception.ValidationException;
import com.smarthire.model.ApplicationRoundHistory;
import com.smarthire.model.ApplicationStatus;
import com.smarthire.model.RoundResultStatus;
import com.smarthire.model.SelectionRound;

import java.util.List;

/**
 * SelectionService
 *
 * Business service interface managing interview rounds, candidate scorecards,
 * round progression, and official job offer dispatches.
 */
public interface SelectionService {

    /**
     * Schedules a new interview round for a placement drive.
     */
    SelectionRound createRound(SelectionRound round) throws ValidationException;

    /**
     * Updates an existing interview round's venue, date, or name.
     */
    void updateRound(SelectionRound round) throws ValidationException;

    /**
     * Retrieves an interview round by primary key ID.
     */
    SelectionRound getRoundById(int roundId);

    /**
     * Retrieves all scheduled selection rounds for a placement drive in order.
     */
    List<SelectionRound> getRoundsByJobId(int jobId);

    /**
     * Deletes a scheduled round.
     */
    void deleteRound(int roundId);

    /**
     * Records or updates a candidate's scorecard for a specific selection round.
     */
    void recordCandidateRoundResult(int roundId, int applicationId, RoundResultStatus status, String score, String feedback);

    /**
     * Retrieves the entire evaluation history for a candidate application.
     */
    List<ApplicationRoundHistory> getApplicantRoundHistory(int applicationId);

    /**
     * Retrieves all candidate evaluations for a specific round.
     */
    List<ApplicationRoundHistory> getRoundEvaluations(int roundId);

    /**
     * Formally releases an official job offer to the candidate.
     */
    void releaseOffer(int applicationId);

    /**
     * Rejects a candidate application with feedback remarks.
     */
    void rejectCandidate(int applicationId, String reason);

    /**
     * Updates an application status directly (e.g. SHORTLISTED, IN_PROCESS).
     */
    void updateApplicationStatus(int applicationId, ApplicationStatus status);
}
