package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.dto.LoginRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.LoginResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.RestaurantSignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.RestaurantSignupResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.CustomerSignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.CustomerSignupResponseDto;
import com.siteshkumar.zomato_clone_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    
    @PostMapping("/customer-signup")
    public ResponseEntity<CustomerSignupResponseDto> customerSignup(@Valid @RequestBody CustomerSignupRequestDto request){
        CustomerSignupResponseDto response = authService.customerSignup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/restaurant-signup")
    public ResponseEntity<RestaurantSignupResponseDto> restaurantSignup(@Valid @RequestBody RestaurantSignupRequestDto request){
        RestaurantSignupResponseDto response = authService.restaurantSignup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request){
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
