package com.cop_3060.exception;

/**
 * Thrown when an operation cannot complete due to a data conflict.
 * Example: attempting to delete a Location or Category that is
 * still referenced by existing Resources.
 *
 * Results in HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
