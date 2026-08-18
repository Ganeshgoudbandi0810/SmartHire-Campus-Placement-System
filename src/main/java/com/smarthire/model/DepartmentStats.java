package com.smarthire.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DepartmentStats
 *
 * Value Object DTO encapsulating academic department-level placement performance.
 */
public class DepartmentStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String department;
    private final int totalStudents;
    private final int placedStudents;
    private final double placementRate;
    private final BigDecimal averagePackage;

    public DepartmentStats(String department, int totalStudents, int placedStudents, double placementRate, BigDecimal averagePackage) {
        this.department = department;
        this.totalStudents = totalStudents;
        this.placedStudents = placedStudents;
        this.placementRate = placementRate;
        this.averagePackage = averagePackage != null ? averagePackage : BigDecimal.ZERO;
    }

    public String getDepartment() {
        return department;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public int getPlacedStudents() {
        return placedStudents;
    }

    public double getPlacementRate() {
        return placementRate;
    }

    public BigDecimal getAveragePackage() {
        return averagePackage;
    }
}
