package com.example.healthdietapp.exceptions;

/**
 * NetworkException - Thrown when network operations fail
 * Reserved for future use when network operations are implemented
 */
public class NetworkException extends Exception {

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
