package com.smarthire.dao;

import com.smarthire.model.DepartmentStats;
import com.smarthire.model.PlacementAnalyticsDTO;
import com.smarthire.util.DBConnectionManager;
import com.smarthire.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * AnalyticsDAOImpl
 *
 * JDBC implementation of AnalyticsDAO executing high-performance aggregate SQL queries.
 */
public class AnalyticsDAOImpl implements AnalyticsDAO {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsDAOImpl.class);

    private static final String SQL_STUDENT_COUNTS =
            "SELECT COUNT(*), COALESCE(SUM(CASE WHEN is_verified = 1 THEN 1 ELSE 0 END), 0) FROM student_profiles";

    private static final String SQL_PLACED_COUNT =
            "SELECT COUNT(DISTINCT student_id) FROM job_applications WHERE current_status IN ('OFFERED', 'ACCEPTED')";

    private static final String SQL_SALARY_STATS =
            "SELECT COALESCE(MAX(j.package_lpa), 0), COALESCE(AVG(j.package_lpa), 0) " +
            "FROM job_applications ja " +
            "INNER JOIN job_postings j ON ja.job_id = j.id " +
            "WHERE ja.current_status IN ('OFFERED', 'ACCEPTED')";

    private static final String SQL_COMPANY_COUNT = "SELECT COUNT(*) FROM companies";
    private static final String SQL_ACTIVE_DRIVES_COUNT = "SELECT COUNT(*) FROM job_postings WHERE status = 'OPEN'";

    private static final String SQL_DEPT_BREAKDOWN =
            "SELECT sp.department, " +
            "COUNT(DISTINCT sp.id) AS total_dept_students, " +
            "COUNT(DISTINCT CASE WHEN ja.current_status IN ('OFFERED', 'ACCEPTED') THEN sp.id END) AS placed_dept_students, " +
            "COALESCE(AVG(CASE WHEN ja.current_status IN ('OFFERED', 'ACCEPTED') THEN j.package_lpa END), 0) AS avg_dept_package " +
            "FROM student_profiles sp " +
            "LEFT JOIN job_applications ja ON sp.id = ja.student_id " +
            "LEFT JOIN job_postings j ON ja.job_id = j.id " +
            "WHERE sp.department IS NOT NULL AND sp.department != '' " +
            "GROUP BY sp.department ORDER BY total_dept_students DESC";

    @Override
    public PlacementAnalyticsDTO getPlacementAnalytics() {
        PlacementAnalyticsDTO dto = new PlacementAnalyticsDTO();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.createStatement();

            // 1. Student Counts
            rs = stmt.executeQuery(SQL_STUDENT_COUNTS);
            if (rs.next()) {
                dto.setTotalStudents(rs.getInt(1));
                dto.setVerifiedStudents(rs.getInt(2));
            }
            rs.close();

            // 2. Placed Count
            rs = stmt.executeQuery(SQL_PLACED_COUNT);
            if (rs.next()) {
                dto.setPlacedStudents(rs.getInt(1));
            }
            rs.close();

            // Compute overall placement rate
            if (dto.getTotalStudents() > 0) {
                double rate = ((double) dto.getPlacedStudents() / dto.getTotalStudents()) * 100.0;
                dto.setOverallPlacementRate(BigDecimal.valueOf(rate).setScale(1, RoundingMode.HALF_UP).doubleValue());
            }

            // 3. CTC Statistics
            rs = stmt.executeQuery(SQL_SALARY_STATS);
            if (rs.next()) {
                dto.setHighestPackageLpa(rs.getBigDecimal(1).setScale(2, RoundingMode.HALF_UP));
                dto.setAveragePackageLpa(rs.getBigDecimal(2).setScale(2, RoundingMode.HALF_UP));
            }
            rs.close();

            // 4. Partner Companies & Active Drives
            rs = stmt.executeQuery(SQL_COMPANY_COUNT);
            if (rs.next()) {
                dto.setTotalCompanies(rs.getInt(1));
            }
            rs.close();

            rs = stmt.executeQuery(SQL_ACTIVE_DRIVES_COUNT);
            if (rs.next()) {
                dto.setActiveDrives(rs.getInt(1));
            }
            rs.close();

            // 5. Department Breakdown
            rs = stmt.executeQuery(SQL_DEPT_BREAKDOWN);
            List<DepartmentStats> deptList = new ArrayList<>();
            while (rs.next()) {
                String dept = rs.getString("department");
                int total = rs.getInt("total_dept_students");
                int placed = rs.getInt("placed_dept_students");
                double deptRate = total > 0 ? ((double) placed / total) * 100.0 : 0.0;
                BigDecimal avgPkg = rs.getBigDecimal("avg_dept_package").setScale(2, RoundingMode.HALF_UP);

                deptList.add(new DepartmentStats(dept, total, placed,
                        BigDecimal.valueOf(deptRate).setScale(1, RoundingMode.HALF_UP).doubleValue(), avgPkg));
            }
            dto.setDepartmentStats(deptList);

            return dto;

        } catch (SQLException e) {
            logger.error("Error computing placement analytics: {}", e.getMessage(), e);
            throw new RuntimeException("Database error computing analytics: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }
}
