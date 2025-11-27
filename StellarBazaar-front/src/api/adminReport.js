// src/api/adminReport.js
import request from "./request";

// 销售总览
export function getSalesSummary(begin, end) {
  return request.get("/admin/report/summary", { 
    params: { begin, end } 
  });
}

// 日销售额统计
export function getDailySales(begin, end) {
  return request.get("/admin/report/daily", { 
    params: { begin, end } 
  });
}

// 销售Top商品
export function getTopSales(limit = 10) {
  return request.get("/admin/report/top", { 
    params: { limit } 
  });
}

export default {
  getSalesSummary,
  getDailySales,
  getTopSales
};