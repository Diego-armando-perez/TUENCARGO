package com.tuencargo.application.services;

import com.tuencargo.application.ports.in.AuthUseCase;
import com.tuencargo.application.ports.out.PasswordHasherPort;
import com.tuencargo.application.ports.out.UserRepositoryPort;
import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.exception.NotFoundException;
import com.tuencargo.domain.model.Role;
import com.tuencargo.domain.model.User;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Servicio de aplicación: registro de clientes con contraseña hasheada (BCrypt)
 * y asignación automática de casillero internacional.
 */
@Service
public class AuthService implements AuthUseCase {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserRepositoryPort users;
    private final PasswordHasherPort hasher;
    private final Clock clock;

    public AuthService(UserRepositoryPort users, PasswordHasherPort hasher, Clock clock) {
        this.users = users;
        this.hasher = hasher;
        this.clock = clock;
    }

    @Override
    public User register(String fullName, String email, String rawPassword) {
        String name = safeTrim(fullName);
        String normalizedEmail = safeTrim(email).toLowerCase(Locale.ROOT);
        if (name.length() < 3) {
            throw new DomainException("Escribe tu nombre completo");
        }
        if (!EMAIL.matcher(normalizedEmail).matches()) {
            throw new DomainException("El correo electrónico no es válido");
        }
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new DomainException("La contraseña debe tener al menos 8 caracteres");
        }
        if (users.findByEmail(normalizedEmail).isPresent()) {
            throw new DomainException("Ya existe una cuenta registrada con ese correo");
        }
        String locker = buildLockerAddress(name);
        User user = User.registered(name, normalizedEmail, hasher.hash(rawPassword), Role.USER,
                locker, clock.instant());
        return users.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return users.findByEmail(safeTrim(email).toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    private String buildLockerAddress(String fullName) {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
        return "TU-" + code + ", 8500 NW 36th St Suite " + code + ", Miami, FL 33166, USA"
                + " (a nombre de " + fullName + ")";
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
