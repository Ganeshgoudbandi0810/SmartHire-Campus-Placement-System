package com.smarthire.exception;

/**
 * AuthenticationException
 *
 * Custom business exception thrown when user authentication or registration fails.
 */
public class AuthenticationException extends Exception {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
