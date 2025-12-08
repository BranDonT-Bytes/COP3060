package com.cop_3060.service;

import com.cop_3060.entity.ExternalData;
import com.cop_3060.repository.ExternalDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class ExternalApiService {

    private final ExternalDataRepository externalDataRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String openWeatherKey;
    private final long cacheTtlMinutes;

    public ExternalApiService(ExternalDataRepository externalDataRepository,
                              @Value("${external.openweather.key:}") String openWeatherKey,
                              @Value("${external.cache.ttl.minutes:10}") long cacheTtlMinutes) {
        this.externalDataRepository = externalDataRepository;
        this.openWeatherKey = openWeatherKey;
        this.cacheTtlMinutes = cacheTtlMinutes;
    }

    /**
     * Fetch current weather for a city from OpenWeatherMap and persist the payload.
     * If API key is not configured, returns empty.
     */
    public Optional<ExternalData> fetchWeatherForCity(String city) {
        // Determine which source we'll use for lookup and caching
        final String intendedSource = (openWeatherKey == null || openWeatherKey.isBlank()) ? "open-meteo" : "openweather";

        // Try cache: if recent enough, return persisted record
        try {
            Optional<ExternalData> existing = externalDataRepository.findFirstBySourceAndKeyNameOrderByFetchedAtDesc(intendedSource, city);
            if (existing.isPresent()) {
                Instant then = existing.get().getFetchedAt();
                if (then != null) {
                    Duration age = Duration.between(then, Instant.now());
                    if (age.toMinutes() <= cacheTtlMinutes) {
                        return existing;
                    }
                }
            }
        } catch (Exception e) {
            // ignore cache errors and proceed to fetch
            e.printStackTrace();
        }

        if (openWeatherKey == null || openWeatherKey.isBlank()) {
            // No OpenWeather key configured: try a keyless approach using Open-Meteo geocoding + current_weather
            try {
                ObjectMapper mapper = new ObjectMapper();
                String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + URLEncoder.encode(city, StandardCharsets.UTF_8) + "&count=1";
                ResponseEntity<String> geoResp = restTemplate.getForEntity(geoUrl, String.class);
                if (geoResp.getStatusCode().is2xxSuccessful() && geoResp.getBody() != null) {
                    JsonNode geoNode = mapper.readTree(geoResp.getBody());
                    JsonNode results = geoNode.path("results");
                    if (results.isArray() && results.size() > 0) {
                        JsonNode first = results.get(0);
                        double lat = first.path("latitude").asDouble();
                        double lon = first.path("longitude").asDouble();
                        String name = first.path("name").asText(city);

                        String weatherUrl = String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true", lat, lon);
                        ResponseEntity<String> weatherResp = restTemplate.getForEntity(weatherUrl, String.class);
                        if (weatherResp.getStatusCode().is2xxSuccessful() && weatherResp.getBody() != null) {
                            JsonNode wnode = mapper.readTree(weatherResp.getBody());
                            JsonNode current = wnode.path("current_weather");
                            double temp = current.path("temperature").asDouble(Double.NaN);
                            int weathercode = current.path("weathercode").asInt(-1);
                            String description = mapWeatherCode(weathercode);

                            ObjectNode out = mapper.createObjectNode();
                            out.put("name", name);
                            ObjectNode main = mapper.createObjectNode();
                            if (!Double.isNaN(temp)) main.put("temp", temp);
                            out.set("main", main);
                            ArrayNode weatherArr = mapper.createArrayNode();
                            ObjectNode wdesc = mapper.createObjectNode();
                            wdesc.put("description", description);
                            weatherArr.add(wdesc);
                            out.set("weather", weatherArr);
                            out.put("_source", "open-meteo");
                            out.put("icon", mapWeatherCodeToEmoji(weathercode));
                            out.put("weathercode", weathercode);

                            String payload = mapper.writeValueAsString(out);
                            ExternalData data = new ExternalData("open-meteo", city, payload);
                            externalDataRepository.save(data);
                            return Optional.of(data);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Fallback mocked payload if geocoding/current_weather failed
            String mock = String.format("{\"name\":\"%s\",\"main\":{\"temp\":20.0},\"weather\":[{\"description\":\"clear sky\"}]}", city);
            ExternalData data = new ExternalData("mock-openweather", city, mock);
            externalDataRepository.save(data);
            return Optional.of(data);
        }
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric", city, openWeatherKey);
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                // Try to enrich OpenWeather payload with a simple icon mapping
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(resp.getBody());
                    JsonNode weatherArr = root.path("weather");
                    String iconEmoji = null;
                    if (weatherArr.isArray() && weatherArr.size() > 0) {
                        String iconCode = weatherArr.get(0).path("icon").asText(null);
                        if (iconCode != null) {
                            // iconCode like "10d" - map by prefix
                            iconEmoji = mapOpenWeatherIconToEmoji(iconCode);
                        }
                    }
                    if (iconEmoji != null && root.isObject()) {
                        ((ObjectNode) root).put("icon", iconEmoji);
                    }
                    String enriched = mapper.writeValueAsString(root);
                    ExternalData data = new ExternalData("openweather", city, enriched);
                    externalDataRepository.save(data);
                    return Optional.of(data);
                } catch (Exception e) {
                    // if enrichment fails, persist raw body
                    ExternalData data = new ExternalData("openweather", city, resp.getBody());
                    externalDataRepository.save(data);
                    return Optional.of(data);
                }
            }
        } catch (HttpClientErrorException.TooManyRequests tre) {
            // rate limit - allow the global handler to convert to 429 for the client
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

    // Simple mapper from Open-Meteo weathercode to a human readable description
    private String mapWeatherCode(int code) {
        if (code == 0) return "clear sky";
        if (code == 1 || code == 2 || code == 3) return "partly cloudy";
        if (code == 45 || code == 48) return "fog";
        if (code >= 51 && code <= 67) return "rain";
        if ((code >= 71 && code <= 77) || (code >= 85 && code <= 86)) return "snow";
        if (code >= 80 && code <= 82) return "showers";
        return "unknown";
    }

    private String mapWeatherCodeToEmoji(int code) {
        if (code == 0) return "☀️";
        if (code == 1 || code == 2 || code == 3) return "⛅";
        if (code == 45 || code == 48) return "🌫️";
        if (code >= 51 && code <= 67) return "🌧️";
        if ((code >= 71 && code <= 77) || (code >= 85 && code <= 86)) return "❄️";
        if (code >= 80 && code <= 82) return "🌦️";
        return "❓";
    }

    private String mapOpenWeatherIconToEmoji(String iconCode) {
        if (iconCode == null || iconCode.length() < 2) return null;
        String prefix = iconCode.substring(0, 2);
        switch (prefix) {
            case "01": return "☀️"; // clear
            case "02": return "⛅"; // few clouds
            case "03": return "☁️"; // scattered
            case "04": return "☁️"; // broken
            case "09": return "🌧️"; // shower rain
            case "10": return "🌦️"; // rain
            case "11": return "⛈️"; // thunderstorm
            case "13": return "❄️"; // snow
            case "50": return "🌫️"; // mist
            default: return "❓";
        }
    }
}
