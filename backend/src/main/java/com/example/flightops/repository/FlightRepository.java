package com.example.flightops.repository;

import com.example.flightops.entity.AircraftEntity;
import com.example.flightops.entity.FlightEntity;
import com.example.flightops.model.Aircraft;
import com.example.flightops.model.Flight;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FlightRepository {
    private final FlightJpaRepository flightJpaRepository;
    private final AircraftJpaRepository aircraftJpaRepository;

    public FlightRepository(FlightJpaRepository flightJpaRepository, AircraftJpaRepository aircraftJpaRepository) {
        this.flightJpaRepository = flightJpaRepository;
        this.aircraftJpaRepository = aircraftJpaRepository;
    }

    public List<Flight> getFlights() {
        return flightJpaRepository.findAll().stream()
                .map(this::toDomainFlight)
                .toList();
    }

    public Flight findFlightById(String flightId) {
        return flightJpaRepository.findById(flightId)
                .map(this::toDomainFlight)
                .orElse(null);
    }

    public void saveFlight(Flight updatedFlight) {
        flightJpaRepository.save(toEntity(updatedFlight));
    }

    // Repository layer translates persistence entities to domain objects without changing business rules.
    public List<Aircraft> getAircraft() {
        return aircraftJpaRepository.findAll().stream()
                .map(this::toDomainAircraft)
                .toList();
    }

    public Aircraft findAircraftById(String aircraftId) {
        return aircraftJpaRepository.findById(aircraftId)
                .map(this::toDomainAircraft)
                .orElse(null);
    }

    public void updateAircraft(String aircraftId, Aircraft updatedAircraft) {
        aircraftJpaRepository.findById(aircraftId).ifPresent(entity -> {
            entity.setType(updatedAircraft.type());
            entity.setStatus(updatedAircraft.status());
            entity.setCurrentAirport(updatedAircraft.currentAirport());
            aircraftJpaRepository.save(entity);
        });
    }

    private Flight toDomainFlight(FlightEntity entity) {
        return new Flight(
                entity.getId(),
                entity.getFlightNumber(),
                entity.getDepartureAirport(),
                entity.getArrivalAirport(),
                entity.getAircraftType(),
                entity.getAssignedAircraftId(),
                entity.getStatus(),
                entity.getDisruptionReason()
        );
    }

    private FlightEntity toEntity(Flight flight) {
        return new FlightEntity(
                flight.id(),
                flight.flightNumber(),
                flight.departureAirport(),
                flight.arrivalAirport(),
                flight.aircraftType(),
                flight.assignedAircraftId(),
                flight.status(),
                flight.disruptionReason()
        );
    }

    private Aircraft toDomainAircraft(AircraftEntity entity) {
        return new Aircraft(
                entity.getId(),
                entity.getType(),
                entity.getStatus(),
                entity.getCurrentAirport()
        );
    }
}
