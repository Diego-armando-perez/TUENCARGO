package com.tuencargo.application.ports.out;

import com.tuencargo.domain.model.Order;

import java.util.List;
import java.util.Optional;

/** Puerto de salida: persistencia de pedidos con su historial de estados. */
public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findByUserId(Long userId);

    List<Order> findAll();
}
