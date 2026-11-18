package com.tuencargo.infrastructure.adapter.store;

import com.tuencargo.application.ports.out.StoreCatalogPort;
import com.tuencargo.domain.model.StoreOffer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador simulado del puerto de catálogo de tiendas.
 * Genera ofertas deterministas a partir del link para poder demostrar el flujo
 * cotización -> comparación sin depender de scraping real ni de red.
 */
@Component
public class MockStoreCatalogAdapter implements StoreCatalogPort {

    private static final List<StoreProfile> STORES = List.of(
            new StoreProfile("Amazon US", new BigDecimal("1.00"), new BigDecimal("1.00")),
            new StoreProfile("Walmart US", new BigDecimal("0.88"), new BigDecimal("1.10")),
            new StoreProfile("eBay US", new BigDecimal("1.12"), new BigDecimal("0.90"))
    );

    @Override
    public List<StoreOffer> findOffers(String productUrl) {
        if (productUrl == null || productUrl.isBlank()) {
            return List.of();
        }
        long seed = Math.abs((long) productUrl.trim().toLowerCase(Locale.ROOT).hashCode());
        BigDecimal basePrice = BigDecimal.valueOf(1999 + (seed % 18_000), 2);
        BigDecimal baseWeight = BigDecimal.valueOf(5 + (seed % 45), 1);
        String title = titleFrom(productUrl);
        return STORES.stream()
                .map(profile -> new StoreOffer(
                        profile.name(),
                        title,
                        productUrl.trim(),
                        null,
                        basePrice.multiply(profile.priceFactor()).setScale(2, RoundingMode.HALF_UP),
                        baseWeight.multiply(profile.weightFactor()).setScale(1, RoundingMode.HALF_UP)))
                .toList();
    }

    private String titleFrom(String productUrl) {
        String clean = productUrl.trim().replaceAll("[?#].*$", "");
        String slug = clean.substring(clean.lastIndexOf('/') + 1);
        slug = slug.replaceAll("\\.[a-zA-Z]{2,5}$", "").replace('-', ' ').replace('_', ' ');
        if (slug.isBlank()) {
            return "Producto importado desde link";
        }
        String[] words = slug.split("\\s+");
        StringBuilder title = new StringBuilder();
        for (String word : words) {
            if (title.length() > 0) {
                title.append(' ');
            }
            title.append(word.substring(0, 1).toUpperCase(Locale.ROOT))
                    .append(word.length() > 1 ? word.substring(1).toLowerCase(Locale.ROOT) : "");
        }
        String result = title.toString();
        return result.length() > 120 ? result.substring(0, 120) : result;
    }

    private record StoreProfile(String name, BigDecimal priceFactor, BigDecimal weightFactor) {
    }
}
