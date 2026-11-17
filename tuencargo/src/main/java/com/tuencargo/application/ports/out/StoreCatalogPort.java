package com.tuencargo.application.ports.out;

import com.tuencargo.domain.model.StoreOffer;

import java.util.List;

/**
 * Puerto de salida hacia las tiendas internacionales.
 * En esta entrega lo implementa un adaptador simulado (sin llamadas de red reales).
 */
public interface StoreCatalogPort {

    List<StoreOffer> findOffers(String productUrl);
}
