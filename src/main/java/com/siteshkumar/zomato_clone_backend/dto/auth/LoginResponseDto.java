package com.siteshkumar.zomato_clone_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String email;
    private String token;
    private String role;
    
}
