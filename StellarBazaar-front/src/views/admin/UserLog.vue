<template>
  <div class="user-log-page">
    <div class="page-header">
      <h1>用户行为日志</h1>
      <p>查看和分析用户的浏览和购买行为</p>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-card">
      <div class="filter-row">
        <div class="filter-group">
          <label>用户ID：</label>
          <input 
            v-model="filters.userId" 
            type="number" 
            placeholder="输入用户ID" 
            class="filter-input"
          />
        </div>
        <div class="filter-group">
          <label>行为类型：</label>
          <select v-model="filters.actionType" class="filter-select">
            <option value="">全部</option>
            <option value="browse">浏览</option>
            <option value="purchase">购买</option>
          </select>
        </div>
        <div class="filter-group">
          <label>开始时间：</label>
          <input 
            v-model="filters.startTime" 
            type="datetime-local" 
            class="filter-input"
          />
        </div>
        <div class="filter-group">
          <label>结束时间：</label>
          <input 
            v-model="filters.endTime" 
            type="datetime-local" 
            class="filter-input"
          />
        </div>
        <div class="filter-actions">
          <button class="btn btn-primary" @click="loadLogs">
            <i class="icon-search">🔍</i>
            查询
          </button>
          <button class="btn btn-secondary" @click="resetFilters">
            <i class="icon-reset">🔄</i>
            重置
          </button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon browse">👁️</div>
        <div class="stat-info">
          <h3>{{ formatNumber(stats.totalBrowses) }}</h3>
          <p>总浏览数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purchase">🛒</div>
        <div class="stat-info">
          <h3>{{ formatNumber(stats.totalPurchases) }}</h3>
          <p>总购买数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon conversion">📊</div>
        <div class="stat-info">
          <h3>{{ conversionRate }}%</h3>
          <p>转化率</p>
        </div>
      </div>
    </div>

    <!-- 日志表格 -->
    <div class="table-card">
      <div class="table-header">
        <h3>用户行为日志</h3>
      </div>
      
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th width="80">ID</th>
              <th width="100">用户ID</th>
              <th width="120">行为类型</th>
              <th width="120">目标ID</th>
              <th>IP地址</th>
              <th width="180">创建时间</th>
              <th width="100">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="log in logs" :key="log.id">
              <td>{{ log.id }}</td>
              <td>{{ log.userId }}</td>
              <td>
                <span :class="`action-badge ${log.actionType}`">
                  {{ getActionTypeText(log.actionType) }}
                </span>
              </td>
              <td>{{ log.targetId }}</td>
              <td>{{ log.ipAddress }}</td>
              <td>{{ formatDateTime(log.createdAt) }}</td>
              <td>
                <button class="btn-action" @click="viewLogDetail(log)">
                  <i class="icon-view">👁️</i>
                  查看
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- 空状态 -->
        <div v-if="logs.length === 0" class="empty-state">
          <div class="empty-icon">📝</div>
          <p>暂无日志数据</p>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="logs.length > 0" class="pagination">
        <button 
          class="pagination-btn" 
          :disabled="pagination.current === 1"
          @click="changePage(pagination.current - 1)"
        >
          上一页
        </button>
        <span class="pagination-info">
          第 {{ pagination.current }} 页，共 {{ pagination.total }} 页
        </span>
        <button 
          class="pagination-btn" 
          :disabled="pagination.current === pagination.total"
          @click="changePage(pagination.current + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getUserLogList } from '@/api/userLog'

// 响应式数据
const logs = ref([])
const loading = ref(false)
const filters = reactive({
  userId: '',
  actionType: '',
  startTime: '',
  endTime: ''
})
const stats = reactive({
  totalBrowses: 0,
  totalPurchases: 0
})
const pagination = reactive({
  current: 1,
  total: 1,
  size: 20
})

// 计算属性
const conversionRate = computed(() => {
  if (stats.totalBrowses === 0) return 0
  return ((stats.totalPurchases / stats.totalBrowses) * 100).toFixed(2)
})

// 方法
const loadLogs = async () => {
  try {
    loading.value = true
    
    // 设置默认值
    const defaultUserId = 3;
    
    // 获取当前年份
    const currentYear = new Date().getFullYear();
    
    // 设置开始时间为今年的11月2日
    const defaultStartTime = new Date(currentYear, 10, 2); // 月份从0开始，10表示11月
    
    // 设置结束时间为下一年的11月22日0点0分
    const defaultEndTime = new Date(currentYear + 1, 10, 22, 0, 0, 0);
    
    // 格式化日期为 YYYY-MM-DDTHH:mm 格式（datetime-local 输入框的格式）
    const formatDateForInput = (date) => {
      return date.toISOString().slice(0, 16);
    };
    
    // 如果用户没有设置值，使用默认值
    if (!filters.userId) {
      filters.userId = defaultUserId;
    }
    if (!filters.startTime) {
      filters.startTime = formatDateForInput(defaultStartTime);
    }
    if (!filters.endTime) {
      filters.endTime = formatDateForInput(defaultEndTime);
    }
    
    // 构建干净的查询参数
    const cleanParams = {}
    
    if (filters.userId) cleanParams.userId = filters.userId
    if (filters.actionType) cleanParams.actionType = filters.actionType
    if (filters.startTime) cleanParams.startTime = filters.startTime
    if (filters.endTime) cleanParams.endTime = filters.endTime
    
    console.log('查询参数:', cleanParams)
    
    const response = await getUserLogList(cleanParams)
    
    if (response) {
      logs.value = response || []
      console.log('用户日志响应:', response)
      
      // 计算统计信息
      stats.totalBrowses = logs.value.filter(log => log.actionType === 'browse').length
      stats.totalPurchases = logs.value.filter(log => log.actionType === 'purchase').length
    }
  } catch (error) {
    console.error('加载用户日志失败:', error)
    // 使用模拟数据
    useMockData()
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.userId = ''
  filters.actionType = ''
  filters.startTime = ''
  filters.endTime = ''
  loadLogs()
}

const getActionTypeText = (actionType) => {
  const typeMap = {
    'browse': '浏览',
    'purchase': '购买'
  }
  return typeMap[actionType] || actionType
}

const formatNumber = (num) => {
  return new Intl.NumberFormat('zh-CN').format(num)
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  try {
    const date = new Date(dateTime)
    return date.toLocaleString('zh-CN')
  } catch (error) {
    return dateTime
  }
}

const viewLogDetail = (log) => {
  // 这里可以实现查看日志详情的逻辑
  console.log('查看日志详情:', log)
  alert(`日志详情：
ID: ${log.id}
用户ID: ${log.userId}
行为类型: ${getActionTypeText(log.actionType)}
目标ID: ${log.targetId}
IP地址: ${log.ipAddress}
创建时间: ${formatDateTime(log.createdAt)}`)
}

const changePage = (page) => {
  pagination.current = page
  loadLogs()
}

const useMockData = () => {
  // 模拟数据
  logs.value = [
    {
      id: 1,
      userId: 1001,
      actionType: 'browse',
      targetId: 201,
      ipAddress: '192.168.1.100',
      createdAt: '2024-01-15 10:30:00'
    },
    {
      id: 2,
      userId: 1001,
      actionType: 'purchase',
      targetId: 201,
      ipAddress: '192.168.1.100',
      createdAt: '2024-01-15 11:15:00'
    },
    {
      id: 3,
      userId: 1002,
      actionType: 'browse',
      targetId: 202,
      ipAddress: '192.168.1.101',
      createdAt: '2024-01-15 14:20:00'
    }
  ]
  
  stats.totalBrowses = 2
  stats.totalPurchases = 1
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.user-log-page {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
}

.page-header p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

/* 筛选卡片 */
.filter-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 24px;
}

.filter-row {
  display: flex;
  gap: 16px;
  align-items: end;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  font-weight: 500;
  color: #495057;
}

.filter-input, .filter-select {
  padding: 8px 12px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  font-size: 14px;
  min-width: 150px;
}

.filter-input:focus, .filter-select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.filter-actions {
  display: flex;
  gap: 12px;
  align-items: end;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  gap: 16px;
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

.stat-icon.browse {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.purchase {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon.conversion {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info h3 {
  margin: 0 0 4px 0;
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
}

.stat-info p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

/* 表格卡片 */
.table-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
}

.table-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

/* 数据表格 */
.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f8f9fa;
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.data-table td {
  padding: 16px 12px;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.data-table tr:hover {
  background: #f8f9fa;
}

/* 行为标签 */
.action-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.action-badge.browse {
  background: #e3f2fd;
  color: #1976d2;
}

.action-badge.purchase {
  background: #e8f5e8;
  color: #2e7d32;
}

/* 操作按钮 */
.btn-action {
  background: #3498db;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: background 0.3s ease;
}

.btn-action:hover {
  background: #2980b9;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-top: 1px solid #e9ecef;
}

.pagination-btn {
  padding: 8px 16px;
  border: 1px solid #e1e5e9;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #3498db;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  color: #6c757d;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  margin: 0;
  font-size: 16px;
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255,255,255,0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 按钮样式 */
.btn {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: #3498db;
  color: white;
}

.btn-primary:hover {
  background: #2980b9;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-group {
    width: 100%;
  }
  
  .filter-input, .filter-select {
    min-width: auto;
  }
}

@media (max-width: 768px) {
  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .pagination {
    flex-direction: column;
    gap: 12px;
  }
}
</style>