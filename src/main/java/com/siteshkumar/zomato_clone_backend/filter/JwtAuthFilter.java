package com.siteshkumar.zomato_clone_backend.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UserDetailsService userDetailsService;
    private final AuthUtils authUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");

            if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")){
                log.debug("No JWT token found in request for URI : {}", request.getRequestURI());

                filterChain.doFilter(request, response);
                return;
            }

            String token = requestTokenHeader.substring(7);

            log.debug("JWT token detected in request for URI: {}", request.getRequestURI());

            String username = authUtils.getUsernameFromToken(token);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

                log.debug("Extracted username : '{}' from JWT for URI:{}", username, request.getRequestURI());

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if(authUtils.isTokenValid(token , userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("JWT authentication successful for user '{}' on URI : {}", username, request.getRequestURI());
                }

                else {
                    log.warn("Invalid JWT token for user '{}' on URI: {}", username, request.getRequestURI());
                }
            }

            filterChain.doFilter(request, response);
        }

        catch (Exception ex){
            log.error("JWT authentication failed for request URI : {}", request.getRequestURI(), ex);

            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
