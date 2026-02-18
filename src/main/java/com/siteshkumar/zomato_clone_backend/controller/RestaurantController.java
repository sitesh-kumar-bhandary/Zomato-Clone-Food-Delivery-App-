package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.CreateRestaurantRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.CreateRestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.UpdateRestaurantRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.UpdateRestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    
    @PostMapping
    public ResponseEntity<CreateRestaurantResponseDto> createRestaurant(@Valid @RequestBody CreateRestaurantRequestDto request){
        CreateRestaurantResponseDto createdRestaurant = restaurantService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRestaurant);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESTAURANT', 'ADMIN')")
    public ResponseEntity<UpdateRestaurantResponseDto> updateRestaurant(@PathVariable Long id, @Valid @RequestBody UpdateRestaurantRequestDto request){
        UpdateRestaurantResponseDto updatedRestaurant = restaurantService.updateRestaurant(id, request);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id){
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantResponseDto>> getAllRestaurants(@RequestParam(required = false) String city, Pageable pageable){
        Page<RestaurantResponseDto> restaurants = restaurantService.getAllRestaurants(city, pageable);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurantById(@PathVariable Long id){
        RestaurantResponseDto restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }
}
