package com.kiranastore.exception;

/**
 * Thrown when the authenticated user is not allowed to access a resource.
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Creates a forbidden exception.
     *
     * @param message error message
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
