package com.example.flightops.repository;

import com.example.flightops.entity.PartsShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartsShipmentJpaRepository extends JpaRepository<PartsShipmentEntity, String> {
    List<PartsShipmentEntity> findAllByOrderByUpdatedAtDesc();
}
