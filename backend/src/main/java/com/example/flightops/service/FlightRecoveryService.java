package com.example.flightops.service;

import com.example.flightops.model.Aircraft;
import com.example.flightops.model.AircraftStatus;
import com.example.flightops.model.Flight;
import com.example.flightops.model.RecoveryOption;
import com.example.flightops.repository.FlightRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightRecoveryService {
    private final FlightRepository flightRepository;

    public FlightRecoveryService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> getFlights() {
        return flightRepository.getFlights();
    }

    public List<RecoveryOption> getRecoveryOptions(String flightId) {
        Flight flight = findFlight(flightId);
        if (!flight.status().equals("DISRUPTED")) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Recovery options are only available for disrupted flights"
            );
        }

        List<RecoveryOption> options = new ArrayList<>();
        addMatchingAircraft(flight, options, true);
        addMatchingAircraft(flight, options, false);
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

        Aircraft candidate = flightRepository.findAircraftById(aircraftId);
        if (candidate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aircraft not found: " + aircraftId);
        }

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

        flightRepository.saveFlight(recoveredFlight);
        flightRepository.updateAircraft(candidate.id(), new Aircraft(
                candidate.id(),
                candidate.type(),
                AircraftStatus.ASSIGNED,
                candidate.currentAirport()
        ));
        return recoveredFlight;
    }

    private void addMatchingAircraft(Flight flight, List<RecoveryOption> options, boolean atDeparture) {
        for (Aircraft candidate : flightRepository.getAircraft()) {
            boolean aircraftAtDeparture =
                    candidate.currentAirport().equals(flight.departureAirport());
            boolean compatibleAndAvailable =
                    candidate.status() == AircraftStatus.AVAILABLE
                            && candidate.type().equals(flight.aircraftType());

            if (compatibleAndAvailable && aircraftAtDeparture == atDeparture) {
                options.add(new RecoveryOption(
                        candidate.id(),
                        candidate.type(),
                        candidate.currentAirport(),
                        !aircraftAtDeparture,
                        options.size() + 1
                ));
            }
        }
    }

    private Flight findFlight(String flightId) {
        Flight flight = flightRepository.findFlightById(flightId);
        if (flight != null) {
            return flight;
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Flight not found: " + flightId
        );
    }
}
