package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.auth.LoginRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.auth.LoginResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.auth.SignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.auth.SignupResponseDto;

public interface AuthService {
    SignupResponseDto customerSignup(SignupRequestDto request);
    SignupResponseDto restaurantSignup(SignupRequestDto request);
    LoginResponseDto login(LoginRequestDto request);
}
