package com.an.controller.admin;

import com.an.entity.User;
import com.an.result.PageResult;
import com.an.result.Result;
import com.an.service.SalesUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales/user")
@Api(tags = "销售端-用户管理模块")
public class SalesUserController {

    @Autowired
    private SalesUserService salesUserService;

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    @ApiOperation("分页查询用户")
    public Result<PageResult> pageQuery(@RequestParam Integer page,
                                        @RequestParam Integer pageSize,
                                        @RequestParam(required = false) String username) {
        PageResult pageResult = salesUserService.pageQuery(page, pageSize, username);
        return Result.success(pageResult);
    }

    /**
     * 获取单个用户详情
     */
    @GetMapping("/{id}")
    @ApiOperation("获取用户详情")
    public Result<User> getById(@PathVariable Integer id) {
        User user = salesUserService.getById(id);
        return Result.success(user);
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    @ApiOperation("模糊搜索用户")
    public Result<List<User>> search(@RequestParam String usernameOrEmail) {
        List<User> users = salesUserService.search(usernameOrEmail);
        return Result.success(users);
    }
}
