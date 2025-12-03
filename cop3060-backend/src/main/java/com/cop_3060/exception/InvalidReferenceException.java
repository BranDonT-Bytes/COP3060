package com.cop_3060.exception;

/**
 * Thrown when an entity reference is invalid.
 * E.g., when creating a resource with a non-existent categoryId or locationId.
 */
public class InvalidReferenceException extends RuntimeException {
    public InvalidReferenceException(String message) {
        super(message);
    }

    public InvalidReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
