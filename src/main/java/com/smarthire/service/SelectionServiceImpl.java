package com.smarthire.service;

import com.smarthire.dao.*;
import com.smarthire.exception.ValidationException;
import com.smarthire.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * SelectionServiceImpl
 *
 * Implements business operations for multi-stage interview rounds, scorecards,
 * and official offer management.
 */
public class SelectionServiceImpl implements SelectionService {

    private static final Logger logger = LoggerFactory.getLogger(SelectionServiceImpl.class);

    private final RoundDAO roundDAO;
    private final JobApplicationDAO applicationDAO;
    private final JobDAO jobDAO;

    public SelectionServiceImpl() {
        this.roundDAO = new RoundDAOImpl();
        this.applicationDAO = new JobApplicationDAOImpl();
        this.jobDAO = new JobDAOImpl();
    }

    public SelectionServiceImpl(RoundDAO roundDAO, JobApplicationDAO applicationDAO, JobDAO jobDAO) {
        this.roundDAO = roundDAO;
        this.applicationDAO = applicationDAO;
        this.jobDAO = jobDAO;
    }

    @Override
    public SelectionRound createRound(SelectionRound round) throws ValidationException {
        if (round == null) {
            throw new ValidationException("Round payload cannot be null.");
        }
        if (round.getRoundName() == null || round.getRoundName().trim().isEmpty()) {
            throw new ValidationException("Round name is required.");
        }
        if (round.getJobId() <= 0) {
            throw new ValidationException("Invalid Job ID for round creation.");
        }

        return roundDAO.saveRound(round);
    }

    @Override
    public void updateRound(SelectionRound round) throws ValidationException {
        if (round == null || round.getId() <= 0) {
            throw new ValidationException("Invalid round ID for update.");
        }
        if (round.getRoundName() == null || round.getRoundName().trim().isEmpty()) {
            throw new ValidationException("Round name is required.");
        }
        roundDAO.updateRound(round);
    }

    @Override
    public SelectionRound getRoundById(int roundId) {
        return roundDAO.findRoundById(roundId)
                .orElseThrow(() -> new RuntimeException("Selection round not found with ID: " + roundId));
    }

    @Override
    public List<SelectionRound> getRoundsByJobId(int jobId) {
        return roundDAO.findRoundsByJobId(jobId);
    }

    @Override
    public void deleteRound(int roundId) {
        roundDAO.deleteRound(roundId);
    }

    @Override
    public void recordCandidateRoundResult(int roundId, int applicationId, RoundResultStatus status, String score, String feedback) {
        SelectionRound round = getRoundById(roundId);
        Optional<ApplicationRoundHistory> existing = roundDAO.findHistoryByRoundAndApplication(roundId, applicationId);

        if (existing.isPresent()) {
            roundDAO.updateRoundHistory(existing.get().getId(), status, score, feedback);
            logger.info("Updated existing round history ID {}", existing.get().getId());
        } else {
            ApplicationRoundHistory history = new ApplicationRoundHistory();
            history.setApplicationId(applicationId);
            history.setRoundId(roundId);
            history.setStatus(status);
            history.setScore(score);
            history.setInterviewerFeedback(feedback);
            roundDAO.saveRoundHistory(history);
            logger.info("Created new round history for app {} on round {}", applicationId, roundId);
        }

        // Advance or update application state based on round evaluation
        if (status == RoundResultStatus.QUALIFIED) {
            applicationDAO.updateStatus(applicationId, ApplicationStatus.IN_PROCESS, null);
        } else if (status == RoundResultStatus.DISQUALIFIED) {
            String reason = "Disqualified in " + round.getRoundName() + (feedback != null ? ": " + feedback : "");
            applicationDAO.updateStatus(applicationId, ApplicationStatus.REJECTED, reason);
        }
    }

    @Override
    public List<ApplicationRoundHistory> getApplicantRoundHistory(int applicationId) {
        return roundDAO.findHistoryByApplicationId(applicationId);
    }

    @Override
    public List<ApplicationRoundHistory> getRoundEvaluations(int roundId) {
        return roundDAO.findHistoryByRoundId(roundId);
    }

    @Override
    public void releaseOffer(int applicationId) {
        applicationDAO.updateStatus(applicationId, ApplicationStatus.OFFERED, null);
        logger.info("🎉 Released official job offer for application ID: {}", applicationId);
    }

    @Override
    public void rejectCandidate(int applicationId, String reason) {
        applicationDAO.updateStatus(applicationId, ApplicationStatus.REJECTED, reason);
        logger.info("Candidate rejected for application ID: {} with reason: {}", applicationId, reason);
    }

    @Override
    public void updateApplicationStatus(int applicationId, ApplicationStatus status) {
        applicationDAO.updateStatus(applicationId, status, null);
    }
}
