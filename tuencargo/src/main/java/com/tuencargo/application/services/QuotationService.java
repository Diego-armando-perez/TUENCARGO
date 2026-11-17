package com.tuencargo.application.services;

import com.tuencargo.application.ports.in.QuotationUseCase;
import com.tuencargo.application.ports.out.ExchangeRatePort;
import com.tuencargo.application.ports.out.QuotationRepositoryPort;
import com.tuencargo.application.ports.out.StoreCatalogPort;
import com.tuencargo.application.ports.out.WarehouseRateRepositoryPort;
import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.exception.NotFoundException;
import com.tuencargo.domain.model.MoneyBreakdown;
import com.tuencargo.domain.model.PriceOption;
import com.tuencargo.domain.model.Quotation;
import com.tuencargo.domain.model.StoreOffer;
import com.tuencargo.domain.model.WarehouseRate;
import com.tuencargo.domain.service.QuotationCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Servicio de aplicación: orquesta catálogo de tiendas, TRM y tarifas de bodega
 * para producir la cotización y la comparación de precios.
 */
@Service
public class QuotationService implements QuotationUseCase {

    private static final Duration VALIDITY = Duration.ofHours(24);

    private final StoreCatalogPort stores;
    private final ExchangeRatePort exchangeRate;
    private final QuotationRepositoryPort quotations;
    private final WarehouseRateRepositoryPort rates;
    private final Clock clock;

    public QuotationService(StoreCatalogPort stores, ExchangeRatePort exchangeRate,
                            QuotationRepositoryPort quotations, WarehouseRateRepositoryPort rates,
                            Clock clock) {
        this.stores = stores;
        this.exchangeRate = exchangeRate;
        this.quotations = quotations;
        this.rates = rates;
        this.clock = clock;
    }

    @Override
    public Quotation create(Long userId, String productUrl) {
        String url = productUrl == null ? "" : productUrl.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new DomainException("El link debe iniciar con http:// o https://");
        }
        List<StoreOffer> offers = stores.findOffers(url);
        if (offers.isEmpty()) {
            throw new DomainException("No encontramos el producto en las tiendas soportadas");
        }
        StoreOffer baseOffer = offers.get(0);
        WarehouseRate rate = rates.findActive().stream().findFirst()
                .orElseThrow(() -> new DomainException("No hay una tarifa de bodega activa"));
        BigDecimal trm = exchangeRate.usdToCop();
        MoneyBreakdown breakdown = QuotationCalculator.calculate(baseOffer, rate, trm);
        Instant now = clock.instant();
        Quotation quotation = Quotation.create(userId, baseOffer, trm, rate.id(), breakdown, now,
                now.plus(VALIDITY));
        return quotations.save(quotation);
    }

    @Override
    public Quotation get(Long quotationId) {
        return quotations.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("La cotización no existe"));
    }

    @Override
    public List<Quotation> listByUser(Long userId) {
        return quotations.findByUserId(userId);
    }

    @Override
    public List<PriceOption> compare(Long quotationId) {
        Quotation quotation = get(quotationId);
        WarehouseRate rate = rates.findById(quotation.rateId())
                .orElseGet(() -> rates.findActive().stream().findFirst()
                        .orElseThrow(() -> new DomainException("No hay una tarifa de bodega activa")));
        return stores.findOffers(quotation.productUrl()).stream()
                .map(offer -> new PriceOption(offer,
                        QuotationCalculator.calculate(offer, rate, quotation.trm())))
                .sorted(Comparator.comparing(option -> option.breakdown().totalCop()))
                .toList();
    }
}
