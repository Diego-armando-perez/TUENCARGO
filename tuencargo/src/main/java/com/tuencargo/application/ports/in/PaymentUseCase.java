package com.tuencargo.application.ports.in;

import com.tuencargo.domain.model.PaymentMethod;

/** Caso de uso: pagar un pedido con medios locales a través de la pasarela. */
public interface PaymentUseCase {

    PaymentOutcome pay(Long orderId, Long userId, PaymentMethod method, String payerDocument);

    record PaymentOutcome(boolean approved, String message, String reference) {
    }
}
