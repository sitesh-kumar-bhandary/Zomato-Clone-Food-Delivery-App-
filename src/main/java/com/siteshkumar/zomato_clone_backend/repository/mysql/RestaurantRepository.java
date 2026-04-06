package com.siteshkumar.zomato_clone_backend.repository.mysql;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long>{

    Page<RestaurantEntity> findByCityIgnoreCaseAndActiveTrueAndBlockedFalse(String city, Pageable pageable);
    Page<RestaurantEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<RestaurantEntity> findByActiveTrueAndBlockedFalse(Pageable pageable);
    Page<RestaurantEntity> findByActiveTrue(Pageable pageable);
    Optional<RestaurantEntity> findByIdAndBlockedFalse(Long id);
}
