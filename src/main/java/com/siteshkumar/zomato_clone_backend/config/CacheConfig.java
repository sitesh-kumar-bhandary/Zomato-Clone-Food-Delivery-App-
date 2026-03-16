package com.siteshkumar.zomato_clone_backend.config;

import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory){
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                                .defaultCacheConfig()
                                .entryTtl(Duration.ofDays(7))
                                .serializeValuesWith(RedisSerializationContext
                                                .SerializationPair
                                                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultConfig).build();
    }
}
