package com.siteshkumar.zomato_clone_backend.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
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
import lombok.AccessLevel;
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
    }
)
public class OrderEntity extends AuditableEntity{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.CREATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @Embedded
    private AddressDetails deliveryDetails;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    public void updateStatus(OrderStatus newStatus){
        if(this.status == newStatus)
            return;

        if(! this.status.canTransitionTo(newStatus))
            throw new IllegalStateException("Cannot transition from " + this.status + " to " + newStatus);

        this.status = newStatus;
    }
}
