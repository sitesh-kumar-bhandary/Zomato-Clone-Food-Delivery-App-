package com.siteshkumar.zomato_clone_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
    name="orders",
    indexes = {
        @Index(name="order_user_ind", columnList = "user_id"),
        @Index(name="order_status_ind", columnList = "status"),
        @Index(name="order_created_at_ind", columnList = "created_at")
    }
)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity user;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;
}
