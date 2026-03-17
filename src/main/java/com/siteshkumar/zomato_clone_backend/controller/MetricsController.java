package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.service.MetricsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/metrics")
public class MetricsController {
    
    private final MetricsService metricsService;

    @GetMapping("/db-hits")
    public ResponseEntity<Integer> getDbHits(){
        int hits = metricsService.getDbHits();
        return ResponseEntity.ok(hits);
    }
}
