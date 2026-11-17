package com.tuencargo.infrastructure.persistence.repository;

import com.tuencargo.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {

    @EntityGraph(attributePaths = "events")
    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findWithEventsById(@Param("id") Long id);

    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<OrderEntity> findAllByOrderByCreatedAtDesc();
}
