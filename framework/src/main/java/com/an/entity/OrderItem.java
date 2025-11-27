package com.an.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 21:13
 * @description:
 */
@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;    // 商品名称
    private String productImage;   // 商品图片
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
    private LocalDateTime createdAt;
}
