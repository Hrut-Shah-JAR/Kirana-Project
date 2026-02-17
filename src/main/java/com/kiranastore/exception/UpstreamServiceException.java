package com.kiranastore.exception;

/**
 * Thrown when an upstream dependency fails to provide required data.
 */
public class UpstreamServiceException extends RuntimeException {

    /**
     * Creates an upstream service exception.
     *
     * @param message error message
     */
    public UpstreamServiceException(String message) {
        super(message);
    }
}
