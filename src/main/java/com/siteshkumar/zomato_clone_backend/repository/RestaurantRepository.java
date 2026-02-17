package com.siteshkumar.zomato_clone_backend.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long>{

    Page<RestaurantEntity> findByCityIgnoreCaseAndActiveTrue(String city, Pageable pageable);
    Page<RestaurantEntity> findByActiveTrue(Pageable pageable);
    Optional<RestaurantEntity> findById(Long id);
}
