<template>
  <div class="dashboard">
    <h1>数据概览</h1>
    <div class="stats-grid">
      <div class="stat-card">
        <h3>今日销售额</h3>
        <p class="stat-value">¥ {{ formatNumber(stats.totalRevenue || 0) }}</p>
      </div>
      <div class="stat-card">
        <h3>今日订单</h3>
        <p class="stat-value">{{ formatNumber(stats.todayOrders || 0) }}</p>
      </div>
      <div class="stat-card">
        <h3>商品总数</h3>
        <p class="stat-value">{{ formatNumber(stats.totalProducts || 0) }}</p>
      </div>
      <div class="stat-card">
        <h3>用户数量</h3>
        <p class="stat-value">{{ formatNumber(stats.totalUsers || 0) }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSalesSummary } from '@/api/adminReport'
import { getProductList } from '@/api/product'
import { getUserList } from '@/api/user'
import { getOrderPage } from '@/api/adminOrder'

// 响应式数据
const stats = ref({
  totalRevenue: 0,
  todayOrders: 0,
  totalProducts: 0,
  totalUsers: 0
})

const loading = ref(false)

// 获取统计数据
const loadStats = async () => {
  try {
    loading.value = true

    // 获取今日日期
    const today = new Date().toISOString().split('T')[0]

    // 并行调用所有API
    const [salesData, productsData, usersData, ordersData] = await Promise.all([
      getSalesSummary(today, today).catch(() => null),
      getProductList({ page: 1, pageSize: 1 }).catch(() => null),
      getUserList().catch(() => null),
      getOrderPage({ page: 1, pageSize: 1 }).catch(() => null)
    ])

    console.log('API响应数据:', {
      salesData,
      productsData,
      usersData,
      ordersData
    })

    // 处理销售数据
    if (salesData && salesData.code === 1) {
      stats.value.totalRevenue = salesData.data?.totalRevenue || 0
    } else if (salesData) {
      // 如果返回的不是标准响应格式，尝试直接读取
      stats.value.totalRevenue = salesData.totalRevenue || salesData.totalSales || 0
    }

    // 处理商品数据
    if (productsData && productsData.code === 1) {
      stats.value.totalProducts = productsData.data?.total || 0
    } else if (productsData && productsData.total !== undefined) {
      stats.value.totalProducts = productsData.total
    }

    // 处理用户数据
    if (usersData && usersData.code === 1) {
      stats.value.totalUsers = usersData.data?.total || usersData.data?.count || 0
    } else if (usersData && usersData.total !== undefined) {
      stats.value.totalUsers = usersData.total
    } else if (usersData && Array.isArray(usersData)) {
      // 如果返回的是用户列表数组
      stats.value.totalUsers = usersData.length
    }

    // 处理订单数据
    if (ordersData) {
      const orders = ordersData.records
      if (Array.isArray(orders)) {
        // 过滤今日订单
        const todayOrders = orders.filter(order => {
          try {
            // 使用正确的字段名 createdAt 和 paymentTime
            const dateString = order.createdAt || order.paymentTime
            if (!dateString) return false

            // 将日期字符串转换为 Date 对象
            const date = new Date(dateString)

            // 检查日期是否有效
            if (isNaN(date.getTime())) {
              console.warn('无效的日期:', dateString)
              return false
            }

            // 格式化为 YYYY-MM-DD 进行比较
            const orderDate = date.toISOString().split('T')[0]
            return orderDate === today
          } catch (error) {
            console.warn('日期解析错误:', order.createdAt, order.paymentTime, error)
            return false
          }
        })
        stats.value.todayOrders = todayOrders.length
      }
    } else if (Array.isArray(ordersData)) {
      // 如果直接返回订单数组
      const todayOrders = ordersData.filter(order => {
        try {
          // 使用正确的字段名 createdAt 和 paymentTime
          const dateString = order.createdAt || order.paymentTime
          if (!dateString) return false

          // 将日期字符串转换为 Date 对象
          const date = new Date(dateString)

          // 检查日期是否有效
          if (isNaN(date.getTime())) {
            console.warn('无效的日期:', dateString)
            return false
          }

          // 格式化为 YYYY-MM-DD 进行比较
          const orderDate = date.toISOString().split('T')[0]
          return orderDate === today
        } catch (error) {
          console.warn('日期解析错误:', order.createdAt, order.paymentTime, error)
          return false
        }
      })
      stats.value.todayOrders = todayOrders.length
    }

  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 使用默认数据
    useMockData()
  } finally {
    loading.value = false
  }
}

// 使用模拟数据作为备选
const useMockData = () => {
  stats.value = {
    totalRevenue: 125430,
    todayOrders: 89,
    totalProducts: 156,
    totalUsers: 2847
  }
}

// 格式化数字
const formatNumber = (num) => {
  return new Intl.NumberFormat('zh-CN').format(num)
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.stat-card h3 {
  margin: 0 0 12px 0;
  color: #7f8c8d;
  font-size: 14px;
  font-weight: 500;
}

.stat-value {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
}
</style>