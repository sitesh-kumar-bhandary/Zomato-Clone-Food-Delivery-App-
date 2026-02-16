package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtils authUtils;

    @Override
    public SignupResponseDto customerSignup(SignupRequestDto request) {
        return createUser(request, Role.CUSTOMER, AccountStatus.APPROVED);
    }

    @Override
    public SignupResponseDto restaurantSignup(SignupRequestDto request){
        return createUser(request, Role.RESTAURANT, AccountStatus.PENDING);
    }

    private SignupResponseDto createUser(SignupRequestDto request, Role role, AccountStatus status){
        if(userRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException("Email already exists");

        if(userRepository.existsByPhone(request.getPhone()))
            throw new PhoneAlreadyExistsException("Phone already exists");

        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encryptedPassword);
        
        user.setStatus(status);
        user.setRole(role);

        UserEntity savedUser = userRepository.save(user);
        return new SignupResponseDto(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getStatus().name()
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(), 
                    request.getPassword()));

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        if(user.getUser().getStatus() != AccountStatus.APPROVED)
            throw new AccountNotApprovedException("Account not approved yet");

        String token = authUtils.generateAccessToken(user);

        return new LoginResponseDto(
            user.getUsername(),
            token,
            user.getUser().getRole().name()
        );
    }
}
