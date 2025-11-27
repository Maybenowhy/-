package com.an.dto;

import lombok.Data;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 21:21
 * @description:
 */
@Data
public class PayDTO {
    private Long orderId;
    private String payMethod; // 微信 / 支付宝 / 余额（可选）
}

