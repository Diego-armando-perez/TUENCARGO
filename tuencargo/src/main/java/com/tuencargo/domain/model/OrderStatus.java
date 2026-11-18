package com.tuencargo.domain.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Máquina de estados del pedido. Las transiciones permitidas viven en el dominio.
 */
public enum OrderStatus {
    PENDIENTE_PAGO,
    COMPRADO,
    EN_BODEGA,
    EN_TRANSITO,
    ENTREGADO,
    CANCELADO;

    public Set<OrderStatus> allowedTransitions() {
        return switch (this) {
            case PENDIENTE_PAGO -> EnumSet.of(COMPRADO, CANCELADO);
            case COMPRADO -> EnumSet.of(EN_BODEGA, CANCELADO);
            case EN_BODEGA -> EnumSet.of(EN_TRANSITO);
            case EN_TRANSITO -> EnumSet.of(ENTREGADO);
            case ENTREGADO, CANCELADO -> EnumSet.noneOf(OrderStatus.class);
        };
    }

    public boolean canTransitionTo(OrderStatus target) {
        return allowedTransitions().contains(target);
    }

    public OrderStatus nextLogisticStatus() {
        return switch (this) {
            case COMPRADO -> EN_BODEGA;
            case EN_BODEGA -> EN_TRANSITO;
            case EN_TRANSITO -> ENTREGADO;
            default -> null;
        };
    }

    public String label() {
        return switch (this) {
            case PENDIENTE_PAGO -> "Pendiente de pago";
            case COMPRADO -> "Comprado";
            case EN_BODEGA -> "Recibido en bodega";
            case EN_TRANSITO -> "En tránsito";
            case ENTREGADO -> "Entregado";
            case CANCELADO -> "Cancelado";
        };
    }
}
