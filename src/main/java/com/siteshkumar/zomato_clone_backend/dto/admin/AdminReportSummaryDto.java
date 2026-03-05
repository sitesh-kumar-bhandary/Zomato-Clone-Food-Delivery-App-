package com.siteshkumar.zomato_clone_backend.dto.admin;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportSummaryDto {

    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Map<String, Long> ordersByStatus;
    
}
