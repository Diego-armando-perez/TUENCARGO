package com.tuencargo.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad de dominio: usuario de la plataforma (cliente o administrador).
 * No depende de JPA ni de Spring: la persistencia la adapta la infraestructura.
 */
public final class User {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final String lockerAddress;
    private final Instant createdAt;

    public User(Long id, String fullName, String email, String passwordHash, Role role,
                String lockerAddress, Instant createdAt) {
        this.id = id;
        this.fullName = Objects.requireNonNull(fullName, "fullName");
        this.email = Objects.requireNonNull(email, "email");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.role = Objects.requireNonNull(role, "role");
        this.lockerAddress = lockerAddress;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
    }

    public static User registered(String fullName, String email, String passwordHash, Role role,
                                  String lockerAddress, Instant createdAt) {
        return new User(null, fullName, email, passwordHash, role, lockerAddress, createdAt);
    }

    public User withId(Long newId) {
        return new User(newId, fullName, email, passwordHash, role, lockerAddress, createdAt);
    }

    public Long id() {
        return id;
    }

    public String fullName() {
        return fullName;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public Role role() {
        return role;
    }

    public String lockerAddress() {
        return lockerAddress;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
