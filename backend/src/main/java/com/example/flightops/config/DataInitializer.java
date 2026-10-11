package com.example.flightops.config;

import com.example.flightops.entity.AircraftEntity;
import com.example.flightops.entity.FlightEntity;
import com.example.flightops.model.AircraftStatus;
import com.example.flightops.repository.AircraftJpaRepository;
import com.example.flightops.repository.FlightJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        // Seed the demo flight and aircraft pool only once when the database is empty.
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
    }
}

