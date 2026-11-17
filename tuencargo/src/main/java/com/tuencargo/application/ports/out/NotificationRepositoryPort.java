package com.tuencargo.application.ports.out;

import com.tuencargo.domain.model.Notification;

import java.util.List;

/** Puerto de salida: persistencia de notificaciones. */
public interface NotificationRepositoryPort {

    Notification save(Notification notification);

    List<Notification> findByUserId(Long userId);

    List<Notification> findAll();
}
