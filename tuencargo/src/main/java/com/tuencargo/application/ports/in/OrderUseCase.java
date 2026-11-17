package com.tuencargo.application.ports.in;

import com.tuencargo.domain.model.Order;

import java.util.List;

/** Caso de uso: ciclo de vida del pedido y su máquina de estados. */
public interface OrderUseCase {

    Order createFromQuotation(Long quotationId, Long userId);

    Order get(Long orderId);

    List<Order> listByUser(Long userId);

    List<Order> listAll();

    Order advanceLogisticStatus(Long orderId, String note);

    Order cancel(Long orderId, Long userId);
}
