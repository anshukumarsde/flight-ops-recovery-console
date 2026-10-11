package com.example.flightops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "recovery_history")
public class RecoveryHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_id", nullable = false)
    private String flightId;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @Column(name = "departure_airport", nullable = false)
    private String departureAirport;

    @Column(name = "arrival_airport", nullable = false)
    private String arrivalAirport;

    @Column(name = "disruption_reason")
    private String disruptionReason;

    @Column(name = "previous_aircraft_id", nullable = false)
    private String previousAircraftId;

    @Column(name = "assigned_aircraft_id", nullable = false)
    private String assignedAircraftId;

    @Column(name = "aircraft_current_airport", nullable = false)
    private String aircraftCurrentAirport;

    @Column(name = "reposition_required", nullable = false)
    private boolean repositionRequired;

    @Column(name = "recovered_at", nullable = false)
    private Instant recoveredAt;

    protected RecoveryHistoryEntity() {
    }

    public RecoveryHistoryEntity(
            String flightId,
            String flightNumber,
            String departureAirport,
            String arrivalAirport,
            String disruptionReason,
            String previousAircraftId,
            String assignedAircraftId,
            String aircraftCurrentAirport,
            boolean repositionRequired,
            Instant recoveredAt
    ) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.disruptionReason = disruptionReason;
        this.previousAircraftId = previousAircraftId;
        this.assignedAircraftId = assignedAircraftId;
        this.aircraftCurrentAirport = aircraftCurrentAirport;
        this.repositionRequired = repositionRequired;
        this.recoveredAt = recoveredAt;
    }

    public Long getId() {
        return id;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public String getDisruptionReason() {
        return disruptionReason;
    }

    public String getPreviousAircraftId() {
        return previousAircraftId;
    }

    public String getAssignedAircraftId() {
        return assignedAircraftId;
    }

    public String getAircraftCurrentAirport() {
        return aircraftCurrentAirport;
    }

    public boolean isRepositionRequired() {
        return repositionRequired;
    }

    public Instant getRecoveredAt() {
        return recoveredAt;
    }
}
