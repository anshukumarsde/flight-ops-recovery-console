package com.example.flightops.service;

import com.example.flightops.model.Aircraft;
import com.example.flightops.model.AircraftStatus;
import com.example.flightops.model.Flight;
import com.example.flightops.model.RecoveryOption;
import com.example.flightops.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightRecoveryServiceTest {
    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightRecoveryService flightRecoveryService;

    @Test
    void getFlightsDelegatesToRepository() {
        Flight expectedFlight = new Flight(
                "FL-1001",
                "NJ 1001",
                "KTEB",
                "KMIA",
                "Citation Latitude",
                "N700NJ",
                "DISRUPTED",
                "Maintenance hold"
        );

        when(flightRepository.getFlights()).thenReturn(List.of(expectedFlight));

        assertEquals(List.of(expectedFlight), flightRecoveryService.getFlights());
    }

    @Test
    void getRecoveryOptionsPrioritizesAircraftAlreadyAtDepartureAirport() {
        Flight disruptedFlight = new Flight(
                "FL-1001",
                "NJ 1001",
                "KTEB",
                "KMIA",
                "Citation Latitude",
                "N700NJ",
                "DISRUPTED",
                "Maintenance hold"
        );

        when(flightRepository.findFlightById("FL-1001")).thenReturn(disruptedFlight);
        when(flightRepository.getAircraft()).thenReturn(List.of(
                new Aircraft("N701NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KTEB"),
                new Aircraft("N702NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KOPF")
        ));

        List<RecoveryOption> options = flightRecoveryService.getRecoveryOptions("FL-1001");

        assertEquals(2, options.size());
        assertEquals("N701NJ", options.get(0).aircraftId());
        assertFalse(options.get(0).requiresRepositioning());
        assertEquals("N702NJ", options.get(1).aircraftId());
        assertTrue(options.get(1).requiresRepositioning());
    }

    @Test
    void getRecoveryOptionsRejectsNonDisruptedFlights() {
        when(flightRepository.findFlightById("FL-1002")).thenReturn(new Flight(
                "FL-1002",
                "NJ 1002",
                "KJFK",
                "KSEA",
                "Citation Latitude",
                "N700NJ",
                "RECOVERED",
                null
        ));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> flightRecoveryService.getRecoveryOptions("FL-1002"));

        assertEquals("Recovery options are only available for disrupted flights", exception.getReason());
    }

    @Test
    void reassignFlightUpdatesFlightAndAircraftStatus() {
        Flight disruptedFlight = new Flight(
                "FL-1001",
                "NJ 1001",
                "KTEB",
                "KMIA",
                "Citation Latitude",
                "N700NJ",
                "DISRUPTED",
                "Maintenance hold"
        );
        Aircraft availableAircraft = new Aircraft("N701NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KTEB");

        when(flightRepository.findFlightById("FL-1001")).thenReturn(disruptedFlight);
        when(flightRepository.findAircraftById("N701NJ")).thenReturn(availableAircraft);

        Flight updatedFlight = flightRecoveryService.reassignFlight("FL-1001", "N701NJ");

        assertEquals("RECOVERED", updatedFlight.status());
        assertEquals("N701NJ", updatedFlight.assignedAircraftId());
        verify(flightRepository).saveFlight(any(Flight.class));
        verify(flightRepository).updateAircraft(eq("N701NJ"), any(Aircraft.class));
    }

    @Test
    void reassignFlightRejectsUnavailableAircraft() {
        Flight disruptedFlight = new Flight(
                "FL-1001",
                "NJ 1001",
                "KTEB",
                "KMIA",
                "Citation Latitude",
                "N700NJ",
                "DISRUPTED",
                "Maintenance hold"
        );

        when(flightRepository.findFlightById("FL-1001")).thenReturn(disruptedFlight);
        when(flightRepository.findAircraftById("N704NJ")).thenReturn(
                new Aircraft("N704NJ", "Citation Latitude", AircraftStatus.ASSIGNED, "KTEB")
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> flightRecoveryService.reassignFlight("FL-1001", "N704NJ"));

        assertEquals("Aircraft is not available", exception.getReason());
    }

    @Test
    void reassignFlightRejectsIncompatibleAircraftType() {
        Flight disruptedFlight = new Flight(
                "FL-1001",
                "NJ 1001",
                "KTEB",
                "KMIA",
                "Citation Latitude",
                "N700NJ",
                "DISRUPTED",
                "Maintenance hold"
        );

        when(flightRepository.findFlightById("FL-1001")).thenReturn(disruptedFlight);
        when(flightRepository.findAircraftById("N703NJ")).thenReturn(
                new Aircraft("N703NJ", "Citation Longitude", AircraftStatus.AVAILABLE, "KTEB")
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> flightRecoveryService.reassignFlight("FL-1001", "N703NJ"));

        assertEquals("Aircraft type is incompatible", exception.getReason());
    }
}
