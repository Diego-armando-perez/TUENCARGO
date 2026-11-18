package com.tuencargo.application.ports.out;

/** Puerto de salida para el hashing de contraseñas (BCrypt en infraestructura). */
public interface PasswordHasherPort {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
