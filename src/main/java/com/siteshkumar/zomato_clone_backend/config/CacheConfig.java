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
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory){

        log.info("Initializing RedisCacheManager...");

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                                .defaultCacheConfig()
                                .entryTtl(Duration.ofDays(7))
                                .serializeValuesWith(RedisSerializationContext
                                                .SerializationPair
                                                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        log.info("Redis cache configured with TTL of {} days and JSON serialization", 7);

        RedisCacheManager cacheManager = RedisCacheManager
                                            .builder(connectionFactory)
                                            .cacheDefaults(defaultConfig)
                                            .build();

        log.info("RedisCacheManager bean created successfully");

        return cacheManager;
    }
}