package com.kiranastore.exception;

/**
 * Thrown when a request conflicts with the current resource state.
 */
public class ConflictException extends RuntimeException {

    /**
     * Creates a conflict exception.
     *
     * @param message error message
     */
    public ConflictException(String message) {
        super(message);
    }
}
