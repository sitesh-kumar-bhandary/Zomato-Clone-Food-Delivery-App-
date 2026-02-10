package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.siteshkumar.zomato_clone_backend.dto.LoginRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.LoginResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.RestaurantSignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.RestaurantSignupResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.EmailAlreadyExistsException;
import com.siteshkumar.zomato_clone_backend.dto.CustomerSignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.CustomerSignupResponseDto;
import com.siteshkumar.zomato_clone_backend.repository.RestaurantRepository;
import com.siteshkumar.zomato_clone_backend.repository.UserRepository;
import com.siteshkumar.zomato_clone_backend.security.CustomUserDetails;
import com.siteshkumar.zomato_clone_backend.service.AuthService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestaurantRepository restaurantRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthUtils authUtils;

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
    public RestaurantSignupResponseDto restaurantSignup(RestaurantSignupRequestDto request) {
        if(userRepository.existsByEmail(request.getOwnerEmail()))
            throw new EmailAlreadyExistsException("Email already exists");

        String encryptedPassword = passwordEncoder.encode(request.getOwnerPassword());

        UserEntity user = new UserEntity();
        user.setName("Restaurant Owner");
        user.setEmail(request.getOwnerEmail());
        user.setPassword(encryptedPassword);
        user.setRole(Role.RESTAURANT);
        user.setStatus(AccountStatus.PENDING);

        UserEntity savedUser = userRepository.save(user);

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName(request.getRestaurantName());
        restaurant.setCity(request.getCity());
        restaurant.setActive(false);
        restaurant.setOwner(savedUser);

        RestaurantEntity savedRestaurant = restaurantRepository.save(restaurant);

        return new RestaurantSignupResponseDto(
            savedRestaurant.getName(),
            savedRestaurant.getCity(),
            savedUser.getEmail(),
            savedUser.getStatus()
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(), 
                    request.getPassword()));

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        String token = authUtils.generateAccessToken(user);

        return new LoginResponseDto(
            user.getUsername(),
            token,
            user.getUser().getRole()
        );
    }
}
