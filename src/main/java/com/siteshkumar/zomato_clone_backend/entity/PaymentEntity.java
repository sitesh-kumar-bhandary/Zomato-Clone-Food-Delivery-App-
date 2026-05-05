package com.siteshkumar.zomato_clone_backend.entity;

import java.math.BigDecimal;
import com.siteshkumar.zomato_clone_backend.enums.PaymentMode;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "payments", indexes = {
        @Index(name = "payment_order_ind", columnList = "order_id"),
        @Index(name = "payment_status_ind", columnList = "status")
})
public class PaymentEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private OrderEntity order;

    @Version
    @Column(nullable = false)
    private Long version;
}