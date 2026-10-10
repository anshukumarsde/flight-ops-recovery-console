package com.example.flightops.model;

public record Flight(
        String id,
        String flightNumber,
        String departureAirport,
        String arrivalAirport,
        String aircraftType,
        String assignedAircraftId,
        String status,
        String disruptionReason
) {
}
