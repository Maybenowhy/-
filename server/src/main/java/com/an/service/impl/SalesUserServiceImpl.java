package com.an.service.impl;

import com.an.entity.User;
import com.an.mapper.UserMapper;
import com.an.result.PageResult;
import com.an.service.SalesUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesUserServiceImpl implements SalesUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult pageQuery(Integer page, Integer pageSize, String username) {
        PageHelper.startPage(page, pageSize);
        List<User> list = userMapper.list(username);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @Override
    public List<User> search(String usernameOrEmail) {
        return userMapper.search(usernameOrEmail);
    }
}
