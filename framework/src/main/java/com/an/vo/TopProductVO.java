package com.an.vo;

import lombok.Data;

@Data
public class TopProductVO {
    private Long productId;
    private String productName;
    private Long totalQuantity;
    private Long totalRevenue;
}
