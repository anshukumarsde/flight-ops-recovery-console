package com.example.flightops.service;

import com.example.flightops.api.PartsShipmentUpdateRequest;
import com.example.flightops.entity.PartsShipmentEntity;
import com.example.flightops.entity.PartsShipmentUpdateEntity;
import com.example.flightops.model.PartsShipment;
import com.example.flightops.model.PartsShipmentUpdate;
import com.example.flightops.repository.PartsShipmentJpaRepository;
import com.example.flightops.repository.PartsShipmentUpdateJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@Service
public class PartsShipmentService {
    private static final Set<String> VALID_STATUSES = Set.of(
            "ORDERED", "IN_TRANSIT", "DELAYED", "OUT_FOR_DELIVERY", "DELIVERED"
    );

    private final PartsShipmentJpaRepository shipmentRepository;
    private final PartsShipmentUpdateJpaRepository updateRepository;

    public PartsShipmentService(
            PartsShipmentJpaRepository shipmentRepository,
            PartsShipmentUpdateJpaRepository updateRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.updateRepository = updateRepository;
    }

    @Transactional(readOnly = true)
    public List<PartsShipment> getShipments() {
        return shipmentRepository.findAllByOrderByUpdatedAtDesc().stream()
                .map(this::toModel)
                .toList();
    }

    @Transactional
    public PartsShipment addUpdate(String shipmentId, PartsShipmentUpdateRequest request) {
        PartsShipmentEntity shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found: " + shipmentId));
        String status = required(request.status(), "Status");
        if (!VALID_STATUSES.contains(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status must be one of: " + String.join(", ", VALID_STATUSES)
            );
        }
        String location = required(request.location(), "Location");
        String note = required(request.note(), "Update note");
        LocalDate estimatedDelivery = shipment.getEstimatedDelivery();
        if (request.estimatedDelivery() != null && !request.estimatedDelivery().isBlank()) {
            try {
                estimatedDelivery = LocalDate.parse(request.estimatedDelivery());
            } catch (DateTimeParseException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estimated delivery must be a valid date (YYYY-MM-DD)");
            }
        }

        Instant updatedAt = Instant.now();
        shipment.updateTracking(status, location, estimatedDelivery, updatedAt);
        shipmentRepository.save(shipment);
        updateRepository.save(new PartsShipmentUpdateEntity(shipmentId, status, location, note, updatedAt));
        return toModel(shipment);
    }

    private PartsShipment toModel(PartsShipmentEntity shipment) {
        List<PartsShipmentUpdate> updates = updateRepository
                .findAllByShipmentIdOrderByOccurredAtDesc(shipment.getId()).stream()
                .map(update -> new PartsShipmentUpdate(
                        update.getStatus(),
                        update.getLocation(),
                        update.getNote(),
                        update.getOccurredAt()
                ))
                .toList();

        return new PartsShipment(
                shipment.getId(),
                shipment.getTrackingNumber(),
                shipment.getPartNumber(),
                shipment.getPartName(),
                shipment.getQuantity(),
                shipment.getSupplier(),
                shipment.getOrigin(),
                shipment.getDestination(),
                shipment.getStatus(),
                shipment.getCurrentLocation(),
                shipment.getEstimatedDelivery(),
                shipment.getUpdatedAt(),
                updates
        );
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required");
        }
        return value.trim();
    }
}
