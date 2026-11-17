package com.tuencargo.application.ports.in;

import com.tuencargo.domain.model.User;

/** Caso de uso: registro e identificación de usuarios. */
public interface AuthUseCase {

    User register(String fullName, String email, String rawPassword);

    User getByEmail(String email);
}
