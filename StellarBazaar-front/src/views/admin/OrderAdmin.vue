<template>
  <div class="admin-page">
    <!-- 顶部导航 -->
    <div class="admin-header">
      <h1>订单管理</h1>
    </div>

    <!-- 订单列表 -->
    <div class="table-container">
      <table class="order-table">
        <thead>
          <tr>
            <th width="120">订单号</th>
            <th width="100">用户ID</th>
            <th width="120">联系电话</th>
            <th>收货地址</th>
            <th width="100">金额</th>
            <th width="100">状态</th>
            <th width="150">创建时间</th>
            <th width="80">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td class="order-id">{{ order.orderNumber }}</td>
            <td class="user-id">{{ order.userId }}</td>
            <td class="contact-info">{{ order.shippingPhone }}</td>
            <td class="shipping-address">{{ order.shippingAddress }}</td>
            <td class="amount">¥{{ order.totalAmount }}</td>
            <td>
              <span class="status" :class="getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </span>
            </td>
            <td class="create-time">{{ formatDate(order.createdAt) }}</td>
            <td class="actions">
              <button class="btn-action detail" @click="viewOrderDetail(order)" title="查看详情">
                <i class="icon-detail"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="orders.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">📦</div>
        <h3>暂无订单</h3>
        <p>还没有任何订单记录</p>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="orders.length > 0" class="pagination">
      <button 
        class="pagination-btn" 
        :disabled="currentPage === 1" 
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span class="pagination-info">
        第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
      </span>
      <button 
        class="pagination-btn" 
        :disabled="currentPage === totalPages" 
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 订单详情对话框 -->
    <div v-if="showOrderDetailDialog" class="dialog-overlay">
      <div class="order-detail-dialog">
        <div class="dialog-header">
          <h3>订单详情</h3>
          <button class="close-btn" @click="showOrderDetailDialog = false">×</button>
        </div>
        <div class="dialog-content" v-if="selectedOrder">
          <div class="order-info-section">
            <h4>订单信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <label>订单号：</label>
                <span>{{ selectedOrder.orderNumber }}</span>
              </div>
              <div class="info-item">
                <label>订单状态：</label>
                <span class="status" :class="getStatusClass(selectedOrder.status)">
                  {{ getStatusText(selectedOrder.status) }}
                </span>
              </div>
              <div class="info-item">
                <label>创建时间：</label>
                <span>{{ formatDate(selectedOrder.createdAt) }}</span>
              </div>
              <div class="info-item">
                <label>更新时间：</label>
                <span>{{ formatDate(selectedOrder.updatedAt) }}</span>
              </div>
              <div class="info-item">
                <label>总金额：</label>
                <span class="amount">¥{{ selectedOrder.totalAmount }}</span>
              </div>
            </div>
          </div>

          <div class="customer-info-section">
            <h4>收货信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <label>用户ID：</label>
                <span>{{ selectedOrder.userId }}</span>
              </div>
              <div class="info-item">
                <label>联系电话：</label>
                <span>{{ selectedOrder.shippingPhone }}</span>
              </div>
              <div class="info-item">
                <label>收货地址：</label>
                <span>{{ selectedOrder.shippingAddress }}</span>
              </div>
            </div>
          </div>

          <div class="timeline-section">
            <h4>时间线</h4>
            <div class="timeline">
              <div class="timeline-item">
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <div class="timeline-title">订单创建</div>
                  <div class="timeline-time">{{ formatDate(selectedOrder.createdAt) }}</div>
                </div>
              </div>
              <div v-if="selectedOrder.paymentTime" class="timeline-item">
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <div class="timeline-title">支付时间</div>
                  <div class="timeline-time">{{ formatDate(selectedOrder.paymentTime) }}</div>
                </div>
              </div>
              <div v-if="selectedOrder.shippingTime" class="timeline-item">
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <div class="timeline-title">发货时间</div>
                  <div class="timeline-time">{{ formatDate(selectedOrder.shippingTime) }}</div>
                </div>
              </div>
              <div v-if="selectedOrder.deliveryTime" class="timeline-item">
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <div class="timeline-title">送达时间</div>
                  <div class="timeline-time">{{ formatDate(selectedOrder.deliveryTime) }}</div>
                </div>
              </div>
            </div>
          </div>

          <div class="product-info-section" v-if="selectedOrder.items && selectedOrder.items.length > 0">
            <h4>商品信息</h4>
            <table class="product-table">
              <thead>
                <tr>
                  <th>商品名称</th>
                  <th width="80">单价</th>
                  <th width="60">数量</th>
                  <th width="100">小计</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in selectedOrder.items" :key="item.id">
                  <td>
                    <div class="product-name">{{ item.productName }}</div>
                    <div v-if="item.productImage" class="product-image">
                      <img :src="item.productImage" :alt="item.productName" />
                    </div>
                  </td>
                  <td>¥{{ item.unitPrice }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>¥{{ item.subtotal || (item.unitPrice * item.quantity).toFixed(2) }}</td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="3" class="total-label">总计：</td>
                  <td class="total-amount">¥{{ selectedOrder.totalAmount }}</td>
                </tr>
              </tfoot>
            </table>
          </div>
          <div v-else class="no-items">
            <p>暂无商品信息</p>
          </div>
        </div>
        <div class="dialog-actions">
          <button class="btn btn-secondary" @click="showOrderDetailDialog = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { 
  getOrderPage, 
  getOrderDetail
} from '@/api/adminOrder';

// 响应式数据
const orders = ref([]);
const showOrderDetailDialog = ref(false);
const selectedOrder = ref(null);
const currentPage = ref(1);
const pageSize = 10;
const loading = ref(false);
const totalOrderCount = ref(0);

// 计算属性
const totalPages = computed(() => 
  Math.ceil(totalOrderCount.value / pageSize)
);

// 方法
const loadOrders = async () => {
  try {
    loading.value = true;
    const params = {
      page: currentPage.value,
      pageSize: pageSize
    };
    
    const response = await getOrderPage(params);
    
    console.log('获取订单列表成功:', response);
    
    if (response && response.records) {
      orders.value = response.records.map(order => ({
        id: order.id,
        orderNumber: order.orderNumber,
        userId: order.userId,
        shippingPhone: order.shippingPhone,
        shippingAddress: order.shippingAddress,
        items: order.items || null,
        totalAmount: order.totalAmount,
        status: order.status,
        createdAt: order.createdAt,
        updatedAt: order.updatedAt,
        paymentTime: order.paymentTime,
        shippingTime: order.shippingTime,
        deliveryTime: order.deliveryTime
      }));
      
      totalOrderCount.value = response.total || 0;
    } else {
      console.error('获取订单列表失败: 响应格式不正确', response);
      // 使用模拟数据作为备选
      orders.value = [
        { 
          id: 12, 
          orderNumber: '20251121170054257',
          userId: 3, 
          shippingPhone: '67',
          shippingAddress: '67',
          items: null,
          totalAmount: 399, 
          status: 'pending_payment',
          createdAt: "2025-11-21 17:00",
          updatedAt: "2025-11-21 17:00",
          paymentTime: null,
          shippingTime: null,
          deliveryTime: null
        }
      ];
      totalOrderCount.value = orders.value.length;
    }
  } catch (error) {
    console.error('加载订单数据失败:', error);
    // 使用模拟数据作为备选
    orders.value = [
      { 
        id: 12, 
        orderNumber: '20251121170054257',
        userId: 3, 
        shippingPhone: '67',
        shippingAddress: '67',
        items: null,
        totalAmount: 399, 
        status: 'pending_payment',
        createdAt: "2025-11-21 17:00",
        updatedAt: "2025-11-21 17:00",
        paymentTime: null,
        shippingTime: null,
        deliveryTime: null
      }
    ];
    totalOrderCount.value = orders.value.length;
  } finally {
    loading.value = false;
  }
};

const changePage = (page) => {
  currentPage.value = page;
  loadOrders();
};

const viewOrderDetail = async (order) => {
  try {
    const response = await getOrderDetail(order.id);
    if (response) {
      selectedOrder.value = response;
      showOrderDetailDialog.value = true;
    } else {
      // 使用当前订单数据
      selectedOrder.value = order;
      showOrderDetailDialog.value = true;
    }
  } catch (error) {
    console.error('获取订单详情失败:', error);
    // 使用当前订单数据
    selectedOrder.value = order;
    showOrderDetailDialog.value = true;
  }
};

const getStatusClass = (status) => {
  const statusMap = {
    'pending_payment': 'pending',
    'paid': 'paid',
    'shipped': 'shipped',
    'completed': 'completed',
    'cancelled': 'cancelled'
  };
  return statusMap[status] || 'pending';
};

const getStatusText = (status) => {
  const statusMap = {
    'pending_payment': '待付款',
    'paid': '已付款',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消'
  };
  return statusMap[status] || status;
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  try {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
  } catch (error) {
    return dateString || '-';
  }
};

onMounted(() => {
  loadOrders();
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

/* 表格样式 */
.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
  margin-bottom: 24px;
  min-height: 400px;
  position: relative;
}

.order-table {
  width: 100%;
  border-collapse: collapse;
}

.order-table th {
  background: #f8f9fa;
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.order-table td {
  padding: 16px 12px;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.order-table tbody tr:hover {
  background: #f8f9fa;
}

.order-id {
  font-weight: 600;
  color: #3498db;
}

.user-id {
  color: #6c757d;
}

.contact-info {
  color: #495057;
}

.shipping-address {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.amount {
  font-weight: 600;
  color: #e74c3c;
}

.status {
  padding: 6px 12px;
  border-radius: 20px;
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

.status.completed {
  background: #e2e3e5;
  color: #383d41;
}

.status.cancelled {
  background: #f8d7da;
  color: #721c24;
}

.create-time {
  color: #6c757d;
  font-size: 13px;
}

.actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.btn-action {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
}

.btn-action.detail {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-action.detail:hover {
  background: #bbdefb;
}

/* 空状态 */
.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #6c757d;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #495057;
}

.empty-state p {
  font-size: 14px;
}

/* 加载状态 */
.loading-state {
  padding: 60px 20px;
  text-align: center;
  color: #6c757d;
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

.loading-state p {
  font-size: 14px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.pagination-btn {
  padding: 8px 16px;
  border: 1px solid #dee2e6;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #e9ecef;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  color: #6c757d;
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

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

/* 图标样式 */
.icon-detail::before { content: "👁️"; }

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.order-detail-dialog {
  background: white;
  border-radius: 16px;
  width: 800px;
  max-width: 90%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: dialogSlideIn 0.3s ease;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e9ecef;
}

.dialog-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6c757d;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: #f8f9fa;
}

.dialog-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.order-info-section, .customer-info-section, .timeline-section, .product-info-section {
  margin-bottom: 24px;
}

.order-info-section h4, .customer-info-section h4, .timeline-section h4, .product-info-section h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-weight: 500;
  color: #6c757d;
  font-size: 14px;
}

.info-item span {
  color: #2c3e50;
}

/* 时间线样式 */
.timeline {
  position: relative;
  padding-left: 20px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 6px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e9ecef;
}

.timeline-item {
  position: relative;
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
}

.timeline-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #3498db;
  position: absolute;
  left: -20px;
  top: 4px;
  z-index: 1;
}

.timeline-content {
  flex: 1;
}

.timeline-title {
  font-weight: 500;
  margin-bottom: 4px;
  color: #2c3e50;
}

.timeline-time {
  font-size: 12px;
  color: #6c757d;
}

/* 商品信息表格样式 */
.product-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 12px;
}

.product-table th, .product-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e9ecef;
}

.product-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #495057;
}

.product-name {
  font-weight: 500;
  margin-bottom: 8px;
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e9ecef;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-table tfoot {
  border-top: 2px solid #e9ecef;
}

.total-label {
  text-align: right;
  font-weight: 600;
}

.total-amount {
  font-weight: 700;
  color: #e74c3c;
  font-size: 16px;
}

.no-items {
  text-align: center;
  color: #6c757d;
  padding: 20px;
}

.dialog-actions {
  padding: 16px 24px;
  border-top: 1px solid #e9ecef;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@keyframes dialogSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-page {
    padding: 16px;
  }
  
  .admin-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .order-table {
    font-size: 12px;
  }
  
  .order-table th,
  .order-table td {
    padding: 12px 8px;
  }
  
  .pagination {
    flex-direction: column;
    gap: 12px;
  }
  
  .order-detail-dialog {
    width: 95%;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>