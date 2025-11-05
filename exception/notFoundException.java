package com.cop_3060.exception;

/**
 * Thrown when an entity (resource, location, or category)
 * cannot be found in the in-memory data store.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
