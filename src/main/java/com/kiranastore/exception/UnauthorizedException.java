package com.kiranastore.exception;

/**
 * Thrown when authentication is required or credentials are not valid.
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Creates an unauthorized exception.
     *
     * @param message error message
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
