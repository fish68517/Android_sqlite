package com.example.healthdietapp.exceptions;

/**
 * ValidationException - Thrown when data validation fails
 * Used for input validation errors
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
