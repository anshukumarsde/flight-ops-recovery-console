package com.example.flightops.model;

public record RecoveryOption(
        String aircraftId,
        String aircraftType,
        String currentAirport,
        boolean requiresRepositioning,
        int rank
) {
}
