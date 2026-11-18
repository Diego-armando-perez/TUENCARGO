package com.tuencargo.domain.service;

import com.tuencargo.domain.model.MoneyBreakdown;
import com.tuencargo.domain.model.StoreOffer;
import com.tuencargo.domain.model.WarehouseRate;
import com.tuencargo.domain.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regla de negocio pura: convierte una oferta en USD al costo total puesto en Colombia.
 * Sin Spring, sin JPA, sin HTTP.
 */
public final class QuotationCalculator {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private QuotationCalculator() {
    }

    public static MoneyBreakdown calculate(StoreOffer offer, WarehouseRate rate, BigDecimal trm) {
        if (offer == null || rate == null || trm == null || trm.signum() <= 0) {
            throw new DomainException("Datos insuficientes para cotizar el producto");
        }
        BigDecimal productCop = offer.priceUsd().multiply(trm).setScale(0, RoundingMode.HALF_UP);
        BigDecimal freightCop = offer.weightLb().multiply(rate.pricePerLbCop()).setScale(0, RoundingMode.HALF_UP);
        BigDecimal handlingCop = rate.handlingFeeCop().setScale(0, RoundingMode.HALF_UP);
        BigDecimal serviceFeeCop = productCop.multiply(rate.serviceFeePercent())
                .divide(ONE_HUNDRED, 0, RoundingMode.HALF_UP);
        BigDecimal taxableBase = productCop.add(freightCop).add(handlingCop);
        BigDecimal taxCop = taxableBase.multiply(rate.taxPercent())
                .divide(ONE_HUNDRED, 0, RoundingMode.HALF_UP);
        BigDecimal totalCop = productCop.add(freightCop).add(handlingCop).add(serviceFeeCop).add(taxCop);
        return new MoneyBreakdown(productCop, freightCop, handlingCop, serviceFeeCop, taxCop, totalCop);
    }
}
