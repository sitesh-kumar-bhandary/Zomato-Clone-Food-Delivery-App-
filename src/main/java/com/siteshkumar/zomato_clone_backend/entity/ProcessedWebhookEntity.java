package com.siteshkumar.zomato_clone_backend.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "processed_webhooks",
    indexes = {
        @Index(name = "webhook_processed_at_ind", columnList = "processedAt")
    }
)
public class ProcessedWebhookEntity {
    @Id
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
