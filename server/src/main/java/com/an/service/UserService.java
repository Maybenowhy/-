package com.an.service;

import com.an.dto.UserLoginDTO;
import com.an.dto.UserRegisterDTO;
import com.an.dto.UserUpdateDTO;
import com.an.entity.User;

import java.util.List;

public interface UserService {


    /**
     * 账号密码登录
     * @param userLoginDTO 登录请求参数
     * @return 登录成功的用户信息
     */
    User login(UserLoginDTO userLoginDTO);

    User register(UserRegisterDTO registerDTO);
    // 新增：更新用户最后注销时间
    void updateLastLogoutTime(Integer userId);

    // 管理端新增方法
    List<User> getAllUsers();
    List<User> searchUsers(String keyword, String role);
    User getUserById(Long userId);
    void deleteUser(Long userId);

    void updateUserStatus(Long userId, String status);

    void updateUser(Long userId, UserUpdateDTO dto);
}
