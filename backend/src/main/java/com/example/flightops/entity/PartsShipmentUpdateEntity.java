package com.example.flightops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "parts_shipment_updates")
public class PartsShipmentUpdateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipment_id", nullable = false)
    private String shipmentId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String note;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected PartsShipmentUpdateEntity() {
    }

    public PartsShipmentUpdateEntity(String shipmentId, String status, String location, String note, Instant occurredAt) {
        this.shipmentId = shipmentId;
        this.status = status;
        this.location = location;
        this.note = note;
        this.occurredAt = occurredAt;
    }

    public Long getId() {
        return id;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public String getNote() {
        return note;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
