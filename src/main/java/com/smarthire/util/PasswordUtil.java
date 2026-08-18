package com.smarthire.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PasswordUtil
 *
 * Cryptographic utility providing salted BCrypt password hashing and verification.
 */
public final class PasswordUtil {

    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    private static final int LOG_ROUNDS = 10;

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Hashes a raw plain-text password using BCrypt with a secure random salt.
     *
     * @param plainPassword Raw password entered by the user
     * @return 60-character BCrypt hash string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Verifies a plain-text candidate password against a stored BCrypt hash.
     *
     * @param plainPassword  Raw password from login input
     * @param hashedPassword Stored BCrypt hash from the database
     * @return true if matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            logger.warn("Password verification failed with invalid hash format: {}", e.getMessage());
            return false;
        }
    }
}
