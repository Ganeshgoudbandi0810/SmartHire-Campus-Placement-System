package com.smarthire.exception;

/**
 * ValidationException
 *
 * Custom business exception thrown when domain input bounds (marks, CGPA, years) are violated.
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
