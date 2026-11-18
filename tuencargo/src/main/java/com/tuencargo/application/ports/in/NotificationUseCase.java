package com.tuencargo.application.ports.in;

import com.tuencargo.domain.model.Notification;

import java.util.List;

/** Caso de uso: notificaciones por correo y buzón del usuario. */
public interface NotificationUseCase {

    void notifyUser(Long userId, String recipientEmail, String subject, String body, String category);

    List<Notification> listByUser(Long userId);

    List<Notification> listAll();
}
