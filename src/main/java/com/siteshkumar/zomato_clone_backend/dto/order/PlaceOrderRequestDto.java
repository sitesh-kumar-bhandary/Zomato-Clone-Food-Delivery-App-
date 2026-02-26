package com.siteshkumar.zomato_clone_backend.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequestDto {

    @NotNull(message = "Please select the address to place the order")
    private Long addressId;

}
