package com.an.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 21:12
 * @description:
 */
@Data
public class ShoppingCart {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
