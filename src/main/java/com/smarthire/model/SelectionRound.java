package com.smarthire.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SelectionRound
 *
 * Domain Model POJO representing an interview or assessment stage within a placement drive.
 */
public class SelectionRound implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int jobId;
    private int roundNumber = 1;
    private String roundName;
    private RoundType roundType = RoundType.ONLINE_ASSESSMENT;
    private String description;
    private LocalDateTime scheduledDate;
    private String venueOrLink;
    private RoundStatus status = RoundStatus.SCHEDULED;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient metadata
    private String jobTitle;
    private int candidateCount = 0;

    public SelectionRound() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public RoundType getRoundType() {
        return roundType != null ? roundType : RoundType.ONLINE_ASSESSMENT;
    }

    public void setRoundType(RoundType roundType) {
        this.roundType = roundType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getVenueOrLink() {
        return venueOrLink;
    }

    public void setVenueOrLink(String venueOrLink) {
        this.venueOrLink = venueOrLink;
    }

    public RoundStatus getStatus() {
        return status != null ? status : RoundStatus.SCHEDULED;
    }

    public void setStatus(RoundStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public int getCandidateCount() {
        return candidateCount;
    }

    public void setCandidateCount(int candidateCount) {
        this.candidateCount = candidateCount;
    }
}
