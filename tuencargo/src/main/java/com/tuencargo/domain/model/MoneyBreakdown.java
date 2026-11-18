package com.tuencargo.domain.model;

import java.math.BigDecimal;

/**
 * Value object con el desglose de una cotización en pesos colombianos.
 */
public record MoneyBreakdown(
        BigDecimal productCop,
        BigDecimal freightCop,
        BigDecimal handlingCop,
        BigDecimal serviceFeeCop,
        BigDecimal taxCop,
        BigDecimal totalCop
) {
}
