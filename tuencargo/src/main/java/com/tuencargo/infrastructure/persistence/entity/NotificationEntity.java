package com.tuencargo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Long userId;

    @Column(nullable = false, length = 160)
    public String recipientEmail;

    @Column(nullable = false, length = 200)
    public String subject;

    @Lob
    @Column(nullable = false, length = 4000)
    public String body;

    @Column(nullable = false, length = 30)
    public String category;

    @Column(nullable = false)
    public Instant createdAt;
}
