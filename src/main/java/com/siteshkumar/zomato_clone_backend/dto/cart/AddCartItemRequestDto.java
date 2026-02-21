package com.siteshkumar.zomato_clone_backend.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequestDto {
    private Long menuItemId;
    private int quantity;
}
