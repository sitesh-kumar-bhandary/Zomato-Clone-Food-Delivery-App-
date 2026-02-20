package com.siteshkumar.zomato_clone_backend.dto.menuItem;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponseDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private String restaurantName;

}
