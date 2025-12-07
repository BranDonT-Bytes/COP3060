package com.cop_3060.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "external_data")
public class ExternalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String keyName; // e.g., city or resourceId

    @Lob
    @Column(nullable = false)
    private String payload; // raw JSON payload

    @Column(nullable = false)
    private Instant fetchedAt = Instant.now();

    public ExternalData() {}

    public ExternalData(String source, String keyName, String payload) {
        this.source = source;
        this.keyName = keyName;
        this.payload = payload;
        this.fetchedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Instant getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(Instant fetchedAt) { this.fetchedAt = fetchedAt; }
}
