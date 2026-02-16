package com.siteshkumar.zomato_clone_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {
    
    private Long id;
    private String name;
    private String email;
    private String status;
    
}
