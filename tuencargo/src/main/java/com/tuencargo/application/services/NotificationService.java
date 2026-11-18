package com.tuencargo.application.services;

import com.tuencargo.application.ports.in.NotificationUseCase;
import com.tuencargo.application.ports.out.EmailPort;
import com.tuencargo.application.ports.out.NotificationRepositoryPort;
import com.tuencargo.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

/**
 * Servicio de aplicación: envía correos por el puerto EmailPort y guarda copia en el buzón.
 */
@Service
public class NotificationService implements NotificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepositoryPort notifications;
    private final EmailPort email;
    private final Clock clock;

    public NotificationService(NotificationRepositoryPort notifications, EmailPort email, Clock clock) {
        this.notifications = notifications;
        this.email = email;
        this.clock = clock;
    }

    @Override
    public void notifyUser(Long userId, String recipientEmail, String subject, String body, String category) {
        try {
            email.send(recipientEmail, subject, body);
        } catch (RuntimeException ex) {
            log.warn("No se pudo enviar el correo a {}: {}", recipientEmail, ex.getMessage());
        }
        notifications.save(new Notification(null, userId, recipientEmail, subject, body, category,
                clock.instant()));
    }

    @Override
    public List<Notification> listByUser(Long userId) {
        return notifications.findByUserId(userId);
    }

    @Override
    public List<Notification> listAll() {
        return notifications.findAll();
    }
}
