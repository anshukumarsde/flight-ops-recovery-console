package com.example.flightops.repository;

import com.example.flightops.entity.AircraftEntity;
import com.example.flightops.entity.FlightEntity;
import com.example.flightops.model.Aircraft;
import com.example.flightops.model.AircraftStatus;
import com.example.flightops.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightRepositoryTest {
    @Mock
    private FlightJpaRepository flightJpaRepository;

    @Mock
    private AircraftJpaRepository aircraftJpaRepository;

    private FlightRepository flightRepository;

    @BeforeEach
    void setUp() {
        flightRepository = new FlightRepository(flightJpaRepository, aircraftJpaRepository);
    }

    @Test
    void getFlightsMapsJPAEntitiesToDomainFlights() {
        FlightEntity entity = new FlightEntity(
                "FL-1001",
                "NJ 1001",
                "KTEB",
                "KMIA",
                "Citation Latitude",
                "N700NJ",
                "DISRUPTED",
                "Maintenance hold"
        );
        when(flightJpaRepository.findAll()).thenReturn(List.of(entity));

        List<Flight> flights = flightRepository.getFlights();

        assertEquals(1, flights.size());
        assertEquals("FL-1001", flights.get(0).id());
        assertEquals("NJ 1001", flights.get(0).flightNumber());
    }

    @Test
    void findFlightByIdReturnsDomainObjectWhenPresent() {
        FlightEntity entity = new FlightEntity(
                "FL-1001",
                "NJ 1001",
                "KTEB",
                "KMIA",
                "Citation Latitude",
                "N700NJ",
                "DISRUPTED",
                "Maintenance hold"
        );
        when(flightJpaRepository.findById("FL-1001")).thenReturn(Optional.of(entity));

        Flight flight = flightRepository.findFlightById("FL-1001");

        assertEquals("FL-1001", flight.id());
        assertEquals("NJ 1001", flight.flightNumber());
    }

    @Test
    void findFlightByIdReturnsNullWhenMissing() {
        when(flightJpaRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        assertNull(flightRepository.findFlightById("UNKNOWN"));
    }

    @Test
    void saveFlightPersistsDomainFlightAsEntity() {
        Flight flight = new Flight(
                "FL-1001",
                "NJ 1001",
                "KTEB",
                "KMIA",
                "Citation Latitude",
                "N700NJ",
                "DISRUPTED",
                "Maintenance hold"
        );

        flightRepository.saveFlight(flight);

        ArgumentCaptor<FlightEntity> captor = ArgumentCaptor.forClass(FlightEntity.class);
        verify(flightJpaRepository).save(captor.capture());
        assertEquals("FL-1001", captor.getValue().getId());
        assertEquals("NJ 1001", captor.getValue().getFlightNumber());
    }

    @Test
    void getAircraftMapsJPAEntitiesToDomainAircraft() {
        AircraftEntity entity = new AircraftEntity("N701NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KTEB");
        when(aircraftJpaRepository.findAll()).thenReturn(List.of(entity));

        List<Aircraft> aircraft = flightRepository.getAircraft();

        assertEquals(1, aircraft.size());
        assertEquals("N701NJ", aircraft.get(0).id());
        assertEquals("Citation Latitude", aircraft.get(0).type());
    }

    @Test
    void findAircraftByIdReturnsDomainObjectWhenPresent() {
        AircraftEntity entity = new AircraftEntity("N701NJ", "Citation Latitude", AircraftStatus.AVAILABLE, "KTEB");
        when(aircraftJpaRepository.findById("N701NJ")).thenReturn(Optional.of(entity));

        Aircraft aircraft = flightRepository.findAircraftById("N701NJ");

        assertEquals("N701NJ", aircraft.id());
        assertEquals(AircraftStatus.AVAILABLE, aircraft.status());
    }

    @Test
    void updateAircraftUpdatesMatchingEntity() {
        AircraftEntity entity = new AircraftEntity("N703NJ", "Citation Longitude", AircraftStatus.AVAILABLE, "KTEB");
        when(aircraftJpaRepository.findById("N703NJ")).thenReturn(Optional.of(entity));

        Aircraft updatedAircraft = new Aircraft("N703NJ", "Citation Longitude", AircraftStatus.ASSIGNED, "KJFK");

        flightRepository.updateAircraft("N703NJ", updatedAircraft);

        assertEquals(AircraftStatus.ASSIGNED, entity.getStatus());
        assertEquals("KJFK", entity.getCurrentAirport());
        verify(aircraftJpaRepository).save(entity);
    }
}
