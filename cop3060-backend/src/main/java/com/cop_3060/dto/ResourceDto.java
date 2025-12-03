package com.cop_3060.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for Resource data transfer.
 * Includes nested LocationDto and CategoryDto for expanded relationships.
 */
public record ResourceDto(
        Long id,
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be under 100 characters")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must be under 255 characters")
        String description,

        @NotNull(message = "Location is required")
        LocationDto location,

        @NotNull(message = "Category is required")
        CategoryDto category
) {}
