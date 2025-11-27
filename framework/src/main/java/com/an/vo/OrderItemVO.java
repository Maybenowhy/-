package com.an.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 21:37
 * @description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO implements Serializable {
    private Long productId;            // 商品ID
    private String productName;        // 商品名称
    private BigDecimal unitPrice;      // 单价
    private Integer quantity;          // 数量
    private BigDecimal subtotal;       // 小计
    private String imageUrl;           // 商品图片
}
