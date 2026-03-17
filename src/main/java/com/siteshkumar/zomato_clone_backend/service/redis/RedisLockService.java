package com.siteshkumar.zomato_clone_backend.service.redis;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLockService {
    
    private final StringRedisTemplate stringRedisTemplate;

    public String acquireLock(String key, long timeoutMillis){

        String value = UUID.randomUUID().toString();

        log.debug("Attempting to acquire Redis lock. Key: {}, Timeout: {}ms", key, timeoutMillis);

        Boolean success = stringRedisTemplate
                            .opsForValue()
                            .setIfAbsent(key, value, Duration.ofMillis(timeoutMillis));

        if(Boolean.TRUE.equals(success)) {
            log.info("Lock acquired successfully. Key: {}", key);
            return value;
        }

        log.warn("Failed to acquire lock. Key already in use: {}", key);

        return null;
    }

    public void releaseLock(String key, String value){

        log.debug("Attempting to release Redis lock. Key: {}", key);

        String current = stringRedisTemplate.opsForValue().get(key);

        if(current != null && value.equals(current)) {
            stringRedisTemplate.delete(key);
            log.info("Lock released successfully. Key: {}", key);
        } else {
            log.warn("Lock release skipped. Key mismatch or lock not found. Key: {}", key);
        }
    }
}