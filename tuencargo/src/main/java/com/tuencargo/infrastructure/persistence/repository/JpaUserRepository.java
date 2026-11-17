package com.tuencargo.infrastructure.persistence.repository;

import com.tuencargo.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    List<UserEntity> findAllByOrderByFullNameAsc();
}
