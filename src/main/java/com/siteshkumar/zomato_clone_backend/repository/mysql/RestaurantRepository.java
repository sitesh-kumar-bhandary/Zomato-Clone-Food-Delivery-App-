package com.siteshkumar.zomato_clone_backend.repository.mysql;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

    List<RestaurantEntity> findByRestaurantStatus(AccountStatus status);
    List<RestaurantEntity> findByRestaurantStatusNot(AccountStatus status);
    List<RestaurantEntity> findByCityIgnoreCaseAndRestaurantStatus(String city, AccountStatus status);
    Optional<RestaurantEntity> findByIdAndRestaurantStatus(Long id, AccountStatus status);
    List<RestaurantEntity> findByOwnerId(Long ownerId);
}
