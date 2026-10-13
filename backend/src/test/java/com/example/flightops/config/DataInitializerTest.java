package com.example.flightops.config;

import com.example.flightops.entity.AircraftEntity;
import com.example.flightops.entity.FlightEntity;
import com.example.flightops.model.AircraftStatus;
import com.example.flightops.repository.AircraftJpaRepository;
import com.example.flightops.repository.FlightJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {
    @Mock
    private FlightJpaRepository flightJpaRepository;

    @Mock
    private AircraftJpaRepository aircraftJpaRepository;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer(flightJpaRepository, aircraftJpaRepository);
    }

    @Test
    void runSeedsDemoDataWhenDatabaseIsEmpty() {
        when(flightJpaRepository.count()).thenReturn(0L);
        when(aircraftJpaRepository.count()).thenReturn(0L);
        when(flightJpaRepository.existsByStatus("DISRUPTED")).thenReturn(true);

        dataInitializer.run();

        verify(flightJpaRepository).save(any(FlightEntity.class));
        verify(aircraftJpaRepository).saveAll(anyList());
    }

    @Test
    void runDoesNotSeedWhenDatabaseAlreadyContainsData() {
        when(flightJpaRepository.count()).thenReturn(1L);
        when(aircraftJpaRepository.count()).thenReturn(5L);
        when(flightJpaRepository.existsByStatus("DISRUPTED")).thenReturn(true);

        dataInitializer.run();

        verify(flightJpaRepository, never()).save(any(FlightEntity.class));
        verify(aircraftJpaRepository, never()).saveAll(anyList());
    }

    @Test
    void runCreatesAnActiveIncidentAndReplacementAircraftWhenAllFlightsAreRecovered() {
        when(flightJpaRepository.count()).thenReturn(1L);
        when(aircraftJpaRepository.count()).thenReturn(5L);
        when(flightJpaRepository.existsByStatus("DISRUPTED")).thenReturn(false);
        when(flightJpaRepository.existsById("FL-1001")).thenReturn(true);
        when(flightJpaRepository.existsById("FL-1002")).thenReturn(false);
        when(aircraftJpaRepository.existsById("N700NJ")).thenReturn(true);
        when(aircraftJpaRepository.existsById("N701NJ")).thenReturn(true);
        when(aircraftJpaRepository.existsById("N702NJ")).thenReturn(true);
        when(aircraftJpaRepository.existsById("N703NJ")).thenReturn(true);
        when(aircraftJpaRepository.existsById("N704NJ")).thenReturn(true);
        when(aircraftJpaRepository.existsById("N705NJ")).thenReturn(false);
        when(aircraftJpaRepository.existsById("N706NJ")).thenReturn(false);
        when(aircraftJpaRepository.findAll()).thenReturn(List.of(
                new AircraftEntity("N700NJ", "Citation Latitude", AircraftStatus.MAINTENANCE, "KTEB"),
                new AircraftEntity("N701NJ", "Citation Latitude", AircraftStatus.ASSIGNED, "KTEB"),
                new AircraftEntity("N702NJ", "Citation Latitude", AircraftStatus.ASSIGNED, "KOPF"),
                new AircraftEntity("N703NJ", "Citation Longitude", AircraftStatus.AVAILABLE, "KTEB"),
                new AircraftEntity("N704NJ", "Citation Latitude", AircraftStatus.ASSIGNED, "KTEB")
        ));

        dataInitializer.run();

        verify(flightJpaRepository).save(org.mockito.ArgumentMatchers.argThat(flight ->
                flight.getId().equals("FL-1002")
                        && flight.getStatus().equals("DISRUPTED")
                        && flight.getAssignedAircraftId().equals("N705NJ")
        ));
        verify(aircraftJpaRepository).saveAll(org.mockito.ArgumentMatchers.argThat(aircraft -> {
            List<AircraftEntity> seededAircraft = new ArrayList<>();
            aircraft.forEach(seededAircraft::add);
            return seededAircraft.size() == 2
                    && seededAircraft.get(0).getStatus() == AircraftStatus.MAINTENANCE
                    && seededAircraft.get(1).getStatus() == AircraftStatus.AVAILABLE
                    && !seededAircraft.get(0).getId().equals(seededAircraft.get(1).getId());
        }));
    }
}
