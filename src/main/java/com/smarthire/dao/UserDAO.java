package com.smarthire.dao;

import com.smarthire.model.User;
import com.smarthire.model.UserStatus;

import java.util.List;
import java.util.Optional;

/**
 * UserDAO
 *
 * Data Access Object interface for user identity management.
 */
public interface UserDAO {

    /**
     * Saves a new user record to the database and populates the auto-generated primary key ID.
     */
    User save(User user);

    /**
     * Finds a user by their unique email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their primary key ID.
     */
    Optional<User> findById(int id);

    /**
     * Checks whether an account with the given email already exists.
     */
    boolean existsByEmail(String email);

    /**
     * Updates a user's password hash.
     */
    boolean updatePassword(int id, String newPasswordHash);

    /**
     * Updates a user's account lifecycle status (ACTIVE, INACTIVE, PENDING_APPROVAL).
     */
    boolean updateStatus(int id, UserStatus status);

    /**
     * Retrieves all users (for administrative management).
     */
    List<User> findAll();
}
