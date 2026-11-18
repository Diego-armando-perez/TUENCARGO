package com.tuencargo.infrastructure.persistence.repository;

import com.tuencargo.infrastructure.persistence.entity.QuotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaQuotationRepository extends JpaRepository<QuotationEntity, Long> {

    List<QuotationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
