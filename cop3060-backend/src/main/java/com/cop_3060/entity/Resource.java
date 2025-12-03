package com.cop_3060.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be under 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must be under 255 characters")
    @Column(nullable = false, length = 255)
    private String description;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Resource() {}

    public Resource(String name, String description, Location location, Category category) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.category = category;
    }

    public Resource(Long id, String name, String description, Location location, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location=" + location +
                ", category=" + category +
                '}';
    }
}
