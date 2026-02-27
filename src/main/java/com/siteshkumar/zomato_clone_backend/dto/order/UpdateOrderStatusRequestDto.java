package com.siteshkumar.zomato_clone_backend.dto.order;

import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequestDto {
    
    @NotNull
    private OrderStatus status;
}
