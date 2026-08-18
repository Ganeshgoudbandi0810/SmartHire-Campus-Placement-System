package com.smarthire.service;

import com.smarthire.model.JobApplication;
import com.smarthire.model.PlacementAnalyticsDTO;
import com.smarthire.model.StudentProfile;

import java.util.List;

/**
 * AnalyticsService
 *
 * Business service interface managing institutional placement intelligence
 * and RFC-4180 CSV export generation.
 */
public interface AnalyticsService {

    /**
     * Aggregates placement analytics and department metrics.
     */
    PlacementAnalyticsDTO getPlacementAnalytics();

    /**
     * Generates a sanitized, RFC-4180 compliant CSV string of student academic records.
     */
    String exportStudentsToCSV(List<StudentProfile> students);

    /**
     * Generates a sanitized, RFC-4180 compliant CSV string of candidate applications for a drive.
     */
    String exportApplicantsToCSV(List<JobApplication> applicants);
}
