package com.tuencargo.application.ports.out;

import com.tuencargo.domain.model.WarehouseRate;

import java.util.List;
import java.util.Optional;

/** Puerto de salida: tarifas de bodega administradas por el rol ADMIN. */
public interface WarehouseRateRepositoryPort {

    WarehouseRate save(WarehouseRate rate);

    Optional<WarehouseRate> findById(Long id);

    List<WarehouseRate> findAll();

    List<WarehouseRate> findActive();

    void deleteById(Long id);
}
