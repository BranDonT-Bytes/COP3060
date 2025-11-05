package com.cop_3060.dto;

/**
 * Represents a location entity.
 * Used both in resource nesting and in standalone location endpoints.
 */
public record LocationDto(
        Long id,
        String building,
        String room
) {}
