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
public class CategoryService {

    private final Map<Long, CategoryDto> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong();

    private ResourceService resourceService; // circular dependency handled by setter

    @PostConstruct
    public void logStartup() {
        System.out.println("CategoryService initialized with " + store.size() + " categories.");
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public CategoryDto create(CreateCategoryRequest req) {
        Long id = idGen.incrementAndGet();
        CategoryDto dto = new CategoryDto(id, req.name(), req.description());
        store.put(id, dto);
        return dto;
    }

    public Map<String, Object> findAll(int page, int size, String sort) {
        List<CategoryDto> list = new ArrayList<>(store.values());
        list.sort(Comparator.comparing(CategoryDto::name, Comparator.nullsLast(String::compareToIgnoreCase)));

        int from = page * size;
        int to = Math.min(from + size, list.size());
        List<CategoryDto> content = from >= list.size() ? List.of() : list.subList(from, to);

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("content", content);
        envelope.put("page", page);
        envelope.put("size", size);
        envelope.put("totalElements", list.size());
        envelope.put("totalPages", (int) Math.ceil((double) list.size() / size));
        return envelope;
    }

    public CategoryDto findById(Long id) {
        CategoryDto dto = store.get(id);
        if (dto == null) throw new NotFoundException("Category %d not found".formatted(id));
        return dto;
    }

    public CategoryDto update(Long id, UpdateCategoryRequest req) {
        if (!store.containsKey(id))
            throw new NotFoundException("Category %d not found".formatted(id));
        CategoryDto dto = new CategoryDto(id, req.name(), req.description());
        store.put(id, dto);
        return dto;
    }

    public void delete(Long id) {
        if (!store.containsKey(id))
            throw new NotFoundException("Category %d not found".formatted(id));

        int count = resourceService != null ? resourceService.countByCategory(id) : 0;
        if (count > 0)
            throw new ConflictException("Category %d is in use by %d resources".formatted(id, count));

        store.remove(id);
    }

    public boolean exists(Long id) {
        return store.containsKey(id);
    }
}
