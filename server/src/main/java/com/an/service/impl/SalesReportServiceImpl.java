package com.an.service.impl;

import com.an.mapper.OrderMapper;
import com.an.mapper.OrderItemMapper;
import com.an.service.SalesReportService;
import com.an.vo.DailySalesVO;
import com.an.vo.TopProductVO;
import com.an.vo.SalesSummaryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SalesReportServiceImpl implements SalesReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public List<DailySalesVO> dailySales(String begin, String end) {
        log.info("开始日销售额统计：{}，{}", begin, end);
        // 为日期添加时间部分
        String beginTime = begin + " 00:00:00";
        String endTime = end + " 23:59:59";
        return orderMapper.dailySales(beginTime, endTime);
    }

    @Override
    public List<TopProductVO> topSales(Integer limit) {
        return orderItemMapper.topSales(limit);
    }

    @Override
    public SalesSummaryVO summary(String begin, String end) {
        // 为日期添加时间部分
        String beginTime = begin + " 00:00:00";
        String endTime = end + " 23:59:59";
        return orderMapper.summary(beginTime, endTime);
    }
}
