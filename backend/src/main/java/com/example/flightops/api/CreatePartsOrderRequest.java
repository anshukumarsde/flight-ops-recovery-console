package com.example.flightops.api;

public record CreatePartsOrderRequest(
        String partNumber,
        String partName,
        Integer quantity,
        String supplier,
        String origin,
        String destination,
        String estimatedDelivery
) {
}
