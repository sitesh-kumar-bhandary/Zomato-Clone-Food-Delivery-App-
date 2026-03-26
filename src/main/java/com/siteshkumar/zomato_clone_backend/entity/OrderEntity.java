package com.siteshkumar.zomato_clone_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;
import com.siteshkumar.zomato_clone_backend.enums.RefundStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "orders", indexes = {
        @Index(name = "order_user_ind", columnList = "user_id"),
        @Index(name = "order_status_ind", columnList = "status"),
        @Index(name = "order_payment_created_ind", columnList = "paymentStatus, createdAt")
})
public class OrderEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.CREATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @Embedded
    private AddressDetails deliveryDetails;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(unique = true)
    private String paymentIntentId;

    @Column
    private LocalDateTime paymentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus = RefundStatus.NONE;

    @Version
    @Column(nullable = false)
    private Long version;

    // Business Methods

    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    public void updateStatus(OrderStatus newStatus) {
        if (this.status == newStatus)
            return;

        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Cannot transition from " + this.status + " to " + newStatus);
        }

        this.status = newStatus;
    }

    public boolean isPaid() {
        return this.paymentStatus == PaymentStatus.SUCCESS;
    }

    // Payment Helper Methods

    public void markPaymentSuccess(String paymentIntentId) {
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.paymentIntentId = paymentIntentId;
        this.paymentTime = LocalDateTime.now();
    }

    public void markPaymentFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void markPaymentTimeout() {
        this.paymentStatus = PaymentStatus.TIMEOUT;
    }

    public void markRefundSuccess() {
        this.refundStatus = RefundStatus.SUCCESS;
    }

    // Utility Methods

    public boolean isCancellable() {
        return this.status != OrderStatus.DELIVERED &&
                this.status != OrderStatus.CANCELLED;
    }
}