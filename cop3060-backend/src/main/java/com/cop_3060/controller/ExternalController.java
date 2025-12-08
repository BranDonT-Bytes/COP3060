package com.cop_3060.controller;

import com.cop_3060.entity.ExternalData;
import com.cop_3060.service.ExternalApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/external")
public class ExternalController {

    private final ExternalApiService externalApiService;

    public ExternalController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/weather")
    public ResponseEntity<?> weather(@RequestParam String city) {
        Optional<ExternalData> fetched = externalApiService.fetchWeatherForCity(city);
        if (fetched.isPresent()) {
            return ResponseEntity.ok().body(fetched.get());
        } else {
            // consistent JSON shape for errors
            return ResponseEntity.status(503).body(Map.of("error", "External API key not configured or failed to fetch data"));
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> latest(@RequestParam String source, @RequestParam String key) {
        Optional<ExternalData> data = externalApiService.getLatest(source, key);
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
