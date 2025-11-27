<template>
  <div class="admin-page">
    <!-- 顶部导航 -->
    <div class="admin-header">
      <h1>销售统计报表</h1>
      <div class="header-actions">
        <div class="date-range-picker">
          <div class="date-input-group">
            <label>开始日期：</label>
            <input 
              type="date" 
              v-model="startDate" 
              class="date-input"
            />
          </div>
          <div class="date-input-group">
            <label>结束日期：</label>
            <input 
              type="date" 
              v-model="endDate" 
              class="date-input"
            />
          </div>
          <button class="btn btn-primary" @click="loadReportData">
            <i class="icon-refresh"></i>
            查询
          </button>
        </div>
      </div>
    </div>

    <!-- 销售总览 -->
    <div class="stats-overview">
      <h2 class="section-title">销售总览</h2>
      <div class="stats-grid">
        <div class="stat-card large">
          <div class="stat-icon total-revenue"></div>
          <div class="stat-info">
            <h3>¥{{ formatNumber(salesSummary.totalSales || 0) }}</h3>
            <p>总销售额</p>
            <div class="stat-trend" v-if="salesSummary.revenueGrowth">
              <span :class="salesSummary.revenueGrowth >= 0 ? 'positive' : 'negative'">
                {{ salesSummary.revenueGrowth >= 0 ? '↑' : '↓' }} 
                {{ Math.abs(salesSummary.revenueGrowth) }}%
              </span>
              较上月
            </div>
          </div>
        </div>
        <div class="stat-card large">
          <div class="stat-icon total-orders"></div>
          <div class="stat-info">
            <h3>{{ formatNumber(salesSummary.totalOrders || 0) }}</h3>
            <p>总订单数</p>
            <div class="stat-trend" v-if="salesSummary.orderGrowth">
              <span :class="salesSummary.orderGrowth >= 0 ? 'positive' : 'negative'">
                {{ salesSummary.orderGrowth >= 0 ? '↑' : '↓' }} 
                {{ Math.abs(salesSummary.orderGrowth) }}%
              </span>
              较上月
            </div>
          </div>
        </div>
        <div class="stat-card large">
          <div class="stat-icon avg-order"></div>
          <div class="stat-info">
            <h3>¥{{ formatNumber(salesSummary.avgOrderAmount || 0) }}</h3>
            <p>客单价</p>
            <div class="stat-trend" v-if="salesSummary.avgOrderGrowth">
              <span :class="salesSummary.avgOrderGrowth >= 0 ? 'positive' : 'negative'">
                {{ salesSummary.avgOrderGrowth >= 0 ? '↑' : '↓' }} 
                {{ Math.abs(salesSummary.avgOrderGrowth) }}%
              </span>
              较上月
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表和数据表格 -->
    <div class="charts-container">
      <div class="chart-row">
        <!-- 日销售额图表 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>日销售额趋势</h3>
          </div>
          <div class="chart-content">
            <div v-if="dailySales.length > 0" class="sales-chart">
              <div class="chart-bars">
                <div 
                  v-for="day in dailySales" 
                  :key="day.date"
                  class="chart-bar-container"
                >
                  <div class="chart-bar-label">{{ formatChartDate(day.date) }}</div>
                  <div class="chart-bar">
                    <div 
                      class="bar-fill" 
                      :style="{ height: calculateBarHeight(day.totalAmount) + '%' }"
                    ></div>
                  </div>
                  <div class="chart-bar-value">¥{{ formatNumber(day.totalAmount) }}</div>
                </div>
              </div>
            </div>
            <div v-else class="no-data">
              <div class="no-data-icon">📊</div>
              <p>暂无数据</p>
            </div>
          </div>
        </div>

        <!-- Top商品表格 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>热销商品TOP 10</h3>
            <div class="chart-actions">
              <select v-model="topLimit" class="limit-select" @change="loadTopSales">
                <option value="5">TOP 5</option>
                <option value="10">TOP 10</option>
                <option value="20">TOP 20</option>
              </select>
            </div>
          </div>
          <div class="chart-content">
            <div v-if="topSales.length > 0" class="top-products">
              <table class="top-products-table">
                <thead>
                  <tr>
                    <th width="40">排名</th>
                    <th>商品名称</th>
                    <th width="100">销售额</th>
                    <th width="80">销量</th>
                    <th width="100">占比</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(product, index) in topSales" :key="product.id">
                    <td class="rank">
                      <span :class="getRankClass(index + 1)">{{ index + 1 }}</span>
                    </td>
                    <td class="product-name">
                      <div class="product-info">
                        <div class="product-image" v-if="product.image">
                          <img :src="product.image" :alt="product.name" />
                        </div>
                        <div class="product-details">
                          <div class="name">{{ product.name }}</div>
                          <div class="category" v-if="product.category">{{ product.category }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="revenue">¥{{ formatNumber(product.revenue) }}</td>
                    <td class="quantity">{{ formatNumber(product.quantity) }}</td>
                    <td class="percentage">
                      <div class="percentage-bar">
                        <div 
                          class="percentage-fill" 
                          :style="{ width: product.percentage + '%' }"
                        ></div>
                        <span class="percentage-text">{{ product.percentage }}%</span>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div v-else class="no-data">
              <div class="no-data-icon">🏆</div>
              <p>暂无数据</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-content">
        <div class="loading-spinner"></div>
        <p>数据加载中...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { 
  getSalesSummary, 
  getDailySales, 
  getTopSales 
} from '@/api/adminReport';

// 响应式数据
const salesSummary = ref({});
const dailySales = ref([]);
const topSales = ref([]);
const startDate = ref('');
const endDate = ref('');
const topLimit = ref(10);
const loading = ref(false);

// 方法
const loadReportData = async () => {
  try {
    loading.value = true;
    
    // 加载销售总览
    const summaryResponse = await getSalesSummary(startDate.value, endDate.value);
    console.log('销售总览API响应:', summaryResponse); // 调试日志
    
    if (summaryResponse) {
      salesSummary.value = summaryResponse || {};
    } else {
      console.warn('销售总览接口返回异常:', summaryResponse);
      // 使用模拟数据
      salesSummary.value = {
        totalRevenue: 451200,
        totalOrders: 1248,
        avgOrderValue: 361.5,
        revenueGrowth: 12.5,
        orderGrowth: 8.3,
        avgOrderGrowth: 4.1
      };
    }
    
    // 加载日销售额 - 使用真实数据
    try {
      console.log('请求日销售额参数:', { begin: startDate.value, end: endDate.value });
      const dailyResponse = await getDailySales(startDate.value, endDate.value);
      console.log('日销售额API响应:', dailyResponse); // 调试日志
      
      if (dailyResponse) {
        // 使用后端返回的数据
        dailySales.value = dailyResponse || [];
        console.log('日销售额数据:', dailySales.value); // 调试日志
      } else {
        console.warn('日销售额接口返回异常:', dailyResponse);
        useMockDailyData();
      }
    } catch (dailyError) {
      console.error('日销售额接口调用失败:', dailyError);
      useMockDailyData();
    }
    
    // 加载Top商品
    await loadTopSales();
    
  } catch (error) {
    console.error('加载报表数据失败:', error);
    // 使用模拟数据作为备选
    useMockData();
  } finally {
    loading.value = false;
  }
};

const loadTopSales = async () => {
  try {
    const response = await getTopSales(topLimit.value);
    if (response) {
      // 数据转换：将后端返回的数据格式转换为前端需要的格式
      topSales.value = response.map(item => ({
        id: item.productId,
        name: item.productName,
        category: '', // 如果没有分类信息，可以留空或从其他字段获取
        revenue: item.totalRevenue || 0, // 如果后端没有返回revenue，这里设为0
        quantity: item.totalQuantity || 0, // 处理null值
        percentage: 0 // 需要计算占比
      }));
      
      // 计算总销量用于计算占比
      const totalQuantity = topSales.value.reduce((sum, item) => sum + item.quantity, 0);
      
      // 计算每个商品的占比
      topSales.value = topSales.value.map(item => ({
        ...item,
        percentage: totalQuantity > 0 ? Math.round((item.quantity / totalQuantity) * 100) : 0
      }));
    }
  } catch (error) {
    console.error('加载Top商品失败:', error);
    // 使用模拟数据作为备选
    topSales.value = [
      { id: 1, name: '高端智能手机', category: '电子产品', revenue: 128560, quantity: 24, percentage: 28.5 },
      { id: 2, name: '无线蓝牙耳机', category: '电子产品', revenue: 89500, quantity: 98, percentage: 19.8 },
      { id: 3, name: '智能手表', category: '电子产品', revenue: 67200, quantity: 56, percentage: 14.9 },
      { id: 4, name: '笔记本电脑', category: '电子产品', revenue: 54300, quantity: 18, percentage: 12.1 },
      { id: 5, name: '平板电脑', category: '电子产品', revenue: 38900, quantity: 32, percentage: 8.6 },
    ];
  }
};

const useMockData = () => {
  // 模拟销售总览数据
  salesSummary.value = {
    totalRevenue: 451200,
    totalOrders: 1248,
    avgOrderValue: 361.5,
    revenueGrowth: 12.5,
    orderGrowth: 8.3,
    avgOrderGrowth: 4.1
  };
  
  useMockDailyData();
};

const useMockDailyData = () => {
  // 模拟日销售额数据 - 使用后端字段名 totalAmount
  dailySales.value = [
    { date: '2024-01-01', totalAmount: 12800 },
    { date: '2024-01-02', totalAmount: 15200 },
    { date: '2024-01-03', totalAmount: 9800 },
    { date: '2024-01-04', totalAmount: 16500 },
    { date: '2024-01-05', totalAmount: 14200 },
    { date: '2024-01-06', totalAmount: 18900 },
    { date: '2024-01-07', totalAmount: 21000 },
    { date: '2024-01-08', totalAmount: 17600 },
    { date: '2024-01-09', totalAmount: 15800 },
    { date: '2024-01-10', totalAmount: 19200 },
  ];
};

const formatNumber = (num) => {
  if (num === null || num === undefined) return '0';
  return new Intl.NumberFormat('zh-CN').format(num);
};

const formatChartDate = (dateString) => {
  if (!dateString) return '';
  try {
    const date = new Date(dateString);
    return `${date.getMonth() + 1}/${date.getDate()}`;
  } catch (error) {
    return dateString;
  }
};

const calculateBarHeight = (amount) => {
  if (!dailySales.value.length) return 0;
  const maxAmount = Math.max(...dailySales.value.map(day => day.totalAmount || 0));
  return maxAmount > 0 ? ((amount || 0) / maxAmount) * 100 : 0;
};

const getRankClass = (rank) => {
  if (rank === 1) return 'rank-1';
  if (rank === 2) return 'rank-2';
  if (rank === 3) return 'rank-3';
  return 'rank-other';
};

// 设置默认日期范围（最近30天）
const setDefaultDateRange = () => {
  const end = new Date();
  const start = new Date();
  start.setDate(start.getDate() - 30);
  
  endDate.value = end.toISOString().split('T')[0];
  startDate.value = start.toISOString().split('T')[0];
};

onMounted(() => {
  setDefaultDateRange();
  loadReportData();
});
</script>

<style scoped>
.admin-page {
  padding: 24px;
  background: #f8f9fa;
  min-height: 100vh;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.admin-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.date-range-picker {
  display: flex;
  gap: 16px;
  align-items: center;
}

.date-input-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-input-group label {
  font-size: 14px;
  color: #495057;
  font-weight: 500;
}

.date-input {
  padding: 8px 12px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.date-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

/* 销售总览 */
.stats-overview {
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card.large {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card.large:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.total-revenue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.total-orders {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.avg-order {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info h3 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 4px 0;
  color: #1a1a1a;
}

.stat-info p {
  margin: 0 0 8px 0;
  color: #6c757d;
  font-size: 14px;
}

.stat-trend {
  font-size: 12px;
  color: #6c757d;
}

.stat-trend span {
  font-weight: 600;
  margin-right: 4px;
}

.stat-trend .positive {
  color: #28a745;
}

.stat-trend .negative {
  color: #dc3545;
}

/* 图表容器 */
.charts-container {
  margin-bottom: 24px;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
}

.chart-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.chart-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.limit-select {
  padding: 6px 12px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  font-size: 14px;
  background: white;
}

.chart-content {
  padding: 24px;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 日销售额图表 */
.sales-chart {
  width: 100%;
  height: 100%;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 200px;
  gap: 8px;
}

.chart-bar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  gap: 8px;
}

.chart-bar-label {
  font-size: 12px;
  color: #6c757d;
  text-align: center;
}

.chart-bar {
  width: 100%;
  height: 150px;
  background: #f8f9fa;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.bar-fill {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, #3498db, #67b26f);
  border-radius: 4px 4px 0 0;
  transition: height 0.3s ease;
}

.chart-bar-value {
  font-size: 12px;
  font-weight: 600;
  color: #495057;
}

/* Top商品表格 */
.top-products {
  width: 100%;
}

.top-products-table {
  width: 100%;
  border-collapse: collapse;
}

.top-products-table th {
  background: #f8f9fa;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.top-products-table td {
  padding: 12px;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.rank {
  text-align: center;
}

.rank span {
  display: inline-block;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  text-align: center;
  line-height: 24px;
  font-weight: 600;
  font-size: 12px;
}

.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #856404;
}

.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #e2e3e5);
  color: #383d41;
}

.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #e9a35f);
  color: #721c24;
}

.rank-other {
  background: #f8f9fa;
  color: #6c757d;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details .name {
  font-weight: 500;
  margin-bottom: 2px;
}

.product-details .category {
  font-size: 12px;
  color: #6c757d;
}

.revenue {
  font-weight: 600;
  color: #e74c3c;
}

.quantity {
  text-align: center;
  font-weight: 500;
}

.percentage {
  width: 120px;
}

.percentage-bar {
  position: relative;
  height: 24px;
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
}

.percentage-fill {
  height: 100%;
  background: linear-gradient(90deg, #3498db, #67b26f);
  border-radius: 12px;
  transition: width 0.3s ease;
}

.percentage-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 11px;
  font-weight: 600;
  color: #495057;
}

/* 无数据状态 */
.no-data {
  text-align: center;
  color: #6c757d;
}

.no-data-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.5;
}

.no-data p {
  margin: 0;
  font-size: 14px;
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.loading-content {
  background: white;
  padding: 32px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-content p {
  margin: 0;
  color: #495057;
  font-size: 14px;
}

/* 按钮样式 */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 图标样式 */
.icon-refresh::before { content: "🔄"; }

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chart-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .admin-page {
    padding: 16px;
  }
  
  .admin-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .date-range-picker {
    flex-direction: column;
    align-items: stretch;
  }
  
  .date-input-group {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .chart-actions {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .chart-bars {
    gap: 4px;
  }
  
  .chart-bar-label {
    font-size: 10px;
  }
  
  .chart-bar-value {
    font-size: 10px;
  }
  
  .top-products-table {
    font-size: 12px;
  }
  
  .top-products-table th,
  .top-products-table td {
    padding: 8px 6px;
  }
}
</style>