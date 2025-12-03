package com.cop_3060.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing Category.
 * Used in PUT /api/categories/{id}.
 */
public record UpdateCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must be under 100 characters")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must be under 255 characters")
        String description
) {}
