package com.cop_3060.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating a new Resource.
 * Used in POST /api/resources.
 */
public record CreateResourceRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be under 100 characters")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must be under 255 characters")
        String description,

        @NotNull(message = "locationId is required")
        Long locationId,

        @NotNull(message = "categoryId is required")
        Long categoryId
) {}
