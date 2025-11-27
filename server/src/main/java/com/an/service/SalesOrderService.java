package com.an.service;

import com.an.entity.Order;
import com.an.result.PageResult;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/16 23:23
 * @description:
 */
public interface SalesOrderService {

    PageResult pageQuery(Integer page, Integer pageSize, Order condition);

    Order getById(Long id);

    void update(Order order);

    void deleteById(Long id);
}
