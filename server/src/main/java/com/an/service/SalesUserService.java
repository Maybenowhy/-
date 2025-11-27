package com.an.service;

import com.an.entity.User;
import com.an.result.PageResult;

import java.util.List;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/16 23:54
 * @description:
 */
public interface SalesUserService {

    /**
     * 分页查询用户
     */
    PageResult pageQuery(Integer page, Integer pageSize, String username);

    /**
     * 获取单个用户详情
     */
    User getById(Integer id);

    /**
     * 搜索用户列表（可选条件模糊搜索）
     */
    List<User> search(String usernameOrEmail);
}

