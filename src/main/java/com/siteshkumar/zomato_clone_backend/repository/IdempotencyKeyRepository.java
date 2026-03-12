package com.siteshkumar.zomato_clone_backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.IdempotencyKeyEntity;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, Long>{
    Optional<IdempotencyKeyEntity> findByIdempotencyKey(String key);
}
