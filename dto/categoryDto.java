package com.cop_3060.dto;

/**
 * Represents a category entity.
 * Used both in resource nesting and in standalone category endpoints.
 */
public record CategoryDto(
        Long id,
        String name,
        String description
) {}
