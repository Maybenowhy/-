package com.an.controller.user;

import com.an.constant.JwtClaimsConstant;
import com.an.context.BaseContext;
import com.an.dto.UserLoginDTO;
import com.an.dto.UserRegisterDTO;
import com.an.dto.UserUpdateDTO;
import com.an.entity.User;
import com.an.properties.JwtProperties;
import com.an.result.Result;
import com.an.service.UserService;
import com.an.utils.JwtUtil;
import com.an.vo.UserAdminVO;
import com.an.vo.UserLoginVO;
import com.an.vo.UserRegisterVO;
import com.an.vo.UserStatsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/user")
@Api(tags = "顾客端用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    public Result<List<UserAdminVO>> getUserList() {
        try {
            log.info("获取用户列表");
            List<User> users = userService.getAllUsers();

            List<UserAdminVO> userVOs = users.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());

            return Result.success(userVOs);
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage());
            return Result.error("获取用户列表失败");
        }
    }

    /**
     * 转换 User 到 UserAdminVO
     */
    private UserAdminVO convertToVO(User user) {
        return UserAdminVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .lastLogoutTime(user.getLastLogoutTime())
                .status(determineUserStatus(user))
                .build();
    }

    /**
     * 根据用户最后活动时间确定状态
     */
    private String determineUserStatus(User user) {
        // 这里可以根据业务逻辑判断用户状态
        // 例如：30天内登录过为活跃，否则为不活跃
        if (user.getLastLogin() != null) {
            // 简单的状态判断逻辑，可以根据实际需求调整
            return "active"; // 暂时都返回活跃
        }
        return "inactive";
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    @ApiOperation("搜索用户")
    public Result<List<UserAdminVO>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        try {
            log.info("搜索用户, keyword: {}, role: {}", keyword, role);

            List<User> users = userService.searchUsers(keyword, role);
            List<UserAdminVO> userVOs = users.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());

            return Result.success(userVOs);
        } catch (Exception e) {
            log.error("搜索用户失败: {}", e.getMessage());
            return Result.error("搜索用户失败");
        }
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{userId}")
    @ApiOperation("根据ID获取用户详情")
    public Result<UserAdminVO> getUserById(@PathVariable Long userId) {
        try {
            log.info("获取用户详情, userId: {}", userId);

            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            UserAdminVO userVO = convertToVO(user);
            return Result.success(userVO);
        } catch (Exception e) {
            log.error("获取用户详情失败: {}", e.getMessage());
            return Result.error("获取用户详情失败");
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    @ApiOperation("获取用户统计信息")
    public Result<UserStatsVO> getUserStats() {
        try {
            log.info("获取用户统计信息");

            // 这里需要实现统计逻辑
            // 暂时返回模拟数据
            UserStatsVO stats = UserStatsVO.builder()
                    .total(0L)
                    .active(0L)
                    .newToday(0L)
                    .build();

            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户统计信息失败: {}", e.getMessage());
            return Result.error("获取用户统计信息失败");
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @ApiOperation("删除用户")
    public Result deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            log.info("删除用户, userId: {}", userId);

            // 获取当前登录用户ID
            String token = request.getHeader("Authorization");
            Long currentUserId = BaseContext.getCurrentId();

            if (currentUserId == null) {
                return Result.error("无法获取当前用户信息");
            }

            // 检查要删除的用户是否存在
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 防止删除自己
            if (currentUserId.equals(userId)) {
                return Result.error("不能删除当前登录的用户");
            }

            // 执行删除
            userService.deleteUser(userId);

            return Result.success("用户删除成功");
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage());
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }


    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {

        log.info("用户登录：{}", userLoginDTO);

        // 根据用户名/手机号 + 密码校验
        User user = userService.login(userLoginDTO);
        if (user == null) {
            return Result.error("账号或密码错误");
        }

        // 生成 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());

        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );

        UserLoginVO vo = UserLoginVO.builder()
                .id(user.getId())
                .token(token)
                .openid(null)
                .role(user.getRole())
                .username(user.getUsername())  // 添加用户名返回
                .build();

        return Result.success(vo);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<UserRegisterVO> register(@RequestBody @Validated UserRegisterDTO dto) {

        log.info("用户注册：{}", dto);

        User user = userService.register(dto);

        UserRegisterVO vo = UserRegisterVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .message("注册成功，请登录")
                .build();

        return Result.success(vo);
    }

    /**
     * 用户注销（无需 Spring Security）
     * 逻辑：
     * 1. 从请求头获取 token
     * 2. 解析 userId
     * 3. 更新 last_logout_time
     */
    @PostMapping("/logout")
    @ApiOperation("用户注销")
    public Result<Void> logout(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        // 允许“直接传 token” 或 “Bearer xxx”
        if (token == null || token.trim().isEmpty()) {
            return Result.error("注销失败：未登录");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 从 token 提取 userId
        Integer userId = JwtUtil.getUserIdFromToken(token, jwtProperties.getUserSecretKey());
        if (userId == null) {
            return Result.error("注销失败：无效的 token");
        }

        // 更新 last_logout_time（数据库字段）
        userService.updateLastLogoutTime(userId);

        log.info("用户注销成功，userId = {}", userId);
        return Result.success();
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    @ApiOperation("更新用户信息")
    public Result updateUser(@PathVariable Long userId, @RequestBody UserUpdateDTO dto) {
        try {
            log.info("更新用户信息, userId: {}", userId);

            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 更新用户信息
            userService.updateUser(userId, dto);

            return Result.success("用户信息更新成功");
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage());
            return Result.error("更新用户信息失败");
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    @ApiOperation("更新用户状态")
    public Result updateUserStatus(@PathVariable Long userId, @RequestParam String status) {
        try {
            log.info("更新用户状态, userId: {}, status: {}", userId, status);

            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 更新用户状态
            userService.updateUserStatus(userId, status);

            return Result.success("用户状态更新成功");
        } catch (Exception e) {
            log.error("更新用户状态失败: {}", e.getMessage());
            return Result.error("更新用户状态失败");
        }
    }



}
