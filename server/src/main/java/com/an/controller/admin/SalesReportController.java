package com.an.controller.admin;

import com.an.result.Result;
import com.an.service.SalesReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "B端-销售统计报表")
@Slf4j
public class SalesReportController {

    @Autowired
    private SalesReportService salesReportService;

    /**
     * 日销售额统计
     */
    @GetMapping("/daily")
    @ApiOperation("日销售额统计")
    public Result daily(@RequestParam String begin,
                        @RequestParam String end) {
        return Result.success(salesReportService.dailySales(begin, end));
    }

    /**
     * 销售Top商品
     */
    @GetMapping("/top")
    @ApiOperation("销售Top商品")
    public Result top(@RequestParam Integer limit) {
        return Result.success(salesReportService.topSales(limit));
    }

    /**
     * 销售总览（销售额、订单数、客单价等）
     */
    @GetMapping("/summary")
    @ApiOperation("销售概览统计")
    public Result summary(@RequestParam String begin,
                          @RequestParam String end) {
        log.info("{},{}",begin,end);
        return Result.success(salesReportService.summary(begin, end));
    }
}
