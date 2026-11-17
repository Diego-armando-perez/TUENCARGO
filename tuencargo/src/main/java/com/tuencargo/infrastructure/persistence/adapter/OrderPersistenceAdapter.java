package com.tuencargo.infrastructure.persistence.adapter;

import com.tuencargo.application.ports.out.OrderRepositoryPort;
import com.tuencargo.domain.exception.NotFoundException;
import com.tuencargo.domain.model.Order;
import com.tuencargo.domain.model.OrderEvent;
import com.tuencargo.infrastructure.persistence.entity.OrderEntity;
import com.tuencargo.infrastructure.persistence.entity.OrderEventEntity;
import com.tuencargo.infrastructure.persistence.repository.JpaOrderRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Transactional
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final JpaOrderRepository repository;

    public OrderPersistenceAdapter(JpaOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity;
        if (order.id() == null) {
            entity = new OrderEntity();
        } else {
            entity = repository.findWithEventsById(order.id())
                    .orElseThrow(() -> new NotFoundException("El pedido no existe"));
        }
        entity.orderNumber = order.orderNumber();
        entity.userId = order.userId();
        entity.customerEmail = order.customerEmail();
        entity.quotationId = order.quotationId();
        entity.productName = order.productName();
        entity.store = order.store();
        entity.totalCop = order.totalCop();
        entity.status = order.status();
        entity.createdAt = order.createdAt();
        entity.updatedAt = order.updatedAt();
        appendNewEvents(order, entity);
        OrderEntity saved = repository.saveAndFlush(entity);
        return repository.findWithEventsById(saved.id)
                .map(OrderPersistenceAdapter::toDomain)
                .orElseThrow(() -> new NotFoundException("El pedido no existe"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return repository.findWithEventsById(id).map(OrderPersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderPersistenceAdapter::toDomain)
                .toList();
    }

    private void appendNewEvents(Order order, OrderEntity entity) {
        Set<String> existing = entity.events.stream()
                .map(event -> event.status + "|" + event.occurredAt)
                .collect(Collectors.toSet());
        for (OrderEvent event : order.events()) {
            String key = event.status() + "|" + event.occurredAt();
            if (existing.add(key)) {
                OrderEventEntity child = new OrderEventEntity();
                child.order = entity;
                child.status = event.status();
                child.occurredAt = event.occurredAt();
                child.note = event.note();
                entity.events.add(child);
            }
        }
    }

    private static Order toDomain(OrderEntity entity) {
        List<OrderEvent> events = entity.events.stream()
                .map(event -> new OrderEvent(event.status, event.occurredAt, event.note))
                .toList();
        return new Order(entity.id, entity.orderNumber, entity.userId, entity.customerEmail,
                entity.quotationId, entity.productName, entity.store, entity.totalCop, entity.status,
                entity.createdAt, entity.updatedAt, events);
    }
}
