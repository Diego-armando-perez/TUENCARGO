package com.tuencargo.domain.service;

import java.math.BigDecimal;

public class QuotationCalculator {
    
    // Lógica base para el cálculo del costo total puesto en Colombia
    public BigDecimal calculateTotalCost(BigDecimal productPriceUsa, BigDecimal estimatedWeightKg, BigDecimal shippingRatePerKg) {
        if (productPriceUsa == null || estimatedWeightKg == null || shippingRatePerKg == null) {
            throw new IllegalArgumentException("Los valores para el cálculo no pueden ser nulos");
        }
        BigDecimal freightCost = estimatedWeightKg.multiply(shippingRatePerKg);
        return productPriceUsa.add(freightCost);
    }
}