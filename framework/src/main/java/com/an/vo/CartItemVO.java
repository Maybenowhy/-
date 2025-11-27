package com.an.vo;

import lombok.Data;

@Data
public class CartItemVO {
    private Long productId;
    private Integer quantity;
    private Double price; // 可选，用于验证
}