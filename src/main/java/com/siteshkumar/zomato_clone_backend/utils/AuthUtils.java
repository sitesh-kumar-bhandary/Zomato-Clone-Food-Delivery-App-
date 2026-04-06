package com.siteshkumar.zomato_clone_backend.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.siteshkumar.zomato_clone_backend.security.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthUtils {
    
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    private SecretKey getSecretKey(){
        log.debug("Generating secret key for JWT");
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Claims getAllClaims(String token){

        log.debug("Extracting claims from JWT token");

        return Jwts
            .parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public String generateAccessToken(CustomUserDetails user){

        log.info("Generating JWT token for user: {}", user.getUsername());

        return Jwts
            .builder()
            .subject(user.getUsername())
            .claim("role", user.getAuthorities().iterator().next().getAuthority())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(getSecretKey())
            .compact();
    }

    public String getUsernameFromToken(String token){

        log.debug("Extracting username from JWT token");

        Claims claims = getAllClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){

        log.debug("Validating JWT token for user: {}", userDetails.getUsername());

        String username = getUsernameFromToken(token);

        boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);

        if(!isValid){
            log.warn("Invalid JWT token for user: {}", userDetails.getUsername());
        }

        return isValid;
    }

    public boolean isTokenExpired(String token){

        log.debug("Checking if JWT token is expired");

        Claims claims = getAllClaims(token);
        return claims.getExpiration().before(new Date());
    }

    public CustomUserDetails getCurrentLoggedInUser(){

        log.debug("Fetching current authenticated user from SecurityContext");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)){
            log.error("Authentication not found or invalid principal");
            throw new AuthenticationCredentialsNotFoundException("User not authenticated");
        }

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        log.info("Current authenticated user fetched: {}", user.getUsername());

        return user;
    }
}