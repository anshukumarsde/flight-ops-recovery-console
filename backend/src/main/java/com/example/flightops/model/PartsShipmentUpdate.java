package com.example.flightops.model;

import java.time.Instant;

public record PartsShipmentUpdate(String status, String location, String note, Instant occurredAt) {
}
