package com.siteshkumar.zomato_clone_backend.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bandwidth limit(int capacity, Duration duration) {
        return Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, duration)
                .build();
    }

    private Bucket createBucket(String uri) {

        if (uri.contains("/auth")) {
            return Bucket.builder()
                    .addLimit(limit(5, Duration.ofMinutes(1)))
                    .build();
        }

        if (uri.contains("/orders")) {
            return Bucket.builder()
                    .addLimit(limit(10, Duration.ofMinutes(1)))
                    .build();
        }

        return Bucket.builder()
                .addLimit(limit(50, Duration.ofMinutes(1)))
                .build();
    }

    private Bucket resolveBucket(String ip, String uri) {
        String key = ip + ":" + uri;
        return cache.computeIfAbsent(key, k -> createBucket(uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        Bucket bucket = resolveBucket(ip, uri);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many requests. Try later.");
        }
    }
}