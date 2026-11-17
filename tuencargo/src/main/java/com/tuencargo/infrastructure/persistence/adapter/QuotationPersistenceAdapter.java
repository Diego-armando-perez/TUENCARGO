package com.tuencargo.infrastructure.persistence.adapter;

import com.tuencargo.application.ports.out.QuotationRepositoryPort;
import com.tuencargo.domain.model.MoneyBreakdown;
import com.tuencargo.domain.model.Quotation;
import com.tuencargo.infrastructure.persistence.entity.QuotationEntity;
import com.tuencargo.infrastructure.persistence.repository.JpaQuotationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class QuotationPersistenceAdapter implements QuotationRepositoryPort {

    private final JpaQuotationRepository repository;

    public QuotationPersistenceAdapter(JpaQuotationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Quotation save(Quotation quotation) {
        QuotationEntity entity = quotation.id() == null ? new QuotationEntity()
                : repository.findById(quotation.id()).orElseGet(QuotationEntity::new);
        entity.id = quotation.id();
        entity.userId = quotation.userId();
        entity.productUrl = quotation.productUrl();
        entity.store = quotation.store();
        entity.productName = quotation.productName();
        entity.imageUrl = quotation.imageUrl();
        entity.priceUsd = quotation.priceUsd();
        entity.weightLb = quotation.weightLb();
        entity.trm = quotation.trm();
        entity.rateId = quotation.rateId();
        entity.productCop = quotation.breakdown().productCop();
        entity.freightCop = quotation.breakdown().freightCop();
        entity.handlingCop = quotation.breakdown().handlingCop();
        entity.serviceFeeCop = quotation.breakdown().serviceFeeCop();
        entity.taxCop = quotation.breakdown().taxCop();
        entity.totalCop = quotation.breakdown().totalCop();
        entity.createdAt = quotation.createdAt();
        entity.expiresAt = quotation.expiresAt();
        entity.status = quotation.status();
        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Quotation> findById(Long id) {
        return repository.findById(id).map(QuotationPersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quotation> findByUserId(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(QuotationPersistenceAdapter::toDomain)
                .toList();
    }

    private static Quotation toDomain(QuotationEntity entity) {
        MoneyBreakdown breakdown = new MoneyBreakdown(entity.productCop, entity.freightCop,
                entity.handlingCop, entity.serviceFeeCop, entity.taxCop, entity.totalCop);
        return new Quotation(entity.id, entity.userId, entity.productUrl, entity.store,
                entity.productName, entity.imageUrl, entity.priceUsd, entity.weightLb, entity.trm,
                entity.rateId, breakdown, entity.createdAt, entity.expiresAt, entity.status);
    }
}
