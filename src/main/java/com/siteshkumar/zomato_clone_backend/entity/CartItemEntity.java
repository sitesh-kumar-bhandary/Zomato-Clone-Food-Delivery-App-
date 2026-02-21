package com.siteshkumar.zomato_clone_backend.entity;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
    name = "cart_items",
    indexes = {
        @Index(name = "cart_item_cart_ind", columnList = "cart_id"),
        @Index(name = "cart_item_cart_menu_ind", columnList = "cart_id, menu_item_id", unique = true)
    }
)
public class CartItemEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Min(1)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id", nullable = false)
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="menu_item_id", nullable =  false)
    private MenuItemEntity menuItem;

    public void updateQuantity(int quantity){
        if(quantity < 1)
            throw new IllegalArgumentException("Quantity must atleast 1");

        this.quantity = quantity;
        this.subTotal = this.priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }
}
