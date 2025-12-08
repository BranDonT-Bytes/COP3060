package com.cop_3060.service;

import com.cop_3060.dto.CategoryDto;
import com.cop_3060.dto.CreateCategoryRequest;
import com.cop_3060.dto.UpdateCategoryRequest;
import com.cop_3060.entity.Category;
import com.cop_3060.repository.CategoryRepository;
import com.cop_3060.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testCreateCategory() {
        // Arrange
        CreateCategoryRequest req = new CreateCategoryRequest("Electronics", "Electronic devices");
        Category category = new Category("Electronics", "Electronic devices");
        category.setId(1L);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryDto result = categoryService.create(req);

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.name());
        assertEquals("Electronic devices", result.description());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void testGetAllCategories() {
        // Arrange
        Category cat1 = new Category("Electronics", "Electronic devices");
        cat1.setId(1L);

        Category cat2 = new Category("Books", "Physical and digital books");
        cat2.setId(2L);

        Page<Category> page = new PageImpl<>(Arrays.asList(cat1, cat2), PageRequest.of(0, 10), 2);
        when(categoryRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        // Act
        Map<String, Object> result = categoryService.findAll(0, 10, "name");

        // Assert
        assertNotNull(result);
        assertEquals(2, ((java.util.List<?>) result.get("content")).size());
        assertEquals(0, result.get("page"));
        verify(categoryRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    public void testGetCategoryById() {
        // Arrange
        Category category = new Category("Electronics", "Electronic devices");
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        CategoryDto result = categoryService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.name());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateCategory() {
        // Arrange
        Category existingCategory = new Category("Electronics", "Electronic devices");
        existingCategory.setId(1L);

        UpdateCategoryRequest req = new UpdateCategoryRequest("Electronics Updated", "Updated electronic devices");

        Category updatedCategory = new Category("Electronics Updated", "Updated electronic devices");
        updatedCategory.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // Act
        CategoryDto result = categoryService.update(1L, req);

        // Assert
        assertNotNull(result);
        assertEquals("Electronics Updated", result.name());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void testDeleteCategory() {
        // Arrange
        Category category = new Category("Electronics", "Electronic devices");
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(resourceRepository.countByCategoryId(1L)).thenReturn(0);

        // Act
        categoryService.delete(1L);

        // Assert
        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
