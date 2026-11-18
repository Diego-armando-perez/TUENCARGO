package com.tuencargo.domain.model;

import java.time.Instant;

/**
 * Notificación enviada al usuario (por correo) y guardada para consulta en el panel.
 */
public record Notification(
        Long id,
        Long userId,
        String recipientEmail,
        String subject,
        String body,
        String category,
        Instant createdAt
) {

    public Notification withId(Long newId) {
        return new Notification(newId, userId, recipientEmail, subject, body, category, createdAt);
    }
}
