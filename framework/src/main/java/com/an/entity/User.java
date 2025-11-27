package com.an.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // 对应数据库users表字段
    private Long id; // 用户ID
    private String username; // 用户名（唯一）
    private String passwordHash; // 密码哈希（存储加密后的密码，非明文）
    private String email; // 邮箱（唯一，用于登录和订单确认）
    private String role; // 角色（顾客/销售）
    private String phone; // 联系电话（可选）
    private LocalDateTime createdAt; // 注册时间
    private LocalDateTime updatedAt; // 信息更新时间
    private LocalDateTime lastLogin; // 最后登录时间
    private LocalDateTime lastLogoutTime;



}
