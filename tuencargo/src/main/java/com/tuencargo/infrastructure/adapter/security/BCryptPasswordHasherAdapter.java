package com.tuencargo.infrastructure.adapter.security;

import com.tuencargo.application.ports.out.PasswordHasherPort;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Adaptador del puerto de hashing usando BCrypt (Spring Security Crypto).
 * Se registra como bean en AppConfig para evitar doble definición.
 */
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {

    private final PasswordEncoder encoder;

    public BCryptPasswordHasherAdapter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return encoder.matches(rawPassword, passwordHash);
    }
}
