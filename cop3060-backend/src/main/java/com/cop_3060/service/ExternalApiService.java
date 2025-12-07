package com.cop_3060.service;

import com.cop_3060.entity.ExternalData;
import com.cop_3060.repository.ExternalDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ExternalApiService {

    private final ExternalDataRepository externalDataRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String openWeatherKey;

    public ExternalApiService(ExternalDataRepository externalDataRepository,
                              @Value("${external.openweather.key:}") String openWeatherKey) {
        this.externalDataRepository = externalDataRepository;
        this.openWeatherKey = openWeatherKey;
    }

    /**
     * Fetch current weather for a city from OpenWeatherMap and persist the payload.
     * If API key is not configured, returns empty.
     */
    public Optional<ExternalData> fetchWeatherForCity(String city) {
        if (openWeatherKey == null || openWeatherKey.isBlank()) {
            return Optional.empty();
        }
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric", city, openWeatherKey);
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                ExternalData data = new ExternalData("openweather", city, resp.getBody());
                externalDataRepository.save(data);
                return Optional.of(data);
            }
        } catch (HttpClientErrorException.TooManyRequests tre) {
            // rate limit
            throw tre;
        } catch (Exception ex) {
            // log and swallow
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<ExternalData> getLatest(String source, String key) {
        return externalDataRepository.findFirstBySourceAndKeyNameOrderByFetchedAtDesc(source, key);
    }
}
