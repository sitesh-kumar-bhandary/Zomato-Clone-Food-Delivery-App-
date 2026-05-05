package com.siteshkumar.zomato_clone_backend.dto.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {

    private Long id;
    private String label;
    private String street;
    private String area;
    private String city;
    private String state;
    private String pincode;
    
}