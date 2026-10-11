package com.example.flightops.repository;

import com.example.flightops.entity.AircraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftJpaRepository extends JpaRepository<AircraftEntity, String> {
}
