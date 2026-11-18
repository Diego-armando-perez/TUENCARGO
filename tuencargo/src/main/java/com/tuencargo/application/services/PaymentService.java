package com.tuencargo.application.services;

import com.tuencargo.application.ports.in.NotificationUseCase;
import com.tuencargo.application.ports.in.PaymentUseCase;
import com.tuencargo.application.ports.out.OrderRepositoryPort;
import com.tuencargo.application.ports.out.PaymentGatewayPort;
import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.exception.NotFoundException;
import com.tuencargo.domain.model.Order;
import com.tuencargo.domain.model.OrderStatus;
import com.tuencargo.domain.model.PaymentMethod;
import org.springframework.stereotype.Service;

import java.time.Clock;

/**
 * Servicio de aplicación: cobra con la pasarela local y aplica el resultado al pedido.
 */
@Service
public class PaymentService implements PaymentUseCase {

    private final OrderRepositoryPort orders;
    private final PaymentGatewayPort gateway;
    private final NotificationUseCase notifications;
    private final Clock clock;

    public PaymentService(OrderRepositoryPort orders, PaymentGatewayPort gateway,
                          NotificationUseCase notifications, Clock clock) {
        this.orders = orders;
        this.gateway = gateway;
        this.notifications = notifications;
        this.clock = clock;
    }

    @Override
    public PaymentOutcome pay(Long orderId, Long userId, PaymentMethod method, String payerDocument) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new NotFoundException("El pedido no existe"));
        if (!order.userId().equals(userId)) {
            throw new NotFoundException("El pedido no existe");
        }
        if (!order.isPayable()) {
            throw new DomainException("El pedido esta en estado '" + order.status().label()
                    + "' y no admite un nuevo pago");
        }
        if (payerDocument == null || payerDocument.trim().length() < 6) {
            throw new DomainException("Ingresa un documento o referencia de pago valido");
        }
        PaymentMethod safeMethod = method == null ? PaymentMethod.PSE : method;
        PaymentGatewayPort.PaymentResult result = gateway.charge(
                new PaymentGatewayPort.PaymentRequest(order.orderNumber(), order.totalCop(),
                        safeMethod, payerDocument.trim()));

        if (result.approved()) {
            Order paid = orders.save(order.changeStatus(OrderStatus.COMPRADO,
                    "Pago aprobado con " + safeMethod.label() + " (ref. " + result.reference() + ")",
                    clock.instant()));
            notifications.notifyUser(paid.userId(), paid.customerEmail(),
                    "Pago aprobado - Pedido " + paid.orderNumber(),
                    "Recibimos tu pago por $" + paid.totalCop() + " COP. Referencia: "
                            + result.reference() + ". Tu pedido quedo en estado Comprado.",
                    "PAGO");
            return new PaymentOutcome(true, result.message(), result.reference());
        }

        notifications.notifyUser(order.userId(), order.customerEmail(),
                "Pago rechazado - Pedido " + order.orderNumber(),
                "La pasarela rechazo el pago: " + result.message()
                        + ". Puedes intentar con otro medio de pago.", "PAGO");
        return new PaymentOutcome(false, result.message(), result.reference());
    }
}
