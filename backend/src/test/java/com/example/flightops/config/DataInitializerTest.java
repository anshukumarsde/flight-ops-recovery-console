package com.example.flightops.config;

import com.example.flightops.entity.FlightEntity;
import com.example.flightops.repository.AircraftJpaRepository;
import com.example.flightops.repository.FlightJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        dataInitializer.run();

        verify(flightJpaRepository).save(any(FlightEntity.class));
        verify(aircraftJpaRepository).saveAll(anyList());
    }

    @Test
    void runDoesNotSeedWhenDatabaseAlreadyContainsData() {
        when(flightJpaRepository.count()).thenReturn(1L);
        when(aircraftJpaRepository.count()).thenReturn(5L);

        dataInitializer.run();

        verify(flightJpaRepository, never()).save(any(FlightEntity.class));
        verify(aircraftJpaRepository, never()).saveAll(anyList());
    }
}
