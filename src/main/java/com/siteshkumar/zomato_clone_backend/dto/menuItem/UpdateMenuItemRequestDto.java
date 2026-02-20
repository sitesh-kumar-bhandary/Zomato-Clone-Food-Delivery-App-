package com.siteshkumar.zomato_clone_backend.dto.menuItem;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuItemRequestDto {

    private String name;

    @DecimalMin(value="0.01", message="Price must be greater than 0")
    private BigDecimal price;
}
