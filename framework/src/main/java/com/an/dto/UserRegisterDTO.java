package com.an.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username; // 用户名（唯一）

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "密码必须是6-20位字母或数字")
    private String password; // 明文密码

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email; // 邮箱（唯一，用于通知）

    private String phone; // 手机号（可选）
}