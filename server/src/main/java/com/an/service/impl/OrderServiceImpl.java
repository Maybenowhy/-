package com.an.service.impl;

import com.an.context.BaseContext;
import com.an.entity.Order;
import com.an.entity.OrderItem;
import com.an.entity.Product;
import com.an.entity.ShoppingCart;
import com.an.exception.BusinessException;
import com.an.mapper.OrderItemMapper;
import com.an.mapper.OrderMapper;
import com.an.mapper.ProductMapper;
import com.an.mapper.ShoppingCartMapper;
import com.an.service.MailService;
import com.an.service.OrderService;
import com.an.vo.CartItemVO;
import com.an.vo.OrderSubmitVO;
import com.an.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private MailService mailService;

    /**
     * 提交订单核心方法
     * 使用@Transactional注解确保整个操作在事务中执行
     * 如果任何步骤失败，整个操作将回滚，保证数据一致性
     */
    @Override
    @Transactional
    public OrderVO submitOrder(OrderSubmitVO submitVO) {

        Long userId = BaseContext.getCurrentId();

        // 1. 使用前端传递的购物车数据
        List<CartItemVO> cartItems = submitVO.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException("购物车为空，无法下单");
        }

        log.info("用户 {} 提交订单，商品数量: {}", userId, cartItems.size());

        // 2. 统计总金额并验证商品
        double totalAmount = 0;
        for (CartItemVO cartItem : cartItems) {
            Product product = productMapper.getById(cartItem.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在: " + cartItem.getProductId());
            }
            if (product.getIsActive() != 1) {
                throw new BusinessException("商品已下架: " + product.getName());
            }
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessException("库存不足: " + product.getName() +
                        "，库存: " + product.getStockQuantity() + "，需求: " + cartItem.getQuantity());
            }
            totalAmount += product.getPrice() * cartItem.getQuantity();
        }

        // 3. 创建订单
        String orderNumber = generateOrderNumber();

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUserId(userId);
        order.setStatus("pending_payment");
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(submitVO.getShippingAddress());
        order.setShippingPhone(submitVO.getShippingPhone());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.insert(order);

        // 4. 创建订单明细，扣库存
        for (CartItemVO cartItem : cartItems) {
            Product product = productMapper.getById(cartItem.getProductId());

            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice() * cartItem.getQuantity());
            item.setCreatedAt(LocalDateTime.now());

            orderItemMapper.insert(item);

            // 扣库存
            productMapper.decreaseStock(product.getId(), cartItem.getQuantity());
        }

//         5. 注意：这里不需要清空数据库购物车，因为前端使用本地存储
//         shoppingCartMapper.clear(userId);

        // 返回结果
        OrderVO vo = new OrderVO();
        vo.setOrderId(order.getId());
        vo.setOrderNumber(order.getOrderNumber());
        vo.setTotalAmount(totalAmount);

        log.info("订单创建成功，订单号: {}", orderNumber);
        return vo;
    }

    /**
     * 订单支付
     */
    @Override
    public void payOrder(Long orderId) {

        Order order = orderMapper.getById(orderId);

        if (order == null) throw new BusinessException("订单不存在");

        if (!order.getStatus().equals("pending_payment")) {
            throw new BusinessException("订单无法支付");
        }

        order.setStatus("paid");
        order.setPaymentTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.update(order);
    }

    /**
     * 确认收货
     */
    @Override
    public void confirmOrder(Long orderId) {
        Order order = orderMapper.getById(orderId);
        if (order == null)
            throw new BusinessException("订单不存在");

        if (!order.getStatus().equals("paid"))
            throw new BusinessException("订单未支付，无法收货");

        order.setStatus("delivered");
        order.setDeliveryTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.update(order);

        // 移除邮件发送代码，仅记录日志
        log.info("用户 {} 确认收货订单 {}", order.getUserId(), order.getOrderNumber());
    }

    private String generateOrderNumber() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + (int)(Math.random() * 1000);
    }

    @Override
    public List<Order> listByUser(Long userId) {
        return orderMapper.listByUserId(userId);
    }

    @Override
    public Order getById(Long orderId) {
        Order order = orderMapper.getById(orderId);
        if(order != null) {
            List<OrderItem> items = orderItemMapper.listByOrderId(orderId);

            // 为每个订单项设置商品信息
            for(OrderItem item : items) {
                if(item.getProductId() != null) {
                    Product product = productMapper.getById(item.getProductId());
                    if(product != null) {
                        item.setProductName(product.getName());
                        item.setProductImage(product.getImageUrl());
                    }
                }
            }

            order.setItems(items); // 把订单项设置进去
        }
        return order;
    }

}
