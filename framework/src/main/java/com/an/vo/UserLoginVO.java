package com.an.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO implements Serializable {

    /**
     * 用户ID（数据库中users表的id，用于前端标识用户）
     */
    private Long id;

    /**
     * 第三方登录标识（如微信openid，若支持第三方登录则返回，否则可为null）
     */
    private String openid;

    /**
     * JWT令牌（前端后续请求需携带此令牌进行身份验证）
     */
    private String token;

    /**
     * 用户角色：customer / sales / admin
     */
    private String role;

    private String username;

}
