package com.example.flightops.service;

import com.example.flightops.api.CreatePartsOrderRequest;
import com.example.flightops.api.PartsShipmentUpdateRequest;
import com.example.flightops.entity.PartsShipmentEntity;
import com.example.flightops.model.PartsShipment;
import com.example.flightops.repository.PartsShipmentJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PartsShipmentService {
    private static final Set<String> VALID_STATUSES = Set.of(
            "ORDERED", "IN_TRANSIT", "DELAYED", "DELIVERED"
    );

    private final PartsShipmentJpaRepository shipmentRepository;

    public PartsShipmentService(PartsShipmentJpaRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional(readOnly = true)
    public List<PartsShipment> getShipments() {
        return shipmentRepository.findAllByOrderByUpdatedAtDesc().stream()
                .map(this::toModel)
                .toList();
    }

    @Transactional
    public PartsShipment createOrder(CreatePartsOrderRequest request) {
        String partNumber = required(request.partNumber(), "Part number");
        String partName = required(request.partName(), "Part name");
        String supplier = required(request.supplier(), "Supplier");
        String origin = required(request.origin(), "Origin");
        String destination = required(request.destination(), "Destination");
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        }

        LocalDate estimatedDelivery;
        try {
            estimatedDelivery = LocalDate.parse(required(request.estimatedDelivery(), "Estimated delivery"));
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estimated delivery must be a valid date (YYYY-MM-DD)");
        }
        if (estimatedDelivery.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estimated delivery cannot be in the past");
        }

        String orderReference = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String shipmentId = "PS-" + orderReference;
        String location = "Awaiting supplier dispatch";
        Instant orderedAt = Instant.now();
        PartsShipmentEntity shipment = shipmentRepository.save(new PartsShipmentEntity(
                shipmentId,
                "PO-" + orderReference,
                partNumber,
                partName,
                request.quantity(),
                supplier,
                origin,
                destination,
                "ORDERED",
                location,
                estimatedDelivery,
                orderedAt
        ));
        return toModel(shipment);
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
        LocalDate estimatedDelivery = shipment.getEstimatedDelivery();
        try {
            estimatedDelivery = LocalDate.parse(required(request.estimatedDelivery(), "Estimated delivery"));
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estimated delivery must be a valid date (YYYY-MM-DD)");
        }
        if (estimatedDelivery.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estimated delivery cannot be in the past");
        }

        Instant updatedAt = Instant.now();
        shipment.updateTracking(status, location, estimatedDelivery, updatedAt);
        return toModel(shipmentRepository.save(shipment));
    }

    private PartsShipment toModel(PartsShipmentEntity shipment) {
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
                shipment.getUpdatedAt()
        );
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required");
        }
        return value.trim();
    }
}
