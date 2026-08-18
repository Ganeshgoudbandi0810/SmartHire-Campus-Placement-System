package com.smarthire.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * StudentProfile
 *
 * Domain Model POJO representing a candidate's personal and academic records.
 */
public class StudentProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String rollNumber;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender; // MALE, FEMALE, OTHER
    private String department; // CSE, IT, ECE, MECH, CIVIL, etc.
    private BigDecimal cgpa = BigDecimal.ZERO;
    private BigDecimal tenthPercentage = BigDecimal.ZERO;
    private BigDecimal twelfthPercentage = BigDecimal.ZERO;
    private int activeBacklogs = 0;
    private int totalBacklogsHistory = 0;
    private int graduationYear;
    private String resumeFilePath;
    private String skills;
    private boolean isVerified = false;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Associated User account email (for joins / views)
    private String userEmail;

    public StudentProfile() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        if (firstName == null && lastName == null) return "Unnamed Student";
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return firstName.trim() + " " + lastName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getCgpa() {
        return cgpa != null ? cgpa : BigDecimal.ZERO;
    }

    public void setCgpa(BigDecimal cgpa) {
        this.cgpa = cgpa;
    }

    public BigDecimal getTenthPercentage() {
        return tenthPercentage != null ? tenthPercentage : BigDecimal.ZERO;
    }

    public void setTenthPercentage(BigDecimal tenthPercentage) {
        this.tenthPercentage = tenthPercentage;
    }

    public BigDecimal getTwelfthPercentage() {
        return twelfthPercentage != null ? twelfthPercentage : BigDecimal.ZERO;
    }

    public void setTwelfthPercentage(BigDecimal twelfthPercentage) {
        this.twelfthPercentage = twelfthPercentage;
    }

    public int getActiveBacklogs() {
        return activeBacklogs;
    }

    public void setActiveBacklogs(int activeBacklogs) {
        this.activeBacklogs = activeBacklogs;
    }

    public int getTotalBacklogsHistory() {
        return totalBacklogsHistory;
    }

    public void setTotalBacklogsHistory(int totalBacklogsHistory) {
        this.totalBacklogsHistory = totalBacklogsHistory;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getResumeFilePath() {
        return resumeFilePath;
    }

    public void setResumeFilePath(String resumeFilePath) {
        this.resumeFilePath = resumeFilePath;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Calculates the profile completion percentage for student dashboard progress.
     */
    public int getCompletionPercentage() {
        int score = 0;
        if (rollNumber != null && !rollNumber.trim().isEmpty()) score += 15;
        if (firstName != null && !firstName.trim().isEmpty()) score += 10;
        if (lastName != null && !lastName.trim().isEmpty()) score += 10;
        if (phone != null && !phone.trim().isEmpty()) score += 10;
        if (department != null && !department.trim().isEmpty()) score += 15;
        if (cgpa != null && cgpa.compareTo(BigDecimal.ZERO) > 0) score += 15;
        if (tenthPercentage != null && tenthPercentage.compareTo(BigDecimal.ZERO) > 0) score += 5;
        if (twelfthPercentage != null && twelfthPercentage.compareTo(BigDecimal.ZERO) > 0) score += 5;
        if (graduationYear > 0) score += 5;
        if (resumeFilePath != null && !resumeFilePath.trim().isEmpty()) score += 10;
        return Math.min(100, score);
    }

    public boolean isProfileComplete() {
        return getCompletionPercentage() >= 80;
    }
}
