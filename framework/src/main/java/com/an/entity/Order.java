package com.an.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 21:12
 * @description:
 */
@Data
public class Order {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String status;
    private Double totalAmount;
    private String shippingAddress;
    private String shippingPhone;
    private LocalDateTime paymentTime;
    private LocalDateTime shippingTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 新增：订单明细集合
    private List<OrderItem> items;
}
