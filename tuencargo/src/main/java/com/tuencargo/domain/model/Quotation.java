package com.tuencargo.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Cotización de un producto puesto en Colombia, con vigencia de 24 horas.
 */
public final class Quotation {

    private final Long id;
    private final Long userId;
    private final String productUrl;
    private final String store;
    private final String productName;
    private final String imageUrl;
    private final BigDecimal priceUsd;
    private final BigDecimal weightLb;
    private final BigDecimal trm;
    private final Long rateId;
    private final MoneyBreakdown breakdown;
    private final Instant createdAt;
    private final Instant expiresAt;
    private final QuotationStatus status;

    public Quotation(Long id, Long userId, String productUrl, String store, String productName,
                     String imageUrl, BigDecimal priceUsd, BigDecimal weightLb, BigDecimal trm,
                     Long rateId, MoneyBreakdown breakdown, Instant createdAt, Instant expiresAt,
                     QuotationStatus status) {
        this.id = id;
        this.userId = userId;
        this.productUrl = productUrl;
        this.store = store;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.priceUsd = priceUsd;
        this.weightLb = weightLb;
        this.trm = trm;
        this.rateId = rateId;
        this.breakdown = breakdown;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public static Quotation create(Long userId, StoreOffer offer, BigDecimal trm, Long rateId,
                                   MoneyBreakdown breakdown, Instant createdAt, Instant expiresAt) {
        return new Quotation(null, userId, offer.productUrl(), offer.store(), offer.productName(),
                offer.imageUrl(), offer.priceUsd(), offer.weightLb(), trm, rateId, breakdown,
                createdAt, expiresAt, QuotationStatus.VIGENTE);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public Quotation withId(Long newId) {
        return new Quotation(newId, userId, productUrl, store, productName, imageUrl, priceUsd,
                weightLb, trm, rateId, breakdown, createdAt, expiresAt, status);
    }

    public Quotation withStatus(QuotationStatus newStatus) {
        return new Quotation(id, userId, productUrl, store, productName, imageUrl, priceUsd,
                weightLb, trm, rateId, breakdown, createdAt, expiresAt, newStatus);
    }

    public Long id() {
        return id;
    }

    public Long userId() {
        return userId;
    }

    public String productUrl() {
        return productUrl;
    }

    public String store() {
        return store;
    }

    public String productName() {
        return productName;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public BigDecimal priceUsd() {
        return priceUsd;
    }

    public BigDecimal weightLb() {
        return weightLb;
    }

    public BigDecimal trm() {
        return trm;
    }

    public Long rateId() {
        return rateId;
    }

    public MoneyBreakdown breakdown() {
        return breakdown;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public QuotationStatus status() {
        return status;
    }
}
