package com.kiranastore.exception;

/**
 * Thrown when authentication fails due to invalid credentials.
 */
public class InvalidCredentialsException extends UnauthorizedException {

    /**
     * Creates an invalid credentials exception.
     *
     * @param message error message
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
