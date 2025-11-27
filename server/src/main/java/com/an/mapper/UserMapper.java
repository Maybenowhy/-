package com.an.mapper;

import com.an.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名或邮箱查询用户
     */
    User selectByUsernameOrEmail(@Param("account") String account);

    /**
     * 更新用户最后登录时间
     */
    int updateLastLogin(@Param("userId") Long userId,
                        @Param("lastLogin") LocalDateTime lastLogin);

    /**
     * 更新用户最后注销时间
     */
    int updateLastLogoutTime(@Param("userId") Integer userId,
                             @Param("lastLogoutTime") LocalDateTime lastLogoutTime);

    /**
     * 插入用户
     */
    int insert(User user);

    List<User> list(@Param("username") String username);

    User getById(@Param("id") Integer id);

    List<User> search(@Param("usernameOrEmail") String usernameOrEmail);

    // 添加删除方法
    int delete(@Param("id") Long id);

    /**
     * 更新用户信息
     */
    int updateUser(User user);

    /**
     * 更新用户状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

}