package com.cop_3060.service;

import com.cop_3060.dto.CategoryDto;
import com.cop_3060.dto.CreateCategoryRequest;
import com.cop_3060.dto.UpdateCategoryRequest;
import com.cop_3060.entity.Category;
import com.cop_3060.exception.ConflictException;
import com.cop_3060.exception.NotFoundException;
import com.cop_3060.repository.CategoryRepository;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ResourceRepository resourceRepository;

    public CategoryService(CategoryRepository categoryRepository, ResourceRepository resourceRepository) {
        this.categoryRepository = categoryRepository;
        this.resourceRepository = resourceRepository;
    }

    @PostConstruct
    public void logStartup() {
        System.out.println("CategoryService initialized with " + categoryRepository.count() + " categories.");
    }

    public CategoryDto create(CreateCategoryRequest req) {
        Category category = new Category(req.name(), req.description());
        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }

    public Map<String, Object> findAll(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<Category> result = categoryRepository.findAll(pageable);

        List<CategoryDto> content = result.getContent()
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

    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category " + id + " not found"));
        return toDto(category);
    }

    public CategoryDto update(Long id, UpdateCategoryRequest req) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category " + id + " not found"));
        category.setName(req.name());
        category.setDescription(req.description());
        Category updated = categoryRepository.save(category);
        return toDto(updated);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category " + id + " not found"));

        int count = resourceRepository.countByCategoryId(id);
        if (count > 0) {
            throw new ConflictException("Category " + id + " is in use by " + count + " resources");
        }

        categoryRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return categoryRepository.existsById(id);
    }

    private CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }
}
