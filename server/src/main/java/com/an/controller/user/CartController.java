package com.an.controller.user;

import com.an.context.BaseContext;
import com.an.entity.ShoppingCart;
import com.an.result.Result;
import com.an.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/cart")
@Api(tags = "C端-购物车接口")
public class CartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public Result addToCart(@RequestBody ShoppingCart cart) {
        Long userId = BaseContext.getCurrentId(); // 取当前登录用户
        cart.setUserId(userId);

        shoppingCartService.addToCart(cart);
        return Result.success();
    }

    /**
     * 查询购物车
     */
    @GetMapping("/list")
    @ApiOperation("查询购物车列表")
    public Result<List<ShoppingCart>> list() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> list = shoppingCartService.list(userId);

        return Result.success(list);
    }

    /**
     * 购物车删除商品
     */
    @DeleteMapping("/delete/{productId}")
    @ApiOperation("从购物车删除商品")
    public Result delete(@PathVariable Long productId) {
        Long userId = BaseContext.getCurrentId();
        shoppingCartService.delete(userId, productId);

        return Result.success();
    }
}
