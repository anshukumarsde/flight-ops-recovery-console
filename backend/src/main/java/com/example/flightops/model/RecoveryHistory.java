package com.example.flightops.model;

import java.time.Instant;

public record RecoveryHistory(
        Long id,
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
}
