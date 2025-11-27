<template>
  <div class="admin-page">
    <!-- 顶部导航 -->
    <div class="admin-header">
      <h1>商品管理</h1>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showAddForm = true">
          <i class="icon-add"></i>
          添加商品
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total"></div>
        <div class="stat-info">
          <h3>{{ totalProductCount }}</h3>
          <p>商品总数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon active"></div>
        <div class="stat-info">
          <h3>{{ activeProducts }}</h3>
          <p>上架商品</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon inactive"></div>
        <div class="stat-info">
          <h3>{{ inactiveProducts }}</h3>
          <p>下架商品</p>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="table-container">
      <table class="product-table">
        <thead>
          <tr>
            <th width="60">ID</th>
            <th width="120">商品图片</th>
            <th>商品名称</th>
            <th width="100">价格</th>
            <th width="80">库存</th>
            <th width="100">状态</th>
            <th width="120">创建时间</th>
            <th width="150">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>{{ product.id }}</td>
            <td>
              <div class="product-image">
                <img :src="product.imageUrl || '/placeholder-image.jpg'" :alt="product.name" />
              </div>
            </td>
            <td class="product-name">
              <div class="name">{{ product.name }}</div>
              <div class="description">{{ product.description }}</div>
            </td>
            <td class="price">¥{{ product.price }}</td>
            <td class="stock">{{ product.stock }}</td>
            <td>
              <span class="status" :class="product.isActive === 1 ? 'active' : 'inactive'">
                {{ product.isActive === 1 ? '上架' : '下架' }}
              </span>
            </td>
            <td class="create-time">{{ formatDate(product.createTime) }}</td>
            <td class="actions">
              <button class="btn-action edit" @click="editProduct(product)" title="编辑">
                <i class="icon-edit"></i>
              </button>
              <button 
                class="btn-action status-toggle" 
                @click="toggleProductStatus(product)"
                :title="product.isActive === 1 ? '下架' : '上架'"
              >
                <i :class="product.isActive === 1 ? 'icon-pause' : 'icon-play'"></i>
              </button>
              <button class="btn-action delete" @click="deleteProduct(product)" title="删除">
                <i class="icon-delete"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="products.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">📦</div>
        <h3>暂无商品</h3>
        <p>还没有添加任何商品，点击上方按钮添加第一个商品</p>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="products.length > 0" class="pagination">
      <button 
        class="pagination-btn" 
        :disabled="currentPage === 1" 
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span class="pagination-info">
        第 {{ currentPage }} 页 / 共 {{ totalPages }} 页 (共 {{ totalProductCount }} 件商品)
      </span>
      <button 
        class="pagination-btn" 
        :disabled="currentPage === totalPages" 
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 添加/编辑商品表单 -->
    <ProductForm 
      v-if="showAddForm || editingProduct"
      :product="editingProduct"
      :mode="editingProduct ? 'edit' : 'add'"
      @save="handleSaveProduct"
      @cancel="handleCancelForm"
    />

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="dialog-overlay">
      <div class="confirm-dialog">
        <h3>确认删除</h3>
        <p>确定要删除商品 "{{ productToDelete?.name }}" 吗？此操作不可恢复。</p>
        <div class="dialog-actions">
          <button class="cancel-btn" @click="showDeleteConfirm = false">取消</button>
          <button class="delete-confirm-btn" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import ProductForm from "./ProductForm.vue";
import { 
  getProductList, 
  addProduct, 
  updateProduct, 
  deleteProduct as deleteProductApi, 
  batchUpdateStatus 
} from '@/api/product';

// 响应式数据
const products = ref([]);
const allProducts = ref([]); // 存储所有商品数据用于统计计算
const showAddForm = ref(false);
const editingProduct = ref(null);
const searchKeyword = ref("");
const currentPage = ref(1);
const pageSize = 10;
const loading = ref(false);
const totalProductCount = ref(0);
const showDeleteConfirm = ref(false);
const productToDelete = ref(null);

// 计算属性
const totalPages = computed(() => 
  Math.ceil(totalProductCount.value / pageSize)
);

// 计算属性 - 基于全部数据计算
const activeProducts = computed(() => {
  return allProducts.value.filter(product => product.isActive === 1).length;
});

const inactiveProducts = computed(() => {
  return allProducts.value.filter(product => product.isActive === 0).length;
});

// 方法
const loadProducts = async () => {
  try {
    loading.value = true;
    const response = await getProductList({
      page: currentPage.value,
      pageSize: pageSize
    });
    
    console.log('获取商品列表成功:', response);
    
    // 根据实际的响应结构处理数据
    if (response && response.records) {
      // 响应格式: {total: 35, records: Array(10)}
      products.value = response.records.map(product => ({
        id: product.id,
        name: product.name,
        description: product.description,
        price: product.price,
        stock: product.stockQuantity,
        isActive: product.isActive, // 使用数字值
        imageUrl: product.imageUrl || '/placeholder-image.jpg',
        createTime: product.createTime
      }));
      
      // 更新总商品数
      totalProductCount.value = response.total || 0;
    } else {
      console.error('获取商品列表失败: 响应格式不正确', response);
      // 使用模拟数据作为备选
      products.value = [
        { 
          id: 1, 
          name: "高端智能手机", 
          description: "最新款旗舰手机，配备顶级摄像头",
          price: 5999, 
          stock: 50,
          isActive: 1, // 使用数字值
          imageUrl: "/phone.jpg",
          createTime: "2024-01-15"
        },
        { 
          id: 2, 
          name: "无线蓝牙耳机", 
          description: "降噪蓝牙耳机，续航时间长",
          price: 899, 
          stock: 100,
          isActive: 1, // 使用数字值
          imageUrl: "/earphone.jpg",
          createTime: "2024-01-10"
        },
      ];
      totalProductCount.value = products.value.length;
    }
  } catch (error) {
    console.error('加载商品数据失败:', error);
    // 使用模拟数据作为备选
    products.value = [
      { 
        id: 1, 
        name: "高端智能手机", 
        description: "最新款旗舰手机，配备顶级摄像头",
        price: 5999, 
        stock: 50,
        isActive: 1, // 使用数字值
        imageUrl: "/phone.jpg",
        createTime: "2024-01-15"
      },
      { 
        id: 2, 
        name: "无线蓝牙耳机", 
        description: "降噪蓝牙耳机，续航时间长",
        price: 899, 
        stock: 100,
        isActive: 1, // 使用数字值
        imageUrl: "/earphone.jpg",
        createTime: "2024-01-10"
      },
    ];
    totalProductCount.value = products.value.length;
  } finally {
    loading.value = false;
  }
};

// 加载所有商品数据用于统计
const loadAllProductsForStats = async () => {
  try {
    const response = await getProductList({
      page: 1,
      pageSize: 10000 // 获取足够大的数量来包含所有商品
    });
    
    if (response && response.records) {
      allProducts.value = response.records.map(product => ({
        id: product.id,
        name: product.name,
        isActive: product.isActive
      }));
    }
  } catch (error) {
    console.error('加载统计数据失败:', error);
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadProducts();
};

const changePage = (page) => {
  currentPage.value = page;
  loadProducts();
};

const editProduct = (product) => {
  editingProduct.value = { ...product };
  showAddForm.value = true;
};

const deleteProduct = (product) => {
  productToDelete.value = product;
  showDeleteConfirm.value = true;
};

const confirmDelete = async () => {
  if (!productToDelete.value) return;
  
  try {
    const response = await deleteProductApi(productToDelete.value.id);
    if (!response) {
      // 立即从本地列表中移除，无需重新加载整个列表
      products.value = products.value.filter(p => p.id !== productToDelete.value.id);
      totalProductCount.value = Math.max(0, totalProductCount.value - 1);
      // 同时更新统计数据
      await loadAllProductsForStats();
      alert("删除成功");
    } else {
      alert("删除失败: " + (response?.msg || '未知错误'));
    }
  } catch (error) {
    console.error('删除商品失败:', error);
    alert("删除失败，请稍后重试");
  } finally {
    showDeleteConfirm.value = false;
    productToDelete.value = null;
  }
};

const toggleProductStatus = async (product) => {
  const newIsActive = product.isActive === 1 ? 0 : 1;
  const action = newIsActive === 1 ? "上架" : "下架";
  
  if (!confirm(`确定${action}商品 "${product.name}" 吗？`)) return;
  
  try {
    const response = await batchUpdateStatus([product.id], newIsActive);
    if (!response) {
      // 立即更新本地状态，无需重新加载整个列表
      const index = products.value.findIndex(p => p.id === product.id);
      if (index !== -1) {
        products.value[index].isActive = newIsActive;
      }
      // 同时更新统计数据
      await loadAllProductsForStats();
      alert(`${action}成功`);
    } else {
      alert(`${action}失败: ` + (response?.msg || '未知错误'));
    }
  } catch (error) {
    console.error(`${action}商品失败:`, error);
    alert(`${action}失败，请稍后重试`);
  }
};

const handleSaveProduct = async (productData) => {
  try {
    console.log('保存商品数据:', productData);
    
    // 确保使用正确的字段名
    const submitData = {
      name: productData.name,
      description: productData.description,
      price: productData.price,
      stockQuantity: productData.stockQuantity,
      imageUrl: productData.imageUrl,
      isActive: productData.isActive !== undefined ? productData.isActive : 1 // 使用数字值，默认上架
    };

    if (editingProduct.value) {
      submitData.id = editingProduct.value.id;
      const response = await updateProduct(submitData);
      
      if (!response) {
        await loadProducts();
        // 同时更新统计数据
        await loadAllProductsForStats();
        // 确保关闭对话框
        showAddForm.value = false;
        editingProduct.value = null;
        alert("更新成功");
      } else {
        alert("更新失败: " + (response?.msg || '未知错误'));
      }
    } else {
      const response = await addProduct(submitData);
      if (!response) {
        await loadProducts();
        // 同时更新统计数据
        await loadAllProductsForStats();
        // 确保关闭对话框
        showAddForm.value = false;
        alert("添加成功");
      } else {
        alert("添加失败: " + (response?.msg || '未知错误'));
      }
    }
  } catch (error) {
    console.error('保存商品失败:', error);
    alert('操作失败，请重试');
  }
};

const handleCancelForm = () => {
  showAddForm.value = false;
  editingProduct.value = null;
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  try {
    return new Date(dateString).toLocaleDateString('zh-CN');
  } catch (error) {
    return '-';
  }
};

onMounted(() => {
  loadProducts();
  loadAllProductsForStats(); // 加载统计数据
});
</script>

<style scoped>
/* 样式部分保持不变 */
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
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
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

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.inactive {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-info h3 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 4px 0;
  color: #1a1a1a;
}

.stat-info p {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
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

.product-table {
  width: 100%;
  border-collapse: collapse;
}

.product-table th {
  background: #f8f9fa;
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.product-table td {
  padding: 16px 12px;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.product-table tbody tr:hover {
  background: #f8f9fa;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-name .name {
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.product-name .description {
  color: #6c757d;
  font-size: 12px;
  line-height: 1.4;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.price {
  font-weight: 600;
  color: #e74c3c;
}

.stock {
  text-align: center;
  font-weight: 500;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status.active {
  background: #d4edda;
  color: #155724;
}

.status.inactive {
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

.btn-action.edit {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-action.edit:hover {
  background: #bbdefb;
}

.btn-action.status-toggle {
  background: #e8f5e8;
  color: #2e7d32;
}

.btn-action.status-toggle:hover {
  background: #c8e6c9;
}

.btn-action.delete {
  background: #ffebee;
  color: #d32f2f;
}

.btn-action.delete:hover {
  background: #ffcdd2;
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

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 图标样式 */
.icon-add::before { content: "➕"; }
.icon-edit::before { content: "✏️"; }
.icon-delete::before { content: "🗑️"; }
.icon-play::before { content: "▶️"; }
.icon-pause::before { content: "⏸️"; }

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

.confirm-dialog {
  background: white;
  border-radius: 16px;
  padding: 32px;
  width: 480px;
  max-width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: dialogSlideIn 0.3s ease;
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

.confirm-dialog h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.confirm-dialog p {
  margin: 0 0 24px 0;
  color: #6c757d;
  line-height: 1.5;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.cancel-btn, .delete-confirm-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #5a6268;
}

.delete-confirm-btn {
  background: #e74c3c;
  color: white;
}

.delete-confirm-btn:hover {
  background: #c0392b;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .header-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .search-box input {
    width: 100%;
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
  
  .product-table {
    font-size: 12px;
  }
  
  .product-table th,
  .product-table td {
    padding: 12px 8px;
  }
  
  .pagination {
    flex-direction: column;
    gap: 12px;
  }
}
</style>