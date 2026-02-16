package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.dto.auth.LoginRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.auth.LoginResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.auth.SignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.auth.SignupResponseDto;
import com.siteshkumar.zomato_clone_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    
    @PostMapping("/customer-signup")
    public ResponseEntity<SignupResponseDto> customerSignup(@Valid @RequestBody SignupRequestDto request){
        SignupResponseDto response = authService.customerSignup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/restaurant-signup")
    public ResponseEntity<SignupResponseDto> restaurantSignup(@Valid @RequestBody SignupRequestDto request){
        SignupResponseDto response = authService.restaurantSignup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request){
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
