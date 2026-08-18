package com.smarthire.controller;

import com.google.gson.Gson;
import com.smarthire.util.DBConnectionManager;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * DatabaseTestServlet
 *
 * Diagnostic Controller for testing MySQL 8.0 connectivity and HikariCP connection pool health.
 *
 * Mapped to:
 *   - /db-test     -> Renders diagnostic view (/WEB-INF/views/db-test.jsp)
 *   - /api/db-test -> Returns raw JSON diagnostic metrics
 */
@WebServlet(name = "DatabaseTestServlet", urlPatterns = {"/db-test", "/api/db-test"})
public class DatabaseTestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, Object> dbReport = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            DBConnectionManager poolManager = DBConnectionManager.getInstance();
            HikariDataSource ds = poolManager.getDataSource();

            // Lease a connection to perform test queries
            try (Connection conn = poolManager.getConnection()) {
                long latencyMs = System.currentTimeMillis() - startTime;
                DatabaseMetaData meta = conn.getMetaData();

                dbReport.put("status", "CONNECTED");
                dbReport.put("databaseProduct", meta.getDatabaseProductName());
                dbReport.put("databaseVersion", meta.getDatabaseProductVersion());
                dbReport.put("driverName", meta.getDriverName());
                dbReport.put("driverVersion", meta.getDriverVersion());
                dbReport.put("connectionLatencyMs", latencyMs);
                dbReport.put("url", meta.getURL());
                dbReport.put("username", meta.getUserName());

                // Inspect table row counts
                Map<String, Integer> tableCounts = new HashMap<>();
                try (Statement stmt = conn.createStatement()) {
                    String[] tables = {"users", "student_profiles", "companies", "job_postings", "job_applications", "selection_rounds", "application_round_history"};
                    for (String table : tables) {
                        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
                            if (rs.next()) {
                                tableCounts.put(table, rs.getInt(1));
                            }
                        } catch (Exception tableEx) {
                            tableCounts.put(table, -1); // table not found or error
                        }
                    }
                }
                dbReport.put("tableCounts", tableCounts);
            }

            // Inspect HikariCP Pool metrics
            if (ds != null && ds.getHikariPoolMXBean() != null) {
                HikariPoolMXBean poolBean = ds.getHikariPoolMXBean();
                Map<String, Object> poolStats = new HashMap<>();
                poolStats.put("poolName", ds.getPoolName());
                poolStats.put("activeConnections", poolBean.getActiveConnections());
                poolStats.put("idleConnections", poolBean.getIdleConnections());
                poolStats.put("totalConnections", poolBean.getTotalConnections());
                poolStats.put("threadsAwaitingConnection", poolBean.getThreadsAwaitingConnection());
                poolStats.put("maxPoolSize", ds.getMaximumPoolSize());
                poolStats.put("minIdle", ds.getMinimumIdle());
                dbReport.put("poolStats", poolStats);
            }

        } catch (Exception e) {
            dbReport.put("status", "FAILED");
            dbReport.put("errorMessage", e.getMessage());
            dbReport.put("errorType", e.getClass().getName());
        }

        dbReport.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String requestUri = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");

        if (requestUri.endsWith("/api/db-test") || (acceptHeader != null && acceptHeader.contains("application/json"))) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(dbReport));
                out.flush();
            }
            return;
        }

        request.setAttribute("dbReport", dbReport);
        request.getRequestDispatcher("/WEB-INF/views/db-test.jsp").forward(request, response);
    }
}
