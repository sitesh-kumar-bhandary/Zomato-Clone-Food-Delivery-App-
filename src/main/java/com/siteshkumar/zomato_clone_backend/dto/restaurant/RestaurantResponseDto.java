package com.siteshkumar.zomato_clone_backend.dto.restaurant;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDto implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String city;

}
