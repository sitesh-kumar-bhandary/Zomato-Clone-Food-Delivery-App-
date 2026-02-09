package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.siteshkumar.zomato_clone_backend.dto.LoginRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.LoginResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.RestaurantSignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.RestaurantSignupResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.EmailAlreadyExistsException;
import com.siteshkumar.zomato_clone_backend.dto.CustomerSignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.CustomerSignupResponseDto;
import com.siteshkumar.zomato_clone_backend.repository.UserRepository;
import com.siteshkumar.zomato_clone_backend.service.AuthService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomerSignupResponseDto customerSignup(CustomerSignupRequestDto request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException("Email already exists");

        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encryptedPassword);
        
        user.setStatus(AccountStatus.APPROVED);
        user.setRole(Role.CUSTOMER);

        UserEntity savedUser = userRepository.save(user);
        return new CustomerSignupResponseDto(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail()
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

    @Override
    public RestaurantSignupResponseDto restaurantSignup(RestaurantSignupRequestDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'RestaurantSignup'");
    }
}
