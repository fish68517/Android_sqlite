package com.example.healthdietapp.exceptions;

/**
 * AuthenticationException - Thrown when authentication operations fail
 * Used for login, registration, and credential verification errors
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
