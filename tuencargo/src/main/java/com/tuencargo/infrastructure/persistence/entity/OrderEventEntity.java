package com.tuencargo.infrastructure.persistence.entity;

import com.tuencargo.domain.model.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "order_events")
public class OrderEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    public OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public OrderStatus status;

    @Column(nullable = false)
    public Instant occurredAt;

    @Column(length = 300)
    public String note;
}
