package com.example.flightops.repository;

import com.example.flightops.entity.RecoveryHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecoveryHistoryJpaRepository extends JpaRepository<RecoveryHistoryEntity, Long> {
    List<RecoveryHistoryEntity> findAllByOrderByRecoveredAtDesc();
}
