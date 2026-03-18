package com.siteshkumar.zomato_clone_backend.dto.payment;

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
public class PaymentIntentResponseDto {

    private Long orderId;
    private String paymentIntentId;
    private String clientSecret;
    private PaymentStatus status;
    private BigDecimal amount;

}
