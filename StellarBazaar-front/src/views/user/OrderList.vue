<template>
  <div class="page-container">
    <h2>订单历史</h2>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>正在加载订单...</p>
    </div>

    <div v-else-if="orders.length === 0" class="empty-state">
      <div class="empty-icon">📦</div>
      <h3>暂无订单</h3>
      <p>您还没有任何订单记录</p>
    </div>

    <div v-else>
      <table class="sb-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>总金额</th>
            <th>收货地址</th>
            <th>联系电话</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td>{{ order.orderNumber }}</td>
            <td>￥{{ order.totalAmount }}</td>
            <td>{{ order.shippingAddress }}</td>
            <td>{{ order.shippingPhone }}</td>
            <td>
              <span class="status" :class="getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </span>
            </td>
            <td>{{ formatDate(order.createdAt) }}</td>
            <td>
              <button class="view-btn" @click="viewOrder(order.id)">查看详情</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { orderList } from "@/api/order";

const router = useRouter();
const orders = ref([]);
const loading = ref(true);
const error = ref("");

const loadOrders = async () => {
  try {
    loading.value = true;
    error.value = "";
    
    // 从 token 中获取用户ID，或者使用其他方式
    // 方式1: 如果后端从token解析userId，可以不需要传参数
    // 方式2: 如果登录时存储了用户ID，从localStorage获取
    
    // 尝试从localStorage获取用户ID
    const userId = localStorage.getItem("userId") || localStorage.getItem("id");
    
    console.log("加载订单，用户ID:", userId);
    
    let response;
    if (userId) {
      // 如果获取到用户ID，传递参数
      response = await orderList(userId);
    } else {
      // 如果没有用户ID，尝试不传参数（后端从token解析）
      response = await orderList();
    }
    
    console.log("订单列表响应:", response);
    
    // 处理响应数据
    if (response && Array.isArray(response)) {
      orders.value = response;
    } else if (response && response.code === 1) {
      orders.value = response.data || [];
    } else {
      orders.value = [];
    }
    
  } catch (err) {
    console.error("加载订单失败:", err);
    error.value = "加载订单失败，请稍后重试";
    orders.value = [];
  } finally {
    loading.value = false;
  }
};

const getStatusClass = (status) => {
  const statusMap = {
    'pending_payment': 'pending',
    'paid': 'paid', 
    'shipped': 'shipped',
    'delivered': 'delivered',
    'cancelled': 'cancelled'
  };
  return statusMap[status] || 'pending';
};

const getStatusText = (status) => {
  const statusTextMap = {
    'pending_payment': '待支付',
    'paid': '已支付',
    'shipped': '已发货',
    'delivered': '已收货',
    'cancelled': '已取消'
  };
  return statusTextMap[status] || status;
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN');
};

const viewOrder = (orderId) => {
  router.push(`/order/${orderId}`);
};

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

h2 {
  font-size: 28px;
  margin-bottom: 30px;
  text-align: center;
  color: #333;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(106, 17, 203, 0.2);
  border-radius: 50%;
  border-top-color: #6a11cb;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 24px;
  color: #2d3748;
  margin-bottom: 10px;
}

.empty-state p {
  color: #718096;
  font-size: 16px;
}

.sb-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.sb-table th {
  background: #f8f9fa;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #e9ecef;
}

.sb-table td {
  padding: 16px;
  border-bottom: 1px solid #f1f1f1;
  vertical-align: middle;
}

.sb-table tbody tr:hover {
  background: #f8f9fa;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status.pending {
  background: #fff3cd;
  color: #856404;
}

.status.paid {
  background: #d1ecf1;
  color: #0c5460;
}

.status.shipped {
  background: #d4edda;
  color: #155724;
}

.status.delivered {
  background: #d1e7dd;
  color: #0f5132;
}

.status.cancelled {
  background: #f8d7da;
  color: #721c24;
}

.view-btn {
  background: #6a11cb;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.view-btn:hover {
  background: #5a0fb8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sb-table {
    font-size: 14px;
  }
  
  .sb-table th,
  .sb-table td {
    padding: 12px 8px;
  }
}
</style>