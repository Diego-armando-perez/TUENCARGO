package com.tuencargo.application.services;

import com.tuencargo.application.ports.in.WarehouseRateUseCase;
import com.tuencargo.application.ports.out.WarehouseRateRepositoryPort;
import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.exception.NotFoundException;
import com.tuencargo.domain.model.WarehouseRate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de aplicación: tarifas de bodega. Solo una tarifa puede estar activa,
 * porque es la que alimenta todas las cotizaciones nuevas.
 */
@Service
public class WarehouseRateService implements WarehouseRateUseCase {

    private final WarehouseRateRepositoryPort rates;

    public WarehouseRateService(WarehouseRateRepositoryPort rates) {
        this.rates = rates;
    }

    @Override
    public List<WarehouseRate> listAll() {
        return rates.findAll();
    }

    @Override
    public WarehouseRate create(String name, BigDecimal pricePerLbCop, BigDecimal handlingFeeCop,
                                 BigDecimal serviceFeePercent, BigDecimal taxPercent) {
        if (name == null || name.isBlank()) {
            throw new DomainException("El nombre de la tarifa es obligatorio");
        }
        requirePositive(pricePerLbCop, "El precio por libra debe ser mayor que cero");
        requirePositive(handlingFeeCop, "La tarifa de manejo debe ser mayor que cero");
        requirePercent(serviceFeePercent, "El porcentaje de servicio debe estar entre 0 y 100");
        requirePercent(taxPercent, "El porcentaje de impuestos debe estar entre 0 y 100");

        rates.findActive().forEach(active -> rates.save(active.toggled()));
        WarehouseRate created = new WarehouseRate(null, name.trim(), pricePerLbCop, handlingFeeCop,
                serviceFeePercent, taxPercent, true);
        return rates.save(created);
    }

    @Override
    public WarehouseRate toggle(Long rateId) {
        WarehouseRate rate = rates.findById(rateId)
                .orElseThrow(() -> new NotFoundException("La tarifa no existe"));
        if (!rate.active() && rates.findActive().isEmpty()) {
            return rates.save(rate.toggled());
        }
        if (rate.active()) {
            throw new DomainException("No puedes desactivar la unica tarifa activa. Crea o activa otra primero");
        }
        rates.findActive().forEach(active -> rates.save(active.toggled()));
        return rates.save(rate.toggled());
    }

    @Override
    public void delete(Long rateId) {
        WarehouseRate rate = rates.findById(rateId)
                .orElseThrow(() -> new NotFoundException("La tarifa no existe"));
        if (rate.active()) {
            throw new DomainException("No puedes eliminar la tarifa activa");
        }
        rates.deleteById(rateId);
    }

    @Override
    public WarehouseRate activeRate() {
        return rates.findActive().stream().findFirst()
                .orElseThrow(() -> new DomainException("No hay una tarifa de bodega activa"));
    }

    private void requirePositive(BigDecimal value, String message) {
        if (value == null || value.signum() <= 0) {
            throw new DomainException(message);
        }
    }

    private void requirePercent(BigDecimal value, String message) {
        if (value == null || value.signum() < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new DomainException(message);
        }
    }
}
