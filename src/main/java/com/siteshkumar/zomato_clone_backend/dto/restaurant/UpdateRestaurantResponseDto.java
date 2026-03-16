package com.siteshkumar.zomato_clone_backend.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRestaurantResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String city;
    private boolean active;
    
}
