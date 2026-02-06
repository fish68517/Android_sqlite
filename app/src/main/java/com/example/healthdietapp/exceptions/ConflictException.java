package com.example.healthdietapp.exceptions;

/**
 * ConflictException - Thrown when data conflicts occur
 * Used for schedule conflicts, duplicate entries, and other conflict scenarios
 */
public class ConflictException extends Exception {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
