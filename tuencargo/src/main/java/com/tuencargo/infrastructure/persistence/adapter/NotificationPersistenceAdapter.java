package com.tuencargo.infrastructure.persistence.adapter;

import com.tuencargo.application.ports.out.NotificationRepositoryPort;
import com.tuencargo.domain.model.Notification;
import com.tuencargo.infrastructure.persistence.entity.NotificationEntity;
import com.tuencargo.infrastructure.persistence.repository.JpaNotificationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {

    private final JpaNotificationRepository repository;

    public NotificationPersistenceAdapter(JpaNotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.id = notification.id();
        entity.userId = notification.userId();
        entity.recipientEmail = notification.recipientEmail();
        entity.subject = notification.subject();
        entity.body = notification.body();
        entity.category = notification.category();
        entity.createdAt = notification.createdAt();
        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByUserId(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(NotificationPersistenceAdapter::toDomain)
                .toList();
    }

    private static Notification toDomain(NotificationEntity entity) {
        return new Notification(entity.id, entity.userId, entity.recipientEmail, entity.subject,
                entity.body, entity.category, entity.createdAt);
    }
}
