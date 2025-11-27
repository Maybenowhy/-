package com.an.controller.user;

import com.an.context.BaseContext;
import com.an.entity.Order;
import com.an.exception.BusinessException;
import com.an.result.Result;
import com.an.service.OrderService;
import com.an.vo.OrderSubmitVO;
import com.an.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/order")
@Api(tags = "C端-订单模块")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     */
    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public Result<OrderVO> submit(@RequestBody OrderSubmitVO submitVO) {
        OrderVO orderVO = orderService.submitOrder(submitVO);
        return Result.success(orderVO);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderId}")
    @ApiOperation("订单支付")
    public Result pay(@PathVariable Long orderId) {
        try {
            orderService.payOrder(orderId);
            return Result.success("支付成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("支付订单异常: {}", e.getMessage());
            return Result.error("支付失败，请稍后重试");
        }
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirm/{orderId}")
    @ApiOperation("确认收货")
    public Result confirm(@PathVariable Long orderId) {
        orderService.confirmOrder(orderId);
        return Result.success();
    }

    /**
     * 查询用户所有历史订单
     */
    @GetMapping("/list")
    @ApiOperation("查询用户历史订单")
    public Result<List<Order>> list() {
        Long userId = BaseContext.getCurrentId(); // 从token中获取用户ID
        List<Order> orders = orderService.listByUser(userId);
        return Result.success(orders);
    }

    /**
     * 查询订单详情（含订单项）
     */
    @GetMapping("/{orderId}")
    @ApiOperation("查询订单详情")
    public Result<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        return Result.success(order);
    }

}
