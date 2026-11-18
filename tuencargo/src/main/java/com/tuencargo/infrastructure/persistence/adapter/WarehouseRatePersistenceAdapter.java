package com.tuencargo.infrastructure.persistence.adapter;

import com.tuencargo.application.ports.out.WarehouseRateRepositoryPort;
import com.tuencargo.domain.model.WarehouseRate;
import com.tuencargo.infrastructure.persistence.entity.WarehouseRateEntity;
import com.tuencargo.infrastructure.persistence.repository.JpaWarehouseRateRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class WarehouseRatePersistenceAdapter implements WarehouseRateRepositoryPort {

    private final JpaWarehouseRateRepository repository;

    public WarehouseRatePersistenceAdapter(JpaWarehouseRateRepository repository) {
        this.repository = repository;
    }

    @Override
    public WarehouseRate save(WarehouseRate rate) {
        WarehouseRateEntity entity = rate.id() == null ? new WarehouseRateEntity()
                : repository.findById(rate.id()).orElseGet(WarehouseRateEntity::new);
        entity.id = rate.id();
        entity.name = rate.name();
        entity.pricePerLbCop = rate.pricePerLbCop();
        entity.handlingFeeCop = rate.handlingFeeCop();
        entity.serviceFeePercent = rate.serviceFeePercent();
        entity.taxPercent = rate.taxPercent();
        entity.active = rate.active();
        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseRate> findById(Long id) {
        return repository.findById(id).map(WarehouseRatePersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseRate> findAll() {
        return repository.findAllByOrderByIdDesc().stream()
                .map(WarehouseRatePersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseRate> findActive() {
        return repository.findByActiveTrueOrderByIdDesc().stream()
                .map(WarehouseRatePersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private static WarehouseRate toDomain(WarehouseRateEntity entity) {
        return new WarehouseRate(entity.id, entity.name, entity.pricePerLbCop,
                entity.handlingFeeCop, entity.serviceFeePercent, entity.taxPercent, entity.active);
    }
}
