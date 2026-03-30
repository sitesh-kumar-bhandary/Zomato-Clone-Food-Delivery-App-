package com.siteshkumar.zomato_clone_backend.repository.mysql;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findByUser_Id(Long id, Pageable pageable);

    Page<OrderEntity> findByRestaurant_Owner_Id(Long id, Pageable pageable);

    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);

    List<OrderEntity> findByStatus(OrderStatus status);

    List<OrderEntity> findByPaymentStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime time);

    long countByStatus(OrderStatus status);

    @Query("""
            SELECT o.status, COUNT(o)
            FROM OrderEntity o
            GROUP BY o.status
            """)
    List<Object[]> countOrdersGroupByStatus();

    @Query("""
                SELECT COALESCE(SUM(o.totalAmount), 0)
                FROM OrderEntity o
                WHERE o.paymentStatus = com.siteshkumar.zomato_clone_backend.enums.PaymentStatus.COMPLETED
            """)
    Double getTotalRevenue();
}
