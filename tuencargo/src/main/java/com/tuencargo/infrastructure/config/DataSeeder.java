package com.tuencargo.infrastructure.config;

import com.tuencargo.application.ports.out.PasswordHasherPort;
import com.tuencargo.application.ports.out.UserRepositoryPort;
import com.tuencargo.application.ports.out.WarehouseRateRepositoryPort;
import com.tuencargo.domain.model.Role;
import com.tuencargo.domain.model.User;
import com.tuencargo.domain.model.WarehouseRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;

/**
 * Seeder: deja listas las cuentas de prueba (ADMIN y USER) y la tarifa de bodega activa.
 * Es idempotente: si el dato ya existe, no lo duplica.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepositoryPort users;
    private final WarehouseRateRepositoryPort rates;
    private final PasswordHasherPort hasher;
    private final Clock clock;

    public DataSeeder(UserRepositoryPort users, WarehouseRateRepositoryPort rates,
                      PasswordHasherPort hasher, Clock clock) {
        this.users = users;
        this.rates = rates;
        this.hasher = hasher;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedUser("Administrador TuEncargo", "admin@tuencargo.com", "Admin123*", Role.ADMIN, null);
        seedUser("Cliente Demo", "cliente@tuencargo.com", "Cliente123*", Role.USER, locker());
        if (rates.findActive().isEmpty()) {
            rates.save(new WarehouseRate(null, "Tarifa Estandar Miami",
                    new BigDecimal("28000.00"), new BigDecimal("15000.00"),
                    new BigDecimal("10.00"), new BigDecimal("19.00"), true));
            log.info("Seeder: tarifa de bodega estandar creada");
        }
        log.info("Credenciales de prueba -> ADMIN: admin@tuencargo.com / Admin123* | USER: cliente@tuencargo.com / Cliente123*");
    }

    private void seedUser(String fullName, String email, String rawPassword, Role role, String locker) {
        if (users.findByEmail(email).isPresent()) {
            return;
        }
        users.save(User.registered(fullName, email, hasher.hash(rawPassword), role, locker,
                clock.instant()));
        log.info("Seeder: usuario {} creado con rol {}", email, role);
    }

    private String locker() {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "TU-" + code + ", 8500 NW 36th St Suite " + code + ", Miami, FL 33166, USA";
    }
}
