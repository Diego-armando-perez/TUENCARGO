package com.tuencargo.application.ports.out;

import com.tuencargo.domain.model.User;

import java.util.List;
import java.util.Optional;

/** Puerto de salida: persistencia de usuarios. */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();
}
