package com.smarthire.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * PlacementAnalyticsDTO
 *
 * Institutional analytics composite DTO for university accreditation and Placement Cell dashboards.
 */
public class PlacementAnalyticsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalStudents;
    private int verifiedStudents;
    private int placedStudents;
    private double overallPlacementRate;
    private BigDecimal highestPackageLpa = BigDecimal.ZERO;
    private BigDecimal averagePackageLpa = BigDecimal.ZERO;
    private int totalCompanies;
    private int activeDrives;
    private List<DepartmentStats> departmentStats = new ArrayList<>();

    public PlacementAnalyticsDTO() {
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getVerifiedStudents() {
        return verifiedStudents;
    }

    public void setVerifiedStudents(int verifiedStudents) {
        this.verifiedStudents = verifiedStudents;
    }

    public int getPlacedStudents() {
        return placedStudents;
    }

    public void setPlacedStudents(int placedStudents) {
        this.placedStudents = placedStudents;
    }

    public double getOverallPlacementRate() {
        return overallPlacementRate;
    }

    public void setOverallPlacementRate(double overallPlacementRate) {
        this.overallPlacementRate = overallPlacementRate;
    }

    public BigDecimal getHighestPackageLpa() {
        return highestPackageLpa != null ? highestPackageLpa : BigDecimal.ZERO;
    }

    public void setHighestPackageLpa(BigDecimal highestPackageLpa) {
        this.highestPackageLpa = highestPackageLpa;
    }

    public BigDecimal getAveragePackageLpa() {
        return averagePackageLpa != null ? averagePackageLpa : BigDecimal.ZERO;
    }

    public void setAveragePackageLpa(BigDecimal averagePackageLpa) {
        this.averagePackageLpa = averagePackageLpa;
    }

    public int getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(int totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public int getActiveDrives() {
        return activeDrives;
    }

    public void setActiveDrives(int activeDrives) {
        this.activeDrives = activeDrives;
    }

    public List<DepartmentStats> getDepartmentStats() {
        return departmentStats;
    }

    public void setDepartmentStats(List<DepartmentStats> departmentStats) {
        this.departmentStats = departmentStats;
    }
}
