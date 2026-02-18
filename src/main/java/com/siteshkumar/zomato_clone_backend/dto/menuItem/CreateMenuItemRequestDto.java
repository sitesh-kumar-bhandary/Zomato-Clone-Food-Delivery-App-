package com.siteshkumar.zomato_clone_backend.dto.menuItem;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuItemRequestDto {
    
    @NotBlank(message="Item name is required")
    private String name;

    @NotNull(message="Price of the item is required")
    @DecimalMin(value="0.01", message="Price must be greater than 0")
    private BigDecimal price;
}
