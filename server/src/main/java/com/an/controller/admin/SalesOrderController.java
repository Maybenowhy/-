package com.an.controller.admin;

import com.an.entity.Order;
import com.an.result.Result;
import com.an.service.SalesOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Api(tags = "B端-订单管理")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    /**
     * 分页查询订单（后台管理）
     */
    @GetMapping("/page")
    @ApiOperation("分页查询订单")
    public Result page(@RequestParam Integer page,
                       @RequestParam Integer pageSize,
                       Order condition) {
        return Result.success(salesOrderService.pageQuery(page, pageSize, condition));
    }

    /**
     * 修改订单（发货/修改状态等）
     */
    @PutMapping
    @ApiOperation("修改订单信息")
    public Result update(@RequestBody Order order) {
        salesOrderService.update(order);
        return Result.success();
    }

    /**
     * 根据ID查询订单详情
     */
    @GetMapping("/{id}")
    @ApiOperation("查询订单详情")
    public Result getById(@PathVariable Long id) {
        return Result.success(salesOrderService.getById(id));
    }

    /**
     * 删除订单（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除订单")
    public Result delete(@PathVariable Long id) {
        salesOrderService.deleteById(id);
        return Result.success();
    }
}
