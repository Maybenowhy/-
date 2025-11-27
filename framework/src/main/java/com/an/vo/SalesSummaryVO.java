package com.an.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalesSummaryVO {
    private BigDecimal totalSales;  // 总销售额
    private Integer totalOrders;    // 订单数
    private BigDecimal avgOrderAmount; // 客单价
}
