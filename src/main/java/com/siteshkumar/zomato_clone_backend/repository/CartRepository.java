package com.siteshkumar.zomato_clone_backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.CartEntity;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long>{
    Optional<CartEntity> findById(Long id);
}
