package com.an.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.an.constant.MessageConstant;
import com.an.dto.UserLoginDTO;
import com.an.dto.UserRegisterDTO;
import com.an.dto.UserUpdateDTO;
import com.an.entity.User;
import com.an.exception.BusinessException;
import com.an.exception.LoginFailedException;
import com.an.mapper.UserMapper;
import com.an.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录
     */
    @Override
    public User login(UserLoginDTO dto) {

        String loginAccount = dto.getLoginAccount();
        String rawPassword = dto.getPassword();

        User user = userMapper.selectByUsernameOrEmail(loginAccount);
        if (user == null) {
            throw new LoginFailedException("账号或密码错误");
        }

        // 使用 Hutool BCrypt 校验密码
        if (!BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
            throw new LoginFailedException("账号或密码错误");
        }

        updateLoginTime(user.getId());
        return user;
    }


    /**
     * 注册
     */
    @Override
    public User register(UserRegisterDTO dto) {

        if (userMapper.selectByUsernameOrEmail(dto.getUsername()) != null) {
            throw new BusinessException("用户名已被占用");
        }
        if (userMapper.selectByUsernameOrEmail(dto.getEmail()) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 使用 Hutool BCrypt 加密
        String encryptedPassword = BCrypt.hashpw(dto.getPassword());

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(encryptedPassword);
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("customer");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);

        return user;
    }

    @Override
    @Transactional
    public void updateLastLogoutTime(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        int count = userMapper.updateLastLogoutTime(userId, now);

        if (count == 0) {
            log.warn("注销失败：用户不存在 userId={}", userId);
        } else {
            log.info("用户注销成功 userId={} at={}", userId, now);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            // 使用现有的 list 方法，传入空字符串获取所有用户
            return userMapper.list("");
        } catch (Exception e) {
            log.error("获取所有用户失败: {}", e.getMessage());
            throw new RuntimeException("获取用户列表失败");
        }
    }

    @Override
    public List<User> searchUsers(String keyword, String role) {
        try {
            // 使用现有的 search 方法进行关键词搜索
            List<User> users = userMapper.search(keyword);

            // 如果指定了角色，进行角色过滤
            if (role != null && !role.isEmpty()) {
                users = users.stream()
                        .filter(user -> role.equals(user.getRole()))
                        .collect(Collectors.toList());
            }

            return users;
        } catch (Exception e) {
            log.error("搜索用户失败: {}", e.getMessage());
            throw new RuntimeException("搜索用户失败");
        }
    }

    @Override
    public User getUserById(Long userId) {
        try {
            // 使用现有的 getById 方法，注意参数类型转换
            return userMapper.getById(userId.intValue());
        } catch (Exception e) {
            log.error("根据ID获取用户失败: {}", e.getMessage());
            throw new RuntimeException("获取用户失败");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            // 物理删除用户
            int result = userMapper.delete(userId);
            if (result == 0) {
                throw new RuntimeException("用户不存在或删除失败");
            }
            log.info("用户删除成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage());
            throw new RuntimeException("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    public void updateUser(Long userId, UserUpdateDTO dto) {
        // 先获取现有用户信息
        User existingUser = getUserById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setUsername(dto.getUsername());
        userToUpdate.setEmail(dto.getEmail());
        userToUpdate.setRole(dto.getRole());
        userToUpdate.setUpdatedAt(LocalDateTime.now());

        userMapper.updateUser(userToUpdate);
    }

    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long userId, String status) {
        // 先检查用户是否存在
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        userMapper.updateStatus(userId, status);
    }


    private void updateLoginTime(Long userId) {
        userMapper.updateLastLogin(userId, LocalDateTime.now());
    }
}
