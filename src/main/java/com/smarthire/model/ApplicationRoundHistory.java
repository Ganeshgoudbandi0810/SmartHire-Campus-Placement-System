package com.smarthire.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ApplicationRoundHistory
 *
 * Domain Model POJO tracking candidate progression, scores, and feedback for a specific selection round.
 */
public class ApplicationRoundHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int applicationId;
    private int roundId;
    private RoundResultStatus status = RoundResultStatus.PENDING;
    private String score;
    private String interviewerFeedback;
    private LocalDateTime updatedAt;

    // Transient metadata populated via SQL joins
    private String roundName;
    private int roundNumber;
    private RoundType roundType;
    private String studentFullName;
    private String rollNumber;

    public ApplicationRoundHistory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public RoundResultStatus getStatus() {
        return status != null ? status : RoundResultStatus.PENDING;
    }

    public void setStatus(RoundResultStatus status) {
        this.status = status;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getInterviewerFeedback() {
        return interviewerFeedback;
    }

    public void setInterviewerFeedback(String interviewerFeedback) {
        this.interviewerFeedback = interviewerFeedback;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public RoundType getRoundType() {
        return roundType;
    }

    public void setRoundType(RoundType roundType) {
        this.roundType = roundType;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }
}
