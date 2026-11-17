package com.tuencargo.application.ports.out;

/** Puerto de salida hacia el proveedor de correo electrónico. */
public interface EmailPort {

    void send(String recipient, String subject, String body);
}
