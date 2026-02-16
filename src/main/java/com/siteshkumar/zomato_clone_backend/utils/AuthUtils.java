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

@Component
public class AuthUtils {
    
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Claims getAllClaims(String token){
        return Jwts
            .parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public String generateAccessToken(CustomUserDetails user){
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
        Claims claims = getAllClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = getUsernameFromToken(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        Claims claims = getAllClaims(token);
        return claims.getExpiration().before(new Date());
    }

    public CustomUserDetails getCurrentLoggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || (auth.getPrincipal() instanceof CustomUserDetails)){
            throw new AuthenticationCredentialsNotFoundException("User not authenticated");
        }

        return (CustomUserDetails) auth.getPrincipal();
    }
}
