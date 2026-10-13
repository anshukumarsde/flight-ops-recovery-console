package com.example.flightops.service;

import com.example.flightops.api.CreatePartsOrderRequest;
import com.example.flightops.api.PartsShipmentUpdateRequest;
import com.example.flightops.entity.PartsShipmentEntity;
import com.example.flightops.repository.PartsShipmentJpaRepository;
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

    @InjectMocks
    private PartsShipmentService service;

    @Test
    void listsShipmentsWithCurrentLocation() {
        PartsShipmentEntity shipment = shipment();
        when(shipmentRepository.findAllByOrderByUpdatedAtDesc()).thenReturn(List.of(shipment));

        var result = service.getShipments();

        assertEquals("Frankfurt, Germany", result.get(0).currentLocation());
    }

    @Test
    void recordsShipmentProgressAndPreservesEtaWhenOmitted() {
        PartsShipmentEntity shipment = shipment();
        when(shipmentRepository.findById(shipment.getId())).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(PartsShipmentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var result = service.addUpdate(shipment.getId(), new PartsShipmentUpdateRequest(
                "IN_TRANSIT", "Miami, FL", LocalDate.now().plusDays(1).toString()
        ));

        assertEquals("IN_TRANSIT", result.status());
        assertEquals("Miami, FL", result.currentLocation());
        assertEquals(LocalDate.now().plusDays(1), result.estimatedDelivery());
        verify(shipmentRepository).save(shipment);
    }

    @Test
    void rejectsUnknownShipmentAndInvalidStatus() {
        when(shipmentRepository.findById("missing")).thenReturn(Optional.empty());
        assertEquals(404, assertThrows(ResponseStatusException.class,
                () -> service.addUpdate("missing", new PartsShipmentUpdateRequest(
                        "IN_TRANSIT", "Chicago, IL", LocalDate.now().plusDays(1).toString()
                ))).getStatusCode().value());

        when(shipmentRepository.findById("PS-1001")).thenReturn(Optional.of(shipment()));
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.addUpdate("PS-1001", new PartsShipmentUpdateRequest(
                        "UNKNOWN", "Chicago, IL", LocalDate.now().plusDays(1).toString()
                ))).getStatusCode().value());
    }

    @Test
    void createsAnOrderReadyForTracking() {
        when(shipmentRepository.save(any(PartsShipmentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var order = service.createOrder(new CreatePartsOrderRequest(
                "PMP-2048",
                "Engine oil pump",
                2,
                "Example Aviation Parts",
                "Dallas, TX",
                "Miami, FL",
                LocalDate.now().plusDays(4).toString()
        ));

        assertEquals("ORDERED", order.status());
        assertEquals(2, order.quantity());
        assertEquals("Awaiting supplier dispatch", order.currentLocation());
        assertEquals("PO-" + order.id().substring(3), order.trackingNumber());
        verify(shipmentRepository).save(any(PartsShipmentEntity.class));
    }

    @Test
    void rejectsOrdersWithInvalidQuantitiesOrPastDeliveryDates() {
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.createOrder(new CreatePartsOrderRequest(
                        "PMP-2048", "Engine oil pump", 0, "Supplier", "Dallas", "Miami",
                        LocalDate.now().plusDays(1).toString()
                ))).getStatusCode().value());

        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.createOrder(new CreatePartsOrderRequest(
                        "PMP-2048", "Engine oil pump", 1, "Supplier", "Dallas", "Miami",
                        LocalDate.now().minusDays(1).toString()
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
