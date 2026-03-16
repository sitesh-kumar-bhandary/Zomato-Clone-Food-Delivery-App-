package com.siteshkumar.zomato_clone_backend.dto.menuItem;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private BigDecimal price;
    private String restaurantName;

}
