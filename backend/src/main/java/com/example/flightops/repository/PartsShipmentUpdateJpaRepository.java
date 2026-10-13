package com.example.flightops.repository;

import com.example.flightops.entity.PartsShipmentUpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartsShipmentUpdateJpaRepository extends JpaRepository<PartsShipmentUpdateEntity, Long> {
    List<PartsShipmentUpdateEntity> findAllByShipmentIdOrderByOccurredAtDesc(String shipmentId);
}
