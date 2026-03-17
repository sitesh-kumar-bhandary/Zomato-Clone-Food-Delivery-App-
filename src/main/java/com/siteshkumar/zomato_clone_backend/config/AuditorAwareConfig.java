package com.siteshkumar.zomato_clone_backend.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuditorAwareConfig implements AuditorAware<String>{

    @Override
    public Optional<String> getCurrentAuditor() {

        log.info("Fetching current auditor...");

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null) {
            log.warn("Authentication object is null. Returning SYSTEM as auditor.");
            return Optional.of("SYSTEM");
        }

        if(!auth.isAuthenticated()) {
            log.warn("User is not authenticated. Returning SYSTEM as auditor.");
            return Optional.of("SYSTEM");
        }

        if(auth.getPrincipal().equals("anonymousUser")) {
            log.warn("Anonymous user detected. Returning SYSTEM as auditor.");
            return Optional.of("SYSTEM");
        }

        String username = auth.getName();
        log.info("Authenticated user found: {}", username);

        return Optional.of(username);
    }
}