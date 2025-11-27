package com.an.service.impl;

import com.an.entity.Order;
import com.an.mapper.OrderMapper;
import com.an.result.PageResult;
import com.an.service.SalesOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SalesOrderServiceImpl implements SalesOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public PageResult pageQuery(Integer page, Integer pageSize, Order condition) {
        PageHelper.startPage(page, pageSize);

        // 使用同一个 list 方法即可（后台不区分用户过滤）
        var list = orderMapper.adminList(condition);

        log.info(":{}",list);

        PageInfo<Order> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Order getById(Long id) {
        return orderMapper.getFullById(id); // 含订单项
    }

    @Override
    public void update(Order order) {
        orderMapper.update(order);
    }

    @Override
    public void deleteById(Long id) {
        orderMapper.deleteById(id); // 逻辑删除
    }
}
