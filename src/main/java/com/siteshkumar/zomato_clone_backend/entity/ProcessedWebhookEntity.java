package com.siteshkumar.zomato_clone_backend.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "processed_webhooks")
public class ProcessedWebhookEntity {
    @Id
    private String eventId;

    @Column(nullable = false)
    private LocalDateTime processedAt;
}
