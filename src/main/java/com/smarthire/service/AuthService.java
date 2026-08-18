package com.smarthire.service;

import com.smarthire.exception.AuthenticationException;
import com.smarthire.model.Role;
import com.smarthire.model.User;

/**
 * AuthService
 *
 * Business service interface governing user authentication and registration workflows.
 */
public interface AuthService {

    /**
     * Authenticates a user by email and plain-text password.
     *
     * @param email       User's email
     * @param rawPassword User's candidate plain password
     * @return Authenticated User POJO
     * @throws AuthenticationException if credentials fail, account is inactive, or validation error
     */
    User authenticate(String email, String rawPassword) throws AuthenticationException;

    /**
     * Registers a new user account with BCrypt password hashing.
     *
     * @param email       User email
     * @param rawPassword Plain text password
     * @param role        Role (STUDENT, RECRUITER, TPO_ADMIN)
     * @return Saved User POJO with generated ID
     * @throws AuthenticationException if email is duplicate, invalid, or validation fails
     */
    User register(String email, String rawPassword, Role role) throws AuthenticationException;
}
