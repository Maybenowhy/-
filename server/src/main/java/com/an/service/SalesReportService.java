package com.an.service;

import com.an.vo.DailySalesVO;
import com.an.vo.TopProductVO;
import com.an.vo.SalesSummaryVO;

import java.util.List;

public interface SalesReportService {

    List<DailySalesVO> dailySales(String begin, String end);

    List<TopProductVO> topSales(Integer limit);

    SalesSummaryVO summary(String begin, String end);
}
