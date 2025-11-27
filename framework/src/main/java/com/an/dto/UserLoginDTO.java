package com.an.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
public class UserLoginDTO implements Serializable {

    /**
     * 登录账号（支持用户名或邮箱）
     */
    @NotBlank(message = "登录账号不能为空")
    private String loginAccount; // 前端传递的用户名或邮箱

    /**
     * 登录密码（明文）
     */
    @NotBlank(message = "登录密码不能为空")
    private String password; // 前端传递的明文密码

}
