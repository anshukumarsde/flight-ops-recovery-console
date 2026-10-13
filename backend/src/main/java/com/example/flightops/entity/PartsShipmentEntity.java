package com.example.flightops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "parts_shipments")
public class PartsShipmentEntity {
    @Id
    private String id;

    @Column(name = "tracking_number", nullable = false, unique = true)
    private String trackingNumber;

    @Column(name = "part_number", nullable = false)
    private String partNumber;

    @Column(name = "part_name", nullable = false)
    private String partName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String supplier;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String status;

    @Column(name = "current_location", nullable = false)
    private String currentLocation;

    @Column(name = "estimated_delivery", nullable = false)
    private LocalDate estimatedDelivery;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PartsShipmentEntity() {
    }

    public PartsShipmentEntity(
            String id,
            String trackingNumber,
            String partNumber,
            String partName,
            int quantity,
            String supplier,
            String origin,
            String destination,
            String status,
            String currentLocation,
            LocalDate estimatedDelivery,
            Instant updatedAt
    ) {
        this.id = id;
        this.trackingNumber = trackingNumber;
        this.partNumber = partNumber;
        this.partName = partName;
        this.quantity = quantity;
        this.supplier = supplier;
        this.origin = origin;
        this.destination = destination;
        this.status = status;
        this.currentLocation = currentLocation;
        this.estimatedDelivery = estimatedDelivery;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public LocalDate getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateTracking(String status, String currentLocation, LocalDate estimatedDelivery, Instant updatedAt) {
        this.status = status;
        this.currentLocation = currentLocation;
        this.estimatedDelivery = estimatedDelivery;
        this.updatedAt = updatedAt;
    }
}
