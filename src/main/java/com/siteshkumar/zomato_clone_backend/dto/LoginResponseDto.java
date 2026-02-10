package com.siteshkumar.zomato_clone_backend.dto;

import com.siteshkumar.zomato_clone_backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String email;
    private String token;
    private Role role;
    
}
