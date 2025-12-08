package com.cop_3060.service;

import com.cop_3060.dto.CreateResourceRequest;
import com.cop_3060.dto.ResourceDto;
import com.cop_3060.entity.Resource;
import com.cop_3060.entity.Category;
import com.cop_3060.entity.Location;
import com.cop_3060.repository.CategoryRepository;
import com.cop_3060.repository.LocationRepository;
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
public class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private ResourceService resourceService;

    @Test
    public void testCreateResource() {
        // Arrange
        CreateResourceRequest req = new CreateResourceRequest(
            "Microscope",
            "Optical microscope for lab use",
            1L,
            1L
        );

        Category category = new Category("Electronics", "Electronic");
        category.setId(1L);

        Location location = new Location("Science Hall", "101");
        location.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        Resource resource = new Resource("Microscope", "Optical microscope for lab use", location, category);
        resource.setId(1L);

        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        // Act
        ResourceDto result = resourceService.create(req);

        // Assert
        assertNotNull(result);
        assertEquals("Microscope", result.name());
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    public void testGetAllResources() {
        // Arrange
        Location location = new Location("Science Hall", "101");
        location.setId(1L);
        
        Category category = new Category("Equipment", "Lab");
        category.setId(1L);

        Resource res1 = new Resource("Microscope", "Optical microscope", location, category);
        res1.setId(1L);

        Resource res2 = new Resource("Projector", "Digital projector", location, category);
        res2.setId(2L);

        Page<Resource> page = new PageImpl<>(Arrays.asList(res1, res2), PageRequest.of(0, 10), 2);
        when(resourceRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        // Act
        Map<String, Object> result = resourceService.findAll(0, 10, "name", null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, ((java.util.List<?>) result.get("content")).size());
        assertEquals(0, result.get("page"));
        verify(resourceRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    public void testDeleteResource() {
        // Arrange
        when(resourceRepository.existsById(1L)).thenReturn(true);

        // Act
        resourceService.delete(1L);

        // Assert
        verify(resourceRepository, times(1)).existsById(1L);
        verify(resourceRepository, times(1)).deleteById(1L);
    }
}
