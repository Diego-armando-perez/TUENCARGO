package com.tuencargo.domain.model;

import java.math.BigDecimal;

/**
 * Tarifa de bodega vigente: precio por libra, manejo, porcentaje de servicio e impuestos.
 */
public record WarehouseRate(
        Long id,
        String name,
        BigDecimal pricePerLbCop,
        BigDecimal handlingFeeCop,
        BigDecimal serviceFeePercent,
        BigDecimal taxPercent,
        boolean active
) {

    public WarehouseRate withId(Long newId) {
        return new WarehouseRate(newId, name, pricePerLbCop, handlingFeeCop, serviceFeePercent,
                taxPercent, active);
    }

    public WarehouseRate toggled() {
        return new WarehouseRate(id, name, pricePerLbCop, handlingFeeCop, serviceFeePercent,
                taxPercent, !active);
    }
}
