package com.siteshkumar.zomato_clone_backend.dto.cart;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryResponseDto {
    private List<CartItemResponseDto> items;
    private BigDecimal totalAmount;
    private int totalItems;
}
