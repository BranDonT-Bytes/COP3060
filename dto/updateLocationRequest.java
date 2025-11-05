package com.cop_3060.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for updating an existing Location.
 * Used in PUT /api/locations/{id}.
 */
public record UpdateLocationRequest(

        @NotBlank(message = "Building name is required")
        @Size(max = 100, message = "Building name must be under 100 characters")
        String building,

        @NotBlank(message = "Room is required")
        @Size(max = 50, message = "Room must be under 50 characters")
        String room
) {}
