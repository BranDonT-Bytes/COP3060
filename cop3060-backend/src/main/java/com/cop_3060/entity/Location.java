package com.cop_3060.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Building is required")
    @Size(max = 100, message = "Building name must be under 100 characters")
    @Column(nullable = false)
    private String building;

    @NotBlank(message = "Room is required")
    @Size(max = 50, message = "Room must be under 50 characters")
    @Column(nullable = false)
    private String room;

    public Location() {}

    public Location(String building, String room) {
        this.building = building;
        this.room = room;
    }

    public Location(Long id, String building, String room) {
        this.id = id;
        this.building = building;
        this.room = room;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", building='" + building + '\'' +
                ", room='" + room + '\'' +
                '}';
    }
}
