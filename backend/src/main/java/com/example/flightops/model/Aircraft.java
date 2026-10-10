package com.example.flightops.model;

public record Aircraft(
        String id,
        String type,
        AircraftStatus status,
        String currentAirport
) {
}
