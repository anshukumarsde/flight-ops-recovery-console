package com.example.flightops.config;

import com.example.flightops.entity.AircraftEntity;
import com.example.flightops.entity.FlightEntity;
import com.example.flightops.model.AircraftStatus;
import com.example.flightops.repository.AircraftJpaRepository;
import com.example.flightops.repository.FlightJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final FlightJpaRepository flightJpaRepository;
    private final AircraftJpaRepository aircraftJpaRepository;

    public DataInitializer(FlightJpaRepository flightJpaRepository, AircraftJpaRepository aircraftJpaRepository) {
        this.flightJpaRepository = flightJpaRepository;
        this.aircraftJpaRepository = aircraftJpaRepository;
    }

    @Override
    public void run(String... args) {
        // Seed the initial demo records only when their tables are empty.
        if (flightJpaRepository.count() == 0) {
            flightJpaRepository.save(new FlightEntity(
                    "FL-1001",
                    "NJ 1001",
                    "KTEB",
                    "KMIA",
                    "Citation Latitude",
                    "N700NJ",
                    "DISRUPTED",
                    "Maintenance hold"
            ));
        }

        if (aircraftJpaRepository.count() == 0) {
            aircraftJpaRepository.saveAll(List.of(
                    new AircraftEntity("N700NJ", "Citation Latitude", AircraftStatus.MAINTENANCE, "KTEB"),
                    new AircraftEntity("N701NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KTEB"),
                    new AircraftEntity("N702NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KOPF"),
                    new AircraftEntity("N703NJ", "Citation Longitude", AircraftStatus.AVAILABLE, "KTEB"),
                    new AircraftEntity("N704NJ", "Citation Latitude", AircraftStatus.ASSIGNED, "KTEB")
            ));
        }

        seedActiveIncidentWhenNoneExist();
    }

    private void seedActiveIncidentWhenNoneExist() {
        if (flightJpaRepository.existsByStatus("DISRUPTED")) {
            return;
        }

        // Keep an incident available for the next demo run without overwriting completed flights or history.
        int flightNumber = 1001;
        String flightId = flightId(flightNumber);
        while (flightJpaRepository.existsById(flightId)) {
            flightId = flightId(++flightNumber);
        }

        List<AircraftEntity> newAircraft = new ArrayList<>();
        String assignedAircraftId = nextAircraftId(700, newAircraft);
        newAircraft.add(new AircraftEntity(
                assignedAircraftId,
                "Citation Latitude",
                AircraftStatus.MAINTENANCE,
                "KTEB"
        ));

        boolean hasAvailableReplacement = aircraftJpaRepository.findAll().stream()
                .anyMatch(aircraft -> aircraft.getType().equals("Citation Latitude")
                        && aircraft.getStatus() == AircraftStatus.AVAILABLE);
        if (!hasAvailableReplacement) {
            String aircraftId = nextAircraftId(700, newAircraft);
            newAircraft.add(new AircraftEntity(
                    aircraftId,
                    "Citation Latitude",
                    AircraftStatus.AVAILABLE,
                    "KTEB"
            ));
        }

        flightJpaRepository.save(new FlightEntity(
                flightId,
                "NJ " + flightNumber,
                "KTEB",
                "KMIA",
                "Citation Latitude",
                assignedAircraftId,
                "DISRUPTED",
                "Maintenance hold"
        ));
        aircraftJpaRepository.saveAll(newAircraft);
    }

    private String flightId(int flightNumber) {
        return "FL-" + flightNumber;
    }

    private String nextAircraftId(int startNumber, List<AircraftEntity> pendingAircraft) {
        int aircraftNumber = startNumber;
        String aircraftId = "N" + aircraftNumber + "NJ";
        while (aircraftJpaRepository.existsById(aircraftId) || isPendingAircraft(aircraftId, pendingAircraft)) {
            aircraftId = "N" + ++aircraftNumber + "NJ";
        }
        return aircraftId;
    }

    private boolean isPendingAircraft(String aircraftId, List<AircraftEntity> pendingAircraft) {
        return pendingAircraft.stream().anyMatch(aircraft -> aircraft.getId().equals(aircraftId));
    }
}
