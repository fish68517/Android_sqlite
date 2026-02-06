package com.example.healthdietapp.exceptions;

/**
 * DatabaseException - Thrown when database operations fail
 * Used for CRUD operation errors and database connectivity issues
 */
public class DatabaseException extends Exception {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
