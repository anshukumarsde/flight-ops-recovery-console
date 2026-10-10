package com.example.flightops.service;

import com.example.flightops.model.Aircraft;
import com.example.flightops.model.AircraftStatus;
import com.example.flightops.model.Flight;
import com.example.flightops.model.RecoveryOption;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightRecoveryService {
    private final List<Flight> flights = new ArrayList<>(List.of(
            new Flight(
                    "FL-1001",
                    "NJ 1001",
                    "KTEB",
                    "KMIA",
                    "Citation Latitude",
                    "N700NJ",
                    "DISRUPTED",
                    "Maintenance hold"
            )
    ));

    private final List<Aircraft> aircraft = new ArrayList<>(List.of(
            new Aircraft("N700NJ", "Citation Latitude", AircraftStatus.MAINTENANCE, "KTEB"),
            new Aircraft("N701NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KTEB"),
            new Aircraft("N702NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KOPF"),
            new Aircraft("N703NJ", "Citation Longitude", AircraftStatus.AVAILABLE, "KTEB"),
            new Aircraft("N704NJ", "Citation Latitude", AircraftStatus.ASSIGNED, "KTEB")
    ));

    public List<Flight> getFlights() {
        return flights;
    }

    public List<RecoveryOption> getRecoveryOptions(String flightId) {
        Flight flight = findFlight(flightId);
        if (!flight.status().equals("DISRUPTED")) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Recovery options are only available for disrupted flights"
            );
        }

        List<Aircraft> rankedAircraft = new ArrayList<>();
        addMatchingAircraft(rankedAircraft, flight, true);
        addMatchingAircraft(rankedAircraft, flight, false);

        List<RecoveryOption> options = new ArrayList<>();
        for (int index = 0; index < rankedAircraft.size(); index++) {
            Aircraft candidate = rankedAircraft.get(index);
            boolean requiresRepositioning = !candidate.currentAirport().equals(flight.departureAirport());

            options.add(new RecoveryOption(candidate.id(), candidate.type(), candidate.currentAirport(),
                    requiresRepositioning,
                    index + 1
            ));
        }

        return options;
    }

    public Flight reassignFlight(String flightId, String aircraftId) {
        Flight flight = findFlight(flightId);
        if (!flight.status().equals("DISRUPTED")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Flight is not disrupted");
        }
        if (aircraftId == null || aircraftId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aircraft ID is required");
        }

        for (int index = 0; index < aircraft.size(); index++) {
            Aircraft candidate = aircraft.get(index);
            if (candidate.id().equals(aircraftId)) {
                if (candidate.status() != AircraftStatus.AVAILABLE) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Aircraft is not available");
                }
                if (!candidate.type().equals(flight.aircraftType())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aircraft type is incompatible");
                }

                Flight recoveredFlight = new Flight(
                        flight.id(),
                        flight.flightNumber(),
                        flight.departureAirport(),
                        flight.arrivalAirport(),
                        flight.aircraftType(),
                        candidate.id(),
                        "RECOVERED",
                        null
                );
                flights.set(flights.indexOf(flight), recoveredFlight);
                aircraft.set(index, new Aircraft(
                        candidate.id(),
                        candidate.type(),
                        AircraftStatus.ASSIGNED,
                        candidate.currentAirport()
                ));
                return recoveredFlight;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aircraft not found: " + aircraftId);
    }

    private void addMatchingAircraft(List<Aircraft> rankedAircraft, Flight flight, boolean atDeparture) {
        for (Aircraft candidate : aircraft) {
            boolean isAtDeparture =
                    candidate.currentAirport().equals(flight.departureAirport());

            if (isAvailableReplacementFor(candidate, flight) && isAtDeparture == atDeparture) {
                rankedAircraft.add(candidate);
            }
        }
    }

    private boolean isAvailableReplacementFor(Aircraft candidate, Flight flight) {
        return candidate.status() == AircraftStatus.AVAILABLE && candidate.type().equals(flight.aircraftType());
    }

    private Flight findFlight(String flightId) {
        for (Flight flight : flights) {
            if (flight.id().equals(flightId)) {
                return flight;
            }
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Flight not found: " + flightId
        );
    }
}
