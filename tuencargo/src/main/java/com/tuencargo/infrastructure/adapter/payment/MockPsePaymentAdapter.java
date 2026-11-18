package com.tuencargo.infrastructure.adapter.payment;

import com.tuencargo.application.ports.out.PaymentGatewayPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

/**
 * Adaptador simulado de la pasarela local (PSE).
 * Regla de demostración: un documento terminado en "9" es rechazado por la pasarela;
 * cualquier otro documento se aprueba. Queda documentado en el README.
 */
@Component
public class MockPsePaymentAdapter implements PaymentGatewayPort {

    private static final Logger log = LoggerFactory.getLogger(MockPsePaymentAdapter.class);

    @Override
    public PaymentResult charge(PaymentRequest request) {
        boolean rejected = request.payerDocument() != null
                && request.payerDocument().trim().endsWith("9");
        String reference = "PSE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        if (rejected) {
            log.info("Pasarela simulada RECHAZA {} por {} COP", request.orderReference(), request.amountCop());
            return new PaymentResult(false, reference,
                    "Fondos insuficientes o cuenta bloqueada (respuesta simulada de PSE)");
        }
        log.info("Pasarela simulada APRUEBA {} por {} COP con {}", request.orderReference(),
                request.amountCop(), reference);
        return new PaymentResult(true, reference, "Pago aprobado por la pasarela PSE simulada");
    }
}
