package com.siteshkumar.zomato_clone_backend.dto.menuItem;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuItemResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long menuItemId;
    private Long restaurantId;
    private String name;
    private BigDecimal price;
    private boolean active;

}
