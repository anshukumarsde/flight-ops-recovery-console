package com.example.flightops.repository;

import com.example.flightops.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightJpaRepository extends JpaRepository<FlightEntity, String> {
}
