package com.siteshkumar.zomato_clone_backend.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;
import jakarta.persistence.LockModeType;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long>{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MenuItemEntity m WHERE m.id = :id")
    Optional<MenuItemEntity> findWithLockById(Long id);
    
    Optional<MenuItemEntity> findByIdAndRestaurantId(Long menuItemId, Long restaurantId);
    Page<MenuItemEntity> findByRestaurantId(Long restaurantId, Pageable pageable);
    Page<MenuItemEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<MenuItemEntity> findByRestaurantIdAndAvailableTrue(Long restaurantId, Pageable pageable);
}
