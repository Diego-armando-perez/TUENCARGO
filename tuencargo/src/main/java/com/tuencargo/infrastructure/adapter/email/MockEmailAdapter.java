package com.tuencargo.infrastructure.adapter.email;

import com.tuencargo.application.ports.out.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador simulado del proveedor de correo: escribe el correo en el log.
 * La notificación también queda guardada en la base de datos (buzón del usuario).
 */
@Component
public class MockEmailAdapter implements EmailPort {

    private static final Logger log = LoggerFactory.getLogger(MockEmailAdapter.class);

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("[CORREO SIMULADO] para={} asunto={} cuerpo={}", recipient, subject, body);
    }
}
