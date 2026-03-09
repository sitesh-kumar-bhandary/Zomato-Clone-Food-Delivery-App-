package com.siteshkumar.zomato_clone_backend.service.redis;

import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    
    private final StringRedisTemplate stringRedisTemplate;

    public String acquireLock(String key, long timeoutMillis){
        String value = UUID.randomUUID().toString();

        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(timeoutMillis));

        if(Boolean.TRUE.equals(success))
            return value;

        return null;
    }

    public void releaseLock(String key, String value){
        String current = stringRedisTemplate.opsForValue().get(key);

        if(current != null && value.equals(current))
            stringRedisTemplate.delete(key);
    }
}
