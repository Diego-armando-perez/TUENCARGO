package com.tuencargo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "warehouse_rates")
public class WarehouseRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 80)
    public String name;

    @Column(nullable = false, precision = 16, scale = 2)
    public BigDecimal pricePerLbCop;

    @Column(nullable = false, precision = 16, scale = 2)
    public BigDecimal handlingFeeCop;

    @Column(nullable = false, precision = 6, scale = 2)
    public BigDecimal serviceFeePercent;

    @Column(nullable = false, precision = 6, scale = 2)
    public BigDecimal taxPercent;

    @Column(nullable = false)
    public boolean active;
}
