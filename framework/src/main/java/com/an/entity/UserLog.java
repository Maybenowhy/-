package com.an.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLog {
    private Long id;
    private Long userId;
    private String actionType; // browse / purchase
    private Long targetId;     // 商品ID或订单ID
    private String ipAddress;
    private LocalDateTime createdAt;
}
