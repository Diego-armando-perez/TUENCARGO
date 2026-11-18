package com.tuencargo.infrastructure.security;

import com.tuencargo.application.ports.out.UserRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Adaptador de Spring Security hacia el puerto de usuarios: autenticación con BCrypt.
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort users;

    public JpaUserDetailsService(UserRepositoryPort users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByEmail(username)
                .map(AuthenticatedUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales invalidas"));
    }
}
