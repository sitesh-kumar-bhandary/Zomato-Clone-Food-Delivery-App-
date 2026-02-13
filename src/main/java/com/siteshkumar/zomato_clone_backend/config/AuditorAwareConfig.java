package com.siteshkumar.zomato_clone_backend.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareConfig implements AuditorAware<String>{

    @Override
    public Optional<String> getCurrentAuditor() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || ! auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser"))
            return Optional.of("SYSTEM");

        return Optional.of(auth.getName());
    }
}
