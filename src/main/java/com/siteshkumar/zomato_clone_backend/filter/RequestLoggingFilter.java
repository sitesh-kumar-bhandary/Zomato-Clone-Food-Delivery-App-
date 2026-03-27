package com.siteshkumar.zomato_clone_backend.filter;

import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        // Generating request id
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);

        try {
            filterChain.doFilter(request, response);
        }

        finally {
            long duration = System.currentTimeMillis() - startTime;

            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();

            log.info("[REQUEST] {} {} | {} | {}ms | reqId={}",
                    method, uri, status, duration, requestId);

            MDC.remove(REQUEST_ID);
        }
    }
}
