package com.smarthire.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DBUtil
 *
 * Helper utility methods for JDBC resource cleanup, transaction management,
 * and null-safe closing of database objects.
 */
public final class DBUtil {

    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);

    private DBUtil() {
        // Prevent instantiation
    }

    /**
     * Safely closes one or more AutoCloseable resources (ResultSet, Statement, Connection).
     * Suppresses and logs any SQLExceptions to ensure cleanup logic completes without throwing.
     */
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    logger.warn("Error closing database resource: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Safely rolls back a transaction on the given connection.
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                logger.debug("Transaction rolled back successfully.");
            } catch (SQLException e) {
                logger.error("Failed to rollback transaction: {}", e.getMessage());
            }
        }
    }

    /**
     * Resets autoCommit to true before returning a connection to the pool.
     */
    public static void resetAutoCommit(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                logger.warn("Failed to reset autoCommit to true: {}", e.getMessage());
            }
        }
    }
}
