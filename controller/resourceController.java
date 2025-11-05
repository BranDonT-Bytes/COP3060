package com.cop_3060.controller;

import com.cop_3060.dto.*;
import com.cop_3060.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<ResourceDto> create(@Valid @RequestBody CreateResourceRequest request) {
        ResourceDto created = resourceService.create(request);
        URI location = URI.create("/api/resources/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q
    ) {
        Map<String, Object> envelope = resourceService.findAll(page, size, sort, category, q);
        return ResponseEntity.ok(envelope);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceDto> update(@PathVariable Long id, @Valid @RequestBody UpdateResourceRequest request) {
        return ResponseEntity.ok(resourceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
