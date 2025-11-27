package com.an.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSubmitVO implements Serializable {
    private String shippingAddress;     // 收货地址
    private String shippingPhone;       // 收货电话
    private List<CartItemVO> cartItems; // 购物车商品列表
}
