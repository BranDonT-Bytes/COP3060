package com.cop_3060.service;

import com.cop_3060.dto.LocationDto;
import com.cop_3060.dto.CreateLocationRequest;
import com.cop_3060.dto.UpdateLocationRequest;
import com.cop_3060.entity.Location;
import com.cop_3060.exception.ConflictException;
import com.cop_3060.exception.NotFoundException;
import com.cop_3060.repository.LocationRepository;
import com.cop_3060.repository.ResourceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final ResourceRepository resourceRepository;

    public LocationService(LocationRepository locationRepository, ResourceRepository resourceRepository) {
        this.locationRepository = locationRepository;
        this.resourceRepository = resourceRepository;
    }

    @PostConstruct
    public void logStartup() {
        System.out.println("LocationService initialized with " + locationRepository.count() + " locations.");
    }

    public LocationDto create(CreateLocationRequest req) {
        Location location = new Location(req.building(), req.room());
        Location saved = locationRepository.save(location);
        return toDto(saved);
    }

    public Map<String, Object> findAll(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "building"));
        Page<Location> result = locationRepository.findAll(pageable);

        List<LocationDto> content = result.getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("content", content);
        envelope.put("page", page);
        envelope.put("size", size);
        envelope.put("totalElements", result.getTotalElements());
        envelope.put("totalPages", result.getTotalPages());
        return envelope;
    }

    public LocationDto findById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location " + id + " not found"));
        return toDto(location);
    }

    public LocationDto update(Long id, UpdateLocationRequest req) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location " + id + " not found"));
        location.setBuilding(req.building());
        location.setRoom(req.room());
        Location updated = locationRepository.save(location);
        return toDto(updated);
    }

    public void delete(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location " + id + " not found"));

        int count = resourceRepository.countByLocationId(id);
        if (count > 0) {
            throw new ConflictException("Location " + id + " is in use by " + count + " resources");
        }

        locationRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return locationRepository.existsById(id);
    }

    private LocationDto toDto(Location location) {
        return new LocationDto(location.getId(), location.getBuilding(), location.getRoom());
    }
}
