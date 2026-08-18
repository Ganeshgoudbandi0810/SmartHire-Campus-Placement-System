package com.smarthire.dao;

import com.smarthire.model.PlacementAnalyticsDTO;

/**
 * AnalyticsDAO
 *
 * Data Access Object interface for aggregating campus placement statistics and CTC analytics.
 */
public interface AnalyticsDAO {

    /**
     * Aggregates live university-wide placement statistics, salary packages, and branch metrics.
     */
    PlacementAnalyticsDTO getPlacementAnalytics();
}
