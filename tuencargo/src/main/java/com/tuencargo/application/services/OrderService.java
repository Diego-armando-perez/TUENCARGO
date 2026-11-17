package com.tuencargo.application.services;

import com.tuencargo.application.ports.in.NotificationUseCase;
import com.tuencargo.application.ports.in.OrderUseCase;
import com.tuencargo.application.ports.out.OrderRepositoryPort;
import com.tuencargo.application.ports.out.QuotationRepositoryPort;
import com.tuencargo.application.ports.out.UserRepositoryPort;
import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.exception.NotFoundException;
import com.tuencargo.domain.model.Order;
import com.tuencargo.domain.model.OrderStatus;
import com.tuencargo.domain.model.Quotation;
import com.tuencargo.domain.model.QuotationStatus;
import com.tuencargo.domain.model.User;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Servicio de aplicación: crea pedidos desde cotizaciones vigentes y gestiona
 * el avance logístico aplicando la máquina de estados del dominio.
 */
@Service
public class OrderService implements OrderUseCase {

    private static final DateTimeFormatter ORDER_DATE =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private final OrderRepositoryPort orders;
    private final QuotationRepositoryPort quotations;
    private final UserRepositoryPort users;
    private final NotificationUseCase notifications;
    private final Clock clock;

    public OrderService(OrderRepositoryPort orders, QuotationRepositoryPort quotations,
                        UserRepositoryPort users, NotificationUseCase notifications, Clock clock) {
        this.orders = orders;
        this.quotations = quotations;
        this.users = users;
        this.notifications = notifications;
        this.clock = clock;
    }

    @Override
    public Order createFromQuotation(Long quotationId, Long userId) {
        Quotation quotation = quotations.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("La cotización no existe"));
        if (!quotation.userId().equals(userId)) {
            throw new NotFoundException("La cotización no existe para este usuario");
        }
        Instant now = clock.instant();
        if (quotation.isExpired(now) || quotation.status() != QuotationStatus.VIGENTE) {
            quotations.save(quotation.withStatus(QuotationStatus.VENCIDA));
            throw new DomainException("La cotización venció o ya fue usada. Genera una nueva.");
        }
        User user = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        String orderNumber = "TC-" + ORDER_DATE.format(now) + "-"
                + String.format(Locale.ROOT, "%04d", ThreadLocalRandom.current().nextInt(10_000));
        Order order = Order.fromQuotation(quotation, userId, user.email(), orderNumber, now);
        Order saved = orders.save(order);
        quotations.save(quotation.withStatus(QuotationStatus.CONSUMIDA));
        notifications.notifyUser(userId, user.email(), "Pedido " + orderNumber + " creado",
                "Creamos tu pedido para '" + quotation.productName() + "' por un total de $"
                        + quotation.breakdown().totalCop() + " COP. Completa el pago para continuar.",
                "PEDIDO");
        return saved;
    }

    @Override
    public Order get(Long orderId) {
        return orders.findById(orderId)
                .orElseThrow(() -> new NotFoundException("El pedido no existe"));
    }

    @Override
    public List<Order> listByUser(Long userId) {
        return orders.findByUserId(userId);
    }

    @Override
    public List<Order> listAll() {
        return orders.findAll();
    }

    @Override
    public Order advanceLogisticStatus(Long orderId, String note) {
        Order order = get(orderId);
        OrderStatus next = order.status().nextLogisticStatus();
        if (next == null) {
            throw new DomainException("El pedido en estado '" + order.status().label()
                    + "' no puede avanzar de etapa");
        }
        String detail = note == null || note.isBlank()
                ? "Actualizado por el administrador de bodega" : note.trim();
        Order updated = orders.save(order.changeStatus(next, detail, clock.instant()));
        notifyCustomer(updated, "Actualización de tu pedido " + updated.orderNumber(),
                "Tu pedido paso a: " + next.label() + ". " + detail);
        return updated;
    }

    @Override
    public Order cancel(Long orderId, Long userId) {
        Order order = get(orderId);
        if (!order.userId().equals(userId)) {
            throw new NotFoundException("El pedido no existe");
        }
        Order updated = orders.save(order.changeStatus(OrderStatus.CANCELADO,
                "Cancelado por el cliente", clock.instant()));
        notifyCustomer(updated, "Pedido " + updated.orderNumber() + " cancelado",
                "Cancelamos tu pedido. Si fue un error, puedes generar una nueva cotización.");
        return updated;
    }

    private void notifyCustomer(Order order, String subject, String body) {
        notifications.notifyUser(order.userId(), order.customerEmail(), subject, body, "PEDIDO");
    }
}
