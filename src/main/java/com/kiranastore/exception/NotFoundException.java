package com.kiranastore.exception;

/**
 * Thrown when a requested resource cannot be found.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Creates a not found exception.
     *
     * @param message error message
     */
    public NotFoundException(String message) {
        super(message);
    }
}
