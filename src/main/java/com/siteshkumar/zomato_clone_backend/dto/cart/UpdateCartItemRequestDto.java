package com.siteshkumar.zomato_clone_backend.dto.cart;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequestDto {

    @Min(1)
    private int quantity;
    
}
