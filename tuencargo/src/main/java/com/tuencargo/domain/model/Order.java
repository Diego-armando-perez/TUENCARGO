package com.tuencargo.domain.model;

import com.tuencargo.domain.exception.DomainException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido del cliente. Aplica la máquina de estados del dominio y registra su historial.
 */
public final class Order {

    private final Long id;
    private final String orderNumber;
    private final Long userId;
    private final String customerEmail;
    private final Long quotationId;
    private final String productName;
    private final String store;
    private final BigDecimal totalCop;
    private final OrderStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final List<OrderEvent> events;

    public Order(Long id, String orderNumber, Long userId, String customerEmail, Long quotationId,
                 String productName, String store, BigDecimal totalCop, OrderStatus status,
                 Instant createdAt, Instant updatedAt, List<OrderEvent> events) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.customerEmail = customerEmail;
        this.quotationId = quotationId;
        this.productName = productName;
        this.store = store;
        this.totalCop = totalCop;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.events = List.copyOf(events);
    }

    public static Order fromQuotation(Quotation quotation, Long userId, String customerEmail,
                                      String orderNumber, Instant now) {
        List<OrderEvent> history = new ArrayList<>();
        history.add(new OrderEvent(OrderStatus.PENDIENTE_PAGO, now, "Pedido creado desde la cotización"));
        return new Order(null, orderNumber, userId, customerEmail, quotation.id(),
                quotation.productName(), quotation.store(), quotation.breakdown().totalCop(),
                OrderStatus.PENDIENTE_PAGO, now, now, history);
    }

    public Order changeStatus(OrderStatus target, String note, Instant now) {
        if (!status.canTransitionTo(target)) {
            throw new DomainException("Transición no permitida: " + status.label() + " -> " + target.label());
        }
        List<OrderEvent> history = new ArrayList<>(events);
        history.add(new OrderEvent(target, now, note));
        return new Order(id, orderNumber, userId, customerEmail, quotationId, productName, store,
                totalCop, target, createdAt, now, history);
    }

    public Order withId(Long newId) {
        return new Order(newId, orderNumber, userId, customerEmail, quotationId, productName, store,
                totalCop, status, createdAt, updatedAt, events);
    }

    public boolean isPayable() {
        return status == OrderStatus.PENDIENTE_PAGO;
    }

    public Long id() {
        return id;
    }

    public String orderNumber() {
        return orderNumber;
    }

    public Long userId() {
        return userId;
    }

    public String customerEmail() {
        return customerEmail;
    }

    public Long quotationId() {
        return quotationId;
    }

    public String productName() {
        return productName;
    }

    public String store() {
        return store;
    }

    public BigDecimal totalCop() {
        return totalCop;
    }

    public OrderStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public List<OrderEvent> events() {
        return events;
    }
}
