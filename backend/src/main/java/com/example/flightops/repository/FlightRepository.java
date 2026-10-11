package com.example.flightops.repository;

import com.example.flightops.model.Aircraft;
import com.example.flightops.model.AircraftStatus;
import com.example.flightops.model.Flight;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FlightRepository {
    private Flight flight = new Flight(
            "FL-1001", "NJ 1001", "KTEB", "KMIA",
            "Citation Latitude", "N700NJ", "DISRUPTED", "Maintenance hold"
    );

    private final List<Aircraft> aircraft = new ArrayList<>(List.of(
            new Aircraft("N700NJ", "Citation Latitude", AircraftStatus.MAINTENANCE, "KTEB"),
            new Aircraft("N701NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KTEB"),
            new Aircraft("N702NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KOPF"),
            new Aircraft("N703NJ", "Citation Longitude", AircraftStatus.AVAILABLE, "KTEB"),
            new Aircraft("N704NJ", "Citation Latitude", AircraftStatus.ASSIGNED, "KTEB")
    ));

    public List<Flight> getFlights() {
        return List.of(flight);
    }

    public Flight findFlightById(String flightId) {
        if (flight.id().equals(flightId)) {
            return flight;
        }
        return null;
    }

    public void saveFlight(Flight updatedFlight) {
        this.flight = updatedFlight;
    }

    public List<Aircraft> getAircraft() {
        return aircraft;
    }

    public Aircraft findAircraftById(String aircraftId) {
        for (Aircraft candidate : aircraft) {
            if (candidate.id().equals(aircraftId)) {
                return candidate;
            }
        }
        return null;
    }

    public void updateAircraft(String aircraftId, Aircraft updatedAircraft) {
        for (int index = 0; index < aircraft.size(); index++) {
            if (aircraft.get(index).id().equals(aircraftId)) {
                aircraft.set(index, updatedAircraft);
                return;
            }
        }
    }
}
