package com.tuencargo.infrastructure.persistence.entity;

import com.tuencargo.domain.model.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 40)
    public String orderNumber;

    @Column(nullable = false)
    public Long userId;

    @Column(nullable = false, length = 160)
    public String customerEmail;

    @Column(nullable = false)
    public Long quotationId;

    @Column(nullable = false, length = 300)
    public String productName;

    @Column(nullable = false, length = 80)
    public String store;

    @Column(nullable = false, precision = 16, scale = 2)
    public BigDecimal totalCop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public OrderStatus status;

    @Column(nullable = false)
    public Instant createdAt;

    @Column(nullable = false)
    public Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @OrderBy("occurredAt ASC")
    public List<OrderEventEntity> events = new ArrayList<>();
}
