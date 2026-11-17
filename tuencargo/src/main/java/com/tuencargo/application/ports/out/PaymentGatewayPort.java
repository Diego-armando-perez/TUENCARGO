package com.tuencargo.application.ports.out;

import com.tuencargo.domain.model.PaymentMethod;

import java.math.BigDecimal;

/**
 * Puerto de salida hacia la pasarela de pagos local (PSE, tarjeta, transferencia).
 * En esta entrega lo implementa un adaptador simulado.
 */
public interface PaymentGatewayPort {

    PaymentResult charge(PaymentRequest request);

    record PaymentRequest(
            String orderReference,
            BigDecimal amountCop,
            PaymentMethod method,
            String payerDocument
    ) {
    }

    record PaymentResult(boolean approved, String reference, String message) {
    }
}
