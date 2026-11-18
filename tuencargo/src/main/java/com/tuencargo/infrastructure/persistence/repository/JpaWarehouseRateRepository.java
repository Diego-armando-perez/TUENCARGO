package com.tuencargo.infrastructure.persistence.repository;

import com.tuencargo.infrastructure.persistence.entity.WarehouseRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaWarehouseRateRepository extends JpaRepository<WarehouseRateEntity, Long> {

    List<WarehouseRateEntity> findByActiveTrueOrderByIdDesc();

    List<WarehouseRateEntity> findAllByOrderByIdDesc();
}
