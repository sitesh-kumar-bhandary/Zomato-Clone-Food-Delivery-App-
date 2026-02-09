package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.LoginRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.LoginResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.RestaurantSignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.RestaurantSignupResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.CustomerSignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.CustomerSignupResponseDto;

public interface AuthService {
    CustomerSignupResponseDto customerSignup(CustomerSignupRequestDto request);
    RestaurantSignupResponseDto restaurantSignup(RestaurantSignupRequestDto request);
    LoginResponseDto login(LoginRequestDto request);
}
