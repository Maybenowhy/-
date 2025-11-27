package com.an.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterVO {
    private Long id; // 注册成功的用户ID
    private String username; // 用户名
    private String message; // 提示信息
}