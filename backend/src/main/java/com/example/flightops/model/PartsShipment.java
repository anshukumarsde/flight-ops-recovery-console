package com.example.flightops.model;

import java.time.Instant;
import java.time.LocalDate;

public record PartsShipment(
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
}
