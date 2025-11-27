package com.an.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String email;

    private String role;

    private String status;
}