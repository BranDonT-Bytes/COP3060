package com.cop_3060.service;

import com.cop_3060.dto.*;
import com.cop_3060.exception.ConflictException;
import com.cop_3060.exception.NotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LocationService {

    private final Map<Long, LocationDto> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong();

    private ResourceService resourceService; // circular dependency resolved by setter

    @PostConstruct
    public void logStartup() {
        System.out.println("LocationService initialized with " + store.size() + " locations.");
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public LocationDto create(CreateLocationRequest req) {
        Long id = idGen.incrementAndGet();
        LocationDto dto = new LocationDto(id, req.building(), req.room());
        store.put(id, dto);
        return dto;
    }

    public Map<String, Object> findAll(int page, int size, String sort) {
        List<LocationDto> list = new ArrayList<>(store.values());
        list.sort(Comparator.comparing(LocationDto::building, Comparator.nullsLast(String::compareToIgnoreCase)));

        int from = page * size;
        int to = Math.min(from + size, list.size());
        List<LocationDto> content = from >= list.size() ? List.of() : list.subList(from, to);

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("content", content);
        envelope.put("page", page);
        envelope.put("size", size);
        envelope.put("totalElements", list.size());
        envelope.put("totalPages", (int) Math.ceil((double) list.size() / size));
        return envelope;
    }

    public LocationDto findById(Long id) {
        LocationDto dto = store.get(id);
        if (dto == null) throw new NotFoundException("Location %d not found".formatted(id));
        return dto;
    }

    public LocationDto update(Long id, UpdateLocationRequest req) {
        if (!store.containsKey(id))
            throw new NotFoundException("Location %d not found".formatted(id));
        LocationDto dto = new LocationDto(id, req.building(), req.room());
        store.put(id, dto);
        return dto;
    }

    public void delete(Long id) {
        if (!store.containsKey(id))
            throw new NotFoundException("Location %d not found".formatted(id));

        int count = resourceService != null ? resourceService.countByLocation(id) : 0;
        if (count > 0)
            throw new ConflictException("Location %d is in use by %d resources".formatted(id, count));

        store.remove(id);
    }

    public boolean exists(Long id) {
        return store.containsKey(id);
    }
}
