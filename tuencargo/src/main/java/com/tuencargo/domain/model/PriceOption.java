package com.tuencargo.domain.model;

/**
 * Opción de compra calculada para la comparación de precios entre tiendas.
 */
public record PriceOption(StoreOffer offer, MoneyBreakdown breakdown) {
}
