package com.tuencargo.infrastructure.persistence.adapter;

import com.tuencargo.application.ports.out.UserRepositoryPort;
import com.tuencargo.domain.model.User;
import com.tuencargo.infrastructure.persistence.entity.UserEntity;
import com.tuencargo.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final JpaUserRepository repository;

    public UserPersistenceAdapter(JpaUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = user.id() == null ? new UserEntity()
                : repository.findById(user.id()).orElseGet(UserEntity::new);
        entity.id = user.id();
        entity.fullName = user.fullName();
        entity.email = user.email();
        entity.passwordHash = user.passwordHash();
        entity.role = user.role();
        entity.lockerAddress = user.lockerAddress();
        entity.createdAt = user.createdAt();
        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(UserPersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(UserPersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAllByOrderByFullNameAsc().stream()
                .map(UserPersistenceAdapter::toDomain)
                .toList();
    }

    private static User toDomain(UserEntity entity) {
        return new User(entity.id, entity.fullName, entity.email, entity.passwordHash, entity.role,
                entity.lockerAddress, entity.createdAt);
    }
}
