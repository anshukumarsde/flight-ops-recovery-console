package com.example.flightops.config;

import com.example.flightops.entity.PartsShipmentEntity;
import com.example.flightops.entity.PartsShipmentUpdateEntity;
import com.example.flightops.repository.PartsShipmentJpaRepository;
import com.example.flightops.repository.PartsShipmentUpdateJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
public class PartsShipmentDataInitializer implements CommandLineRunner {
    private final PartsShipmentJpaRepository shipmentRepository;
    private final PartsShipmentUpdateJpaRepository updateRepository;

    public PartsShipmentDataInitializer(
            PartsShipmentJpaRepository shipmentRepository,
            PartsShipmentUpdateJpaRepository updateRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.updateRepository = updateRepository;
    }

    @Override
    public void run(String... args) {
        if (shipmentRepository.count() != 0) {
            return;
        }

        Instant now = Instant.now();
        PartsShipmentEntity pump = new PartsShipmentEntity(
                "PS-1001", "DHL-74291058", "PMP-2048", "Engine oil pump", 1,
                "Lufthansa Technik", "Hamburg, Germany", "Miami, FL", "IN_TRANSIT",
                "Frankfurt, Germany", LocalDate.now().plusDays(3), now.minusSeconds(3600)
        );
        PartsShipmentEntity brake = new PartsShipmentEntity(
                "PS-1002", "FDX-55108326", "BRK-7712", "Carbon brake assembly", 2,
                "AeroParts Dallas", "Dallas, TX", "Teterboro, NJ", "DELAYED",
                "Memphis, TN", LocalDate.now().plusDays(1), now
        );
        shipmentRepository.saveAll(List.of(pump, brake));
        updateRepository.saveAll(List.of(
                new PartsShipmentUpdateEntity(pump.getId(), "IN_TRANSIT", "Frankfurt, Germany", "Departed origin facility; customs cleared", now.minusSeconds(3600)),
                new PartsShipmentUpdateEntity(pump.getId(), "ORDERED", "Hamburg, Germany", "Shipment collected from supplier", now.minusSeconds(7200)),
                new PartsShipmentUpdateEntity(brake.getId(), "DELAYED", "Memphis, TN", "Weather delay reported by carrier", now),
                new PartsShipmentUpdateEntity(brake.getId(), "IN_TRANSIT", "Dallas, TX", "Picked up by carrier", now.minusSeconds(10800))
        ));
    }
}
