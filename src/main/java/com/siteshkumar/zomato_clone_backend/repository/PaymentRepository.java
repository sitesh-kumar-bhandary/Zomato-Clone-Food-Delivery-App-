package com.siteshkumar.zomato_clone_backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long>{
    Optional<PaymentEntity> findByOrderId(Long orderId);
    Optional<PaymentEntity> findByStripePaymentIntentId(String stripePaymentIntentId);
}
