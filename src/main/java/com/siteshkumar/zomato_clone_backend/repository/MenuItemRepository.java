package com.siteshkumar.zomato_clone_backend.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long>{
    Optional<MenuItemEntity> findByIdAndRestaurantId(Long menuItemId, Long restaurantId);
    Page<MenuItemEntity> findByRestaurantId(Long restaurantId, Pageable pageable);
    Page<MenuItemEntity> findByRestaurantIdAndAvailableTrue(Long restaurantId, Pageable pageable);
}
