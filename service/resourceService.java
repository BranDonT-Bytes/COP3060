package com.cop_3060.service;

import com.cop_3060.dto.*;
import com.cop_3060.exception.ConflictException;
import com.cop_3060.exception.InvalidReferenceException;
import com.cop_3060.exception.NotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final Map<Long, ResourceDto> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong();

    private final LocationService locationService;
    private final CategoryService categoryService;

    public ResourceService(LocationService locationService, CategoryService categoryService) {
        this.locationService = locationService;
        this.categoryService = categoryService;
    }

    @PostConstruct
    public void logStartup() {
        System.out.println("ResourceService initialized with " + store.size() + " resources.");
    }

    public ResourceDto create(CreateResourceRequest req) {
        if (!locationService.exists(req.locationId())) {
            throw new InvalidReferenceException("Invalid locationId: " + req.locationId());
        }
        if (!categoryService.exists(req.categoryId())) {
            throw new InvalidReferenceException("Invalid categoryId: " + req.categoryId());
        }

        Long id = idGen.incrementAndGet();
        LocationDto loc = locationService.findById(req.locationId());
        CategoryDto cat = categoryService.findById(req.categoryId());
        ResourceDto dto = new ResourceDto(id, req.name(), req.description(), loc, cat);
        store.put(id, dto);
        return dto;
    }

    public Map<String, Object> findAll(int page, int size, String sort, String category, String q) {
        List<ResourceDto> list = new ArrayList<>(store.values());

        if (category != null && !category.isBlank()) {
            list = list.stream()
                    .filter(r -> r.category().name().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }
        if (q != null && !q.isBlank()) {
            list = list.stream()
                    .filter(r -> r.name().toLowerCase().contains(q.toLowerCase()))
                    .collect(Collectors.toList());
        }

        list.sort(Comparator.comparing(ResourceDto::name, Comparator.nullsLast(String::compareToIgnoreCase)));

        int from = page * size;
        int to = Math.min(from + size, list.size());
        List<ResourceDto> content = from >= list.size() ? List.of() : list.subList(from, to);

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("content", content);
        envelope.put("page", page);
        envelope.put("size", size);
        envelope.put("totalElements", list.size());
        envelope.put("totalPages", (int) Math.ceil((double) list.size() / size));
        return envelope;
    }

    public ResourceDto findById(Long id) {
        ResourceDto dto = store.get(id);
        if (dto == null) throw new NotFoundException("Resource %d not found".formatted(id));
        return dto;
    }

    public ResourceDto update(Long id, UpdateResourceRequest req) {
        if (!store.containsKey(id))
            throw new NotFoundException("Resource %d not found".formatted(id));

        if (!locationService.exists(req.locationId())) {
            throw new InvalidReferenceException("Invalid locationId: " + req.locationId());
        }
        if (!categoryService.exists(req.categoryId())) {
            throw new InvalidReferenceException("Invalid categoryId: " + req.categoryId());
        }

        LocationDto loc = locationService.findById(req.locationId());
        CategoryDto cat = categoryService.findById(req.categoryId());
        ResourceDto updated = new ResourceDto(id, req.name(), req.description(), loc, cat);
        store.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (!store.containsKey(id))
            throw new NotFoundException("Resource %d not found".formatted(id));
        store.remove(id);
    }

    public int countByLocation(Long locationId) {
        return (int) store.values().stream()
                .filter(r -> r.location().id().equals(locationId))
                .count();
    }

    public int countByCategory(Long categoryId) {
        return (int) store.values().stream()
                .filter(r -> r.category().id().equals(categoryId))
                .count();
    }
}
