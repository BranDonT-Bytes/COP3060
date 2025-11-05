package com.cop_3060.dto;

/**
 * The primary Resource DTO.
 * Represents a resource record returned from API endpoints.
 * Includes nested LocationDto and CategoryDto for expanded relationships.
 */
public record ResourceDto(
        Long id,
        String name,
        String description,
        LocationDto location,
        CategoryDto category
) {}
