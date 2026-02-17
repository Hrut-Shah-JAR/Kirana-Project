package com.kiranastore.exception;

/**
 * Thrown when a request payload or parameter is invalid.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Creates a bad request exception.
     *
     * @param message error message
     */
    public BadRequestException(String message) {
        super(message);
    }
}
