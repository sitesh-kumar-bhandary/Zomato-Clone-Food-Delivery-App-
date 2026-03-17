package com.siteshkumar.zomato_clone_backend.service;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class MetricsService {
    
    private AtomicInteger dbHits = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        System.out.println("MetricsService instance: " + this);
    }

    public int getDbHits(){
        return dbHits.get();
    }

    public void incrementDbHits() {
        dbHits.incrementAndGet();
    }

    public void reset() {
        dbHits.set(0);
    }
}
