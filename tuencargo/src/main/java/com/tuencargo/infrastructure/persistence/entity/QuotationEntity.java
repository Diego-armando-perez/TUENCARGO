package com.tuencargo.infrastructure.persistence.entity;

import com.tuencargo.domain.model.QuotationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "quotations")
public class QuotationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Long userId;

    @Column(nullable = false, length = 1000)
    public String productUrl;

    @Column(nullable = false, length = 80)
    public String store;

    @Column(nullable = false, length = 300)
    public String productName;

    @Column(length = 1000)
    public String imageUrl;

    @Column(nullable = false, precision = 14, scale = 2)
    public BigDecimal priceUsd;

    @Column(nullable = false, precision = 10, scale = 3)
    public BigDecimal weightLb;

    @Column(nullable = false, precision = 14, scale = 2)
    public BigDecimal trm;

    @Column(nullable = false)
    public Long rateId;

    @Column(nullable = false, precision = 16, scale = 2)
    public BigDecimal productCop;

    @Column(nullable = false, precision = 16, scale = 2)
    public BigDecimal freightCop;

    @Column(nullable = false, precision = 16, scale = 2)
    public BigDecimal handlingCop;

    @Column(nullable = false, precision = 16, scale = 2)
    public BigDecimal serviceFeeCop;

    @Column(nullable = false, precision = 16, scale = 2)
    public BigDecimal taxCop;

    @Column(nullable = false, precision = 16, scale = 2)
    public BigDecimal totalCop;

    @Column(nullable = false)
    public Instant createdAt;

    @Column(nullable = false)
    public Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public QuotationStatus status;
}
