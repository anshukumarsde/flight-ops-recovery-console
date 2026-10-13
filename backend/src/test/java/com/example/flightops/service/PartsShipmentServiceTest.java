package com.example.flightops.service;

import com.example.flightops.api.PartsShipmentUpdateRequest;
import com.example.flightops.entity.PartsShipmentEntity;
import com.example.flightops.entity.PartsShipmentUpdateEntity;
import com.example.flightops.repository.PartsShipmentJpaRepository;
import com.example.flightops.repository.PartsShipmentUpdateJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartsShipmentServiceTest {
    @Mock
    private PartsShipmentJpaRepository shipmentRepository;

    @Mock
    private PartsShipmentUpdateJpaRepository updateRepository;

    @InjectMocks
    private PartsShipmentService service;

    @Test
    void listsShipmentsWithLocationAndUpdateHistory() {
        PartsShipmentEntity shipment = shipment();
        PartsShipmentUpdateEntity update = new PartsShipmentUpdateEntity(
                shipment.getId(), "IN_TRANSIT", "Frankfurt, Germany", "Customs cleared", Instant.now()
        );
        when(shipmentRepository.findAllByOrderByUpdatedAtDesc()).thenReturn(List.of(shipment));
        when(updateRepository.findAllByShipmentIdOrderByOccurredAtDesc(shipment.getId())).thenReturn(List.of(update));

        var result = service.getShipments();

        assertEquals("Frankfurt, Germany", result.get(0).currentLocation());
        assertEquals("Customs cleared", result.get(0).updates().get(0).note());
    }

    @Test
    void recordsShipmentProgressAndPreservesEtaWhenOmitted() {
        PartsShipmentEntity shipment = shipment();
        when(shipmentRepository.findById(shipment.getId())).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(PartsShipmentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updateRepository.findAllByShipmentIdOrderByOccurredAtDesc(shipment.getId())).thenReturn(List.of());

        var result = service.addUpdate(shipment.getId(), new PartsShipmentUpdateRequest(
                "OUT_FOR_DELIVERY", "Miami, FL", null, "Arrived at destination facility"
        ));

        assertEquals("OUT_FOR_DELIVERY", result.status());
        assertEquals("Miami, FL", result.currentLocation());
        assertEquals(LocalDate.now().plusDays(2), result.estimatedDelivery());
        verify(updateRepository).save(any(PartsShipmentUpdateEntity.class));
    }

    @Test
    void rejectsUnknownShipmentAndInvalidStatus() {
        when(shipmentRepository.findById("missing")).thenReturn(Optional.empty());
        assertEquals(404, assertThrows(ResponseStatusException.class,
                () -> service.addUpdate("missing", new PartsShipmentUpdateRequest(
                        "IN_TRANSIT", "Chicago, IL", null, "Update"
                ))).getStatusCode().value());

        when(shipmentRepository.findById("PS-1001")).thenReturn(Optional.of(shipment()));
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.addUpdate("PS-1001", new PartsShipmentUpdateRequest(
                        "UNKNOWN", "Chicago, IL", null, "Update"
                ))).getStatusCode().value());
    }

    private PartsShipmentEntity shipment() {
        return new PartsShipmentEntity(
                "PS-1001", "DHL-74291058", "PMP-2048", "Engine oil pump", 1,
                "Lufthansa Technik", "Hamburg, Germany", "Miami, FL", "IN_TRANSIT",
                "Frankfurt, Germany", LocalDate.now().plusDays(2), Instant.now()
        );
    }
}
