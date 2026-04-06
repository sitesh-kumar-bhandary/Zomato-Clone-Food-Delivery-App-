package com.siteshkumar.zomato_clone_backend.repository.mysql;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

    Page<RestaurantEntity> findByRestaurantStatus(AccountStatus status, Pageable pageable);

    Page<RestaurantEntity> findByCityIgnoreCaseAndRestaurantStatus(String city, AccountStatus status, Pageable pageable);

    Optional<RestaurantEntity> findByIdAndRestaurantStatus(Long id, AccountStatus status);
}
