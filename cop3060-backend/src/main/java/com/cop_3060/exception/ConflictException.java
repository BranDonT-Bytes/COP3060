package com.cop_3060.exception;

/**
 * Thrown when an operation conflicts with existing data.
 * E.g., when trying to delete a category that is still in use by resources.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
