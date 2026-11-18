package com.tuencargo.domain.model;

import java.math.BigDecimal;

/**
 * Oferta de una tienda internacional para un producto: es lo que devuelve el puerto
 * de catálogo (adaptador simulado en esta entrega).
 */
public record StoreOffer(
        String store,
        String productName,
        String productUrl,
        String imageUrl,
        BigDecimal priceUsd,
        BigDecimal weightLb
) {
}
