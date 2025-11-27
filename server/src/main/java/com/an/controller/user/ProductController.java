package com.an.controller.user;

import com.an.entity.Product;
import com.an.result.PageResult;
import com.an.result.Result;
import com.an.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/product")
@Api(tags = "C端-商品浏览接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 查询所有商品列表
     */
    @GetMapping("/list")
    @ApiOperation("查询所有商品列表")
    public Result<List<Product>> list() {
        Product product = new Product();
        product.setIsActive(1); // 只展示上架商品

        List<Product> list = productService.list(product);
        return Result.success(list);
    }

    /**
     * 根据商品ID查询商品详情
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询商品详情")
    public Result<Product> getProduct(@PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(product);
    }

    /**
     * 分页查询商品列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询商品列表")
    public Result<PageResult> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize) {

        Product product = new Product();
        product.setIsActive(1); // 只展示上架商品

        PageResult pageResult = productService.pageQuery(page, pageSize, product);
        return Result.success(pageResult);
    }
}
