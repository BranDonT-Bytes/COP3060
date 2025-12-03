package com.cop_3060.service;

import com.cop_3060.dto.CategoryDto;
import com.cop_3060.dto.CreateResourceRequest;
import com.cop_3060.dto.LocationDto;
import com.cop_3060.dto.ResourceDto;
import com.cop_3060.dto.UpdateResourceRequest;
import com.cop_3060.entity.Category;
import com.cop_3060.entity.Location;
import com.cop_3060.entity.Resource;
import com.cop_3060.exception.InvalidReferenceException;
import com.cop_3060.exception.NotFoundException;
import com.cop_3060.repository.CategoryRepository;
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
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    public ResourceService(ResourceRepository resourceRepository,
                          LocationRepository locationRepository,
                          CategoryRepository categoryRepository) {
        this.resourceRepository = resourceRepository;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void logStartup() {
        System.out.println("ResourceService initialized with " + resourceRepository.count() + " resources.");
    }

    public ResourceDto create(CreateResourceRequest req) {
        Location location = locationRepository.findById(req.locationId())
                .orElseThrow(() -> new InvalidReferenceException("Invalid locationId: " + req.locationId()));
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new InvalidReferenceException("Invalid categoryId: " + req.categoryId()));

        Resource resource = new Resource(req.name(), req.description(), location, category);
        Resource saved = resourceRepository.save(resource);
        return toDto(saved);
    }

    public Map<String, Object> findAll(int page, int size, String sort, String category, String q) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<Resource> result;

        if (category != null && !category.isBlank()) {
            result = resourceRepository.findByCategoryId(Long.parseLong(category), pageable);
        } else if (q != null && !q.isBlank()) {
            result = resourceRepository.searchByName(q, pageable);
        } else {
            result = resourceRepository.findAll(pageable);
        }

        List<ResourceDto> content = result.getContent()
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

    public ResourceDto findById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource " + id + " not found"));
        return toDto(resource);
    }

    public ResourceDto update(Long id, UpdateResourceRequest req) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource " + id + " not found"));

        Location location = locationRepository.findById(req.locationId())
                .orElseThrow(() -> new InvalidReferenceException("Invalid locationId: " + req.locationId()));
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new InvalidReferenceException("Invalid categoryId: " + req.categoryId()));

        resource.setName(req.name());
        resource.setDescription(req.description());
        resource.setLocation(location);
        resource.setCategory(category);
        Resource updated = resourceRepository.save(resource);
        return toDto(updated);
    }

    public void delete(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new NotFoundException("Resource " + id + " not found");
        }
        resourceRepository.deleteById(id);
    }

    public int countByLocation(Long locationId) {
        return resourceRepository.countByLocationId(locationId);
    }

    public int countByCategory(Long categoryId) {
        return resourceRepository.countByCategoryId(categoryId);
    }

    private ResourceDto toDto(Resource resource) {
        LocationDto locDto = new LocationDto(
                resource.getLocation().getId(),
                resource.getLocation().getBuilding(),
                resource.getLocation().getRoom()
        );
        CategoryDto catDto = new CategoryDto(
                resource.getCategory().getId(),
                resource.getCategory().getName(),
                resource.getCategory().getDescription()
        );
        return new ResourceDto(
                resource.getId(),
                resource.getName(),
                resource.getDescription(),
                locDto,
                catDto
        );
    }
}
