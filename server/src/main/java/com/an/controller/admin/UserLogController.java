package com.an.controller.admin;

import com.an.entity.UserLog;
import com.an.result.Result;
import com.an.service.UserLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales/user-log")
@Api(tags = "销售端-客户行为日志")
public class UserLogController {

    @Autowired
    private UserLogService userLogService;

    @GetMapping("/list")
    @ApiOperation("查询客户行为日志")
    public Result<List<UserLog>> list(@RequestParam(required = false) Long userId,
                                      @RequestParam(required = false) String actionType,
                                      @RequestParam(required = false) String startTime,
                                      @RequestParam(required = false) String endTime) {
        List<UserLog> logs = userLogService.list(userId, actionType, startTime, endTime);
        return Result.success(logs);
    }

    @GetMapping("/recent")
    @ApiOperation("查询某客户最近 N 条日志")
    public Result<List<UserLog>> recent(@RequestParam Long userId,
                                        @RequestParam Integer limit) {
        List<UserLog> logs = userLogService.recent(userId, limit);
        return Result.success(logs);
    }

    @PostMapping("/add")
    @ApiOperation("添加用户行为日志")
    public Result<Void> addLog(@RequestBody UserLog userLog) {
        userLogService.addLog(userLog);
        return Result.success();
    }

}
