package com.siteshkumar.zomato_clone_backend.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
    name="audit_logs",
    indexes = {
        @Index(name="audit_entity_ind", columnList="entityName, entityId"),
        @Index(name="audit_timestamp_ind", columnList="timestamp")
    }
)
public class AuditLogsEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    private String entityName;
    private Long entityId;
    private String action;
    private String performedBy;

    @CreationTimestamp
    private LocalDateTime timestamp;
}
