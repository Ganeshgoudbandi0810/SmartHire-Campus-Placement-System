package com.smarthire.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnectionManager
 *
 * Thread-safe Singleton managing the HikariCP connection pool.
 * Loads database configuration from 'db.properties' in the classpath.
 */
public class DBConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(DBConnectionManager.class);
    private static volatile DBConnectionManager instance;
    private final HikariDataSource dataSource;

    private DBConnectionManager() {
        Properties props = loadProperties();
        HikariConfig config = new HikariConfig();

        // Database credentials & URL
        config.setDriverClassName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        config.setJdbcUrl(props.getProperty("db.url", "jdbc:mysql://localhost:3306/smarthire_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"));
        config.setUsername(props.getProperty("db.username", "root"));
        config.setPassword(props.getProperty("db.password", "root"));

        // Pool configuration & tuning
        config.setPoolName(props.getProperty("hikari.poolName", "SmartHireHikariPool"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikari.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("hikari.minimumIdle", "2")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("hikari.idleTimeout", "30000")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("hikari.connectionTimeout", "10000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("hikari.maxLifetime", "1800000")));

        // Performance optimizations for MySQL
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        try {
            this.dataSource = new HikariDataSource(config);
            logger.info("HikariCP connection pool initialized successfully: {}", config.getPoolName());
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP connection pool: {}", e.getMessage(), e);
            throw new RuntimeException("Database Connection Pool Initialization Failed: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the singleton instance of DBConnectionManager.
     */
    public static DBConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DBConnectionManager.class) {
                if (instance == null) {
                    instance = new DBConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Leases a Connection from the HikariCP pool.
     * The caller MUST close this connection (e.g. via try-with-resources)
     * to return it back to the pool.
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("HikariDataSource is closed or not initialized.");
        }
        return dataSource.getConnection();
    }

    /**
     * Returns the underlying HikariDataSource for diagnostic inspections.
     */
    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * Gracefully shuts down the connection pool upon application undeployment.
     */
    public synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing HikariCP connection pool: {}", dataSource.getPoolName());
            dataSource.close();
        }
    }

    /**
     * Helper to load db.properties from the classpath.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                logger.info("Loaded database configuration from 'db.properties'");
            } else {
                logger.warn("'db.properties' not found in classpath. Attempting fallback to 'db.properties.example' or defaults.");
                try (InputStream exampleIs = getClass().getClassLoader().getResourceAsStream("db.properties.example")) {
                    if (exampleIs != null) {
                        props.load(exampleIs);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading db.properties: {}", e.getMessage());
        }
        return props;
    }
}
