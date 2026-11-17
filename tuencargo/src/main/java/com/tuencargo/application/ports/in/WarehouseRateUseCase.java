package com.tuencargo.application.ports.in;

import com.tuencargo.domain.model.WarehouseRate;

import java.math.BigDecimal;
import java.util.List;

/** Caso de uso: administración de tarifas de bodega (rol ADMIN). */
public interface WarehouseRateUseCase {

    List<WarehouseRate> listAll();

    WarehouseRate create(String name, BigDecimal pricePerLbCop, BigDecimal handlingFeeCop,
                          BigDecimal serviceFeePercent, BigDecimal taxPercent);

    WarehouseRate toggle(Long rateId);

    void delete(Long rateId);

    WarehouseRate activeRate();
}
