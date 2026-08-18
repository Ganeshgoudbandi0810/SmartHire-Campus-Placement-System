package com.smarthire.service;

import com.smarthire.dao.UserDAO;
import com.smarthire.dao.UserDAOImpl;
import com.smarthire.exception.AuthenticationException;
import com.smarthire.model.Role;
import com.smarthire.model.User;
import com.smarthire.model.UserStatus;
import com.smarthire.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * AuthServiceImpl
 *
 * Implements business rules for credential verification, password hashing,
 * and user onboarding.
 */
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserDAO userDAO;

    public AuthServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    public AuthServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User authenticate(String email, String rawPassword) throws AuthenticationException {
        if (email == null || email.trim().isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            throw new AuthenticationException("Email and password are required.");
        }

        String sanitizedEmail = email.trim().toLowerCase();
        Optional<User> userOpt = userDAO.findByEmail(sanitizedEmail);

        if (userOpt.isEmpty()) {
            logger.warn("Authentication failed: No user found with email {}", sanitizedEmail);
            throw new AuthenticationException("Invalid email or password.");
        }

        User user = userOpt.get();

        // Check account status
        if (user.getStatus() == UserStatus.INACTIVE) {
            logger.warn("Authentication rejected: Account is inactive for user ID {}", user.getId());
            throw new AuthenticationException("Your account is deactivated. Please contact the administrator.");
        }

        if (user.getStatus() == UserStatus.PENDING_APPROVAL) {
            logger.warn("Authentication rejected: Account pending approval for user ID {}", user.getId());
            throw new AuthenticationException("Your account registration is pending approval by the Placement Cell.");
        }

        // Verify BCrypt hash
        boolean passwordMatches = PasswordUtil.verifyPassword(rawPassword, user.getPasswordHash());
        if (!passwordMatches) {
            logger.warn("Authentication failed: Incorrect password for user {}", sanitizedEmail);
            throw new AuthenticationException("Invalid email or password.");
        }

        logger.info("Authentication successful for user ID: {} ({}) with role: {}", user.getId(), user.getEmail(), user.getRole());
        return user;
    }

    @Override
    public User register(String email, String rawPassword, Role role) throws AuthenticationException {
        if (email == null || email.trim().isEmpty()) {
            throw new AuthenticationException("Email address is required.");
        }

        String sanitizedEmail = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(sanitizedEmail).matches()) {
            throw new AuthenticationException("Please provide a valid email address.");
        }

        if (rawPassword == null || rawPassword.length() < 6) {
            throw new AuthenticationException("Password must be at least 6 characters in length.");
        }

        if (role == null) {
            throw new AuthenticationException("A valid user role must be selected.");
        }

        // Prevent unauthorized self-registration as TPO_ADMIN
        if (role == Role.TPO_ADMIN) {
            throw new AuthenticationException("Administrator accounts cannot be self-registered.");
        }

        if (userDAO.existsByEmail(sanitizedEmail)) {
            throw new AuthenticationException("An account with email " + sanitizedEmail + " already exists.");
        }

        // Generate salted BCrypt hash
        String passwordHash = PasswordUtil.hashPassword(rawPassword);

        User newUser = new User(sanitizedEmail, passwordHash, role, UserStatus.ACTIVE);
        return userDAO.save(newUser);
    }
}
