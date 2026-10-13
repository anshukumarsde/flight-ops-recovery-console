package com.example.flightops.api;

public record PartsShipmentUpdateRequest(
        String status,
        String location,
        String estimatedDelivery,
        String note
) {
}
