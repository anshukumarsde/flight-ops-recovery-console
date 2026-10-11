package com.example.flightops.entity;

import com.example.flightops.model.AircraftStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aircraft")
public class AircraftEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "type", nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AircraftStatus status;

    @Column(name = "current_airport", nullable = false)
    private String currentAirport;

    protected AircraftEntity() {
    }

    public AircraftEntity(String id, String type, AircraftStatus status, String currentAirport) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentAirport = currentAirport;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AircraftStatus getStatus() {
        return status;
    }

    public void setStatus(AircraftStatus status) {
        this.status = status;
    }

    public String getCurrentAirport() {
        return currentAirport;
    }

    public void setCurrentAirport(String currentAirport) {
        this.currentAirport = currentAirport;
    }
}
