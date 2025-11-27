package com.an.controller.admin;

import com.an.entity.Product;
import com.an.result.PageResult;
import com.an.result.Result;
import com.an.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
@Api(tags = "后台-商品管理接口")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    /**
     * 分页查询 + 模糊搜索
     */
    @GetMapping("/page")
    @ApiOperation("分页查询商品（可搜索）")
    public Result<PageResult> pageQuery(@RequestParam Integer page,
                                        @RequestParam Integer pageSize,
                                        Product condition) {
        PageResult pageResult = productService.pageQuery(page, pageSize, condition);
        return Result.success(pageResult);
    }

    /**
     * 新增商品
     */
    @PostMapping
    @ApiOperation("新增商品")
    public Result add(@RequestBody Product product) {
        productService.add(product);
        return Result.success();
    }

    /**
     * 修改商品
     */
    @PutMapping
    @ApiOperation("修改商品")
    public Result update(@RequestBody Product product) {
        productService.update(product);
        return Result.success();
    }

    /**
     * 删除（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除商品（物理删除）")
    public Result delete(@PathVariable Long id) {
        productService.softDelete(id);
        return Result.success();
    }

    /**
     * 批量上下架
     */
    @PostMapping("/status")
    @ApiOperation("批量上下架商品")
    public Result batchUpdateStatus(@RequestParam List<Long> ids,
                                    @RequestParam Integer isActive) {
        productService.batchUpdateActive(ids, isActive);
        return Result.success();
    }

    /**
     * 修改库存
     */
    @PostMapping("/stock")
    @ApiOperation("修改商品库存")
    public Result updateStock(@RequestParam Long id,
                              @RequestParam Integer stockQuantity) {
        productService.updateStock(id, stockQuantity);
        return Result.success();
    }
}
