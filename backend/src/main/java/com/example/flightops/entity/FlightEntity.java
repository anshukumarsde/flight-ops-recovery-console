package com.example.flightops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "flights")
public class FlightEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @Column(name = "departure_airport", nullable = false)
    private String departureAirport;

    @Column(name = "arrival_airport", nullable = false)
    private String arrivalAirport;

    @Column(name = "aircraft_type", nullable = false)
    private String aircraftType;

    @Column(name = "assigned_aircraft_id", nullable = false)
    private String assignedAircraftId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "disruption_reason")
    private String disruptionReason;

    protected FlightEntity() {
    }

    public FlightEntity(
            String id,
            String flightNumber,
            String departureAirport,
            String arrivalAirport,
            String aircraftType,
            String assignedAircraftId,
            String status,
            String disruptionReason
    ) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.aircraftType = aircraftType;
        this.assignedAircraftId = assignedAircraftId;
        this.status = status;
        this.disruptionReason = disruptionReason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getAssignedAircraftId() {
        return assignedAircraftId;
    }

    public void setAssignedAircraftId(String assignedAircraftId) {
        this.assignedAircraftId = assignedAircraftId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisruptionReason() {
        return disruptionReason;
    }

    public void setDisruptionReason(String disruptionReason) {
        this.disruptionReason = disruptionReason;
    }
}
