package com.tuencargo.domain.model;

import java.time.Instant;

/**
 * Evento del historial de estados de un pedido (trazabilidad logística).
 */
public record OrderEvent(OrderStatus status, Instant occurredAt, String note) {
}
