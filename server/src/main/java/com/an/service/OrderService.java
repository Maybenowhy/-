package com.an.service;

import com.an.entity.Order;
import com.an.vo.OrderSubmitVO;
import com.an.vo.OrderVO;

import java.util.List;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 21:27
 * @description:
 */
public interface OrderService {

    OrderVO submitOrder(OrderSubmitVO submitVO);

    void payOrder(Long orderId);

    void confirmOrder(Long orderId);

    /**
     * 查询用户历史订单
     */
    List<Order> listByUser(Long userId);

    /**
     * 查询订单详情（含订单项）
     */
    Order getById(Long orderId);

}

