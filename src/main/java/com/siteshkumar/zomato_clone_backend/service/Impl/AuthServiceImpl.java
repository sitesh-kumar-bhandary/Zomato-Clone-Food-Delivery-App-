package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siteshkumar.zomato_clone_backend.dto.auth.LoginRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.auth.LoginResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.auth.SignupRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.auth.SignupResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.AccountNotApprovedException;
import com.siteshkumar.zomato_clone_backend.exception.EmailAlreadyExistsException;
import com.siteshkumar.zomato_clone_backend.exception.PhoneAlreadyExistsException;
import com.siteshkumar.zomato_clone_backend.repository.UserRepository;
import com.siteshkumar.zomato_clone_backend.security.CustomUserDetails;
import com.siteshkumar.zomato_clone_backend.service.AuthService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtils authUtils;

    @Override
    @Transactional
    public SignupResponseDto customerSignup(SignupRequestDto request) {

        log.info("Customer signup initiated for email: {}", request.getEmail());

        return createUser(request, Role.CUSTOMER, AccountStatus.APPROVED);
    }

    @Override
    @Transactional
    public SignupResponseDto restaurantSignup(SignupRequestDto request){

        log.info("Restaurant signup initiated for email: {}", request.getEmail());

        return createUser(request, Role.RESTAURANT, AccountStatus.PENDING);
    }

    private SignupResponseDto createUser(SignupRequestDto request, Role role, AccountStatus status){

        log.info("Creating user with email: {}, role: {}, status: {}", 
                    request.getEmail(), role, status);

        if(userRepository.existsByEmail(request.getEmail())) {
            log.warn("Signup failed - Email already exists: {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if(userRepository.existsByPhone(request.getPhone())) {
            log.warn("Signup failed - Phone already exists: {}", request.getPhone());
            throw new PhoneAlreadyExistsException("Phone already exists");
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        log.debug("Password encrypted for email: {}", request.getEmail());

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encryptedPassword);
        
        user.setStatus(status);
        user.setRole(role);

        UserEntity savedUser = userRepository.save(user);

        log.info("User created successfully with id: {} and email: {}", 
                    savedUser.getId(), savedUser.getEmail());

        return new SignupResponseDto(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getStatus().name()
        );
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {

        log.info("Login attempt for email: {}", request.getEmail());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(), 
                    request.getPassword()));

        log.info("Authentication successful for email: {}", request.getEmail());

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        if(user.getUser().getStatus() != AccountStatus.APPROVED) {
            log.warn("Login blocked - Account not approved for email: {}", request.getEmail());
            throw new AccountNotApprovedException("Account not approved yet");
        }

        String token = authUtils.generateAccessToken(user);

        log.info("JWT token generated for user: {}", user.getUsername());

        return new LoginResponseDto(
            user.getUsername(),
            token,
            user.getUser().getRole().name()
        );
    }
}