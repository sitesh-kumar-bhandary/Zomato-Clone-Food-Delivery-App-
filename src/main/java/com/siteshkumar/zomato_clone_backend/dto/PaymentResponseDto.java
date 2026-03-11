package com.siteshkumar.zomato_clone_backend.dto;

import java.math.BigDecimal;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private Long paymentId;
    private Long orderId;
    private String paymentMode;
    private String transactionId;
    private PaymentStatus status;
    private BigDecimal amount;
}
