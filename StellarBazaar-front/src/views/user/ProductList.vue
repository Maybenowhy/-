<template>
  <div class="product-list-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">精选商品</h2>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>正在加载商品...</p>
    </div>

    <!-- 错误状态 -->
    <div v-if="error" class="error-state">
      <div class="error-icon">❌</div>
      <h3>加载失败</h3>
      <p>{{ error }}</p>
      <button class="retry-btn" @click="loadProducts">重试</button>
    </div>

    <!-- 商品列表 -->
    <div v-if="!loading && !error" class="product-section">
      <div v-if="products.length === 0" class="empty-state">
        <div class="empty-icon">📦</div>
        <h3>暂无商品</h3>
        <p>当前没有可用的商品，请稍后再来</p>
      </div>
      
      <!-- 添加容器限制宽度 -->
      <div class="product-container">
        <div class="product-grid">
          <div class="product-card" v-for="product in products" :key="product.id">
            <div class="product-image">
              <img :src="product.imageUrl || '/placeholder-image.jpg'" :alt="product.name" />
              <div class="product-overlay">
                <button class="quick-view-btn" @click="viewProduct(product)">快速查看</button>
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-description">{{ product.description }}</p>
              <div class="product-meta">
                <span class="stock" :class="{ 'low-stock': product.stockQuantity < 10 }">
                  库存: {{ product.stockQuantity }}
                </span>
                <span class="status active">
                  上架
                </span>
              </div>
              <div class="product-footer">
                <span class="product-price">￥{{ product.price }}</span>
                <button 
                  class="add-to-cart-btn" 
                  @click="addToCart(product)"
                  :disabled="product.stockQuantity === 0"
                >
                  <span v-if="product.stockQuantity === 0">缺货</span>
                  <span v-else>加入购物车</span>
                  <i class="cart-icon">🛒</i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页控件 -->
      <div v-if="totalPages > 1" class="pagination">
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
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { productPage } from "@/api/product";

const router = useRouter();

// 响应式数据
const products = ref([]);
const loading = ref(true);
const error = ref("");
const currentPage = ref(1);
const pageSize = ref(12);
const totalItems = ref(0);

// 计算属性
const totalPages = computed(() => {
  return Math.ceil(totalItems.value / pageSize.value);
});

// 加载商品
const loadProducts = async () => {
  try {
    loading.value = true;
    error.value = "";
    
    console.log("开始请求商品数据...");
    
    // 使用分页接口
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    };
    
    console.log("请求参数:", params);
    
    const response = await productPage(params);
    console.log("分页接口响应:", response);
    
    // 根据后端响应结构调整
    if (response && response.code === 1) {
      // 如果返回有 code 字段且为1表示成功
      if (response.data) {
        products.value = response.data.list || response.data.records || [];
        totalItems.value = response.data.total || 0;
      } else {
        products.value = response.list || response.records || [];
        totalItems.value = response.total || 0;
      }
    } else if (Array.isArray(response)) {
      // 如果直接返回数组
      products.value = response;
      totalItems.value = response.length;
    } else if (response && (response.list || response.records)) {
      // 如果返回有 list 或 records 字段
      products.value = response.list || response.records;
      totalItems.value = response.total || (response.list ? response.list.length : response.records.length);
    } else {
      // 如果结构不符合预期
      console.error("分页接口返回结构不符合预期:", response);
      throw new Error("数据格式错误");
    }
    
    console.log('处理后的商品数据:', products.value);
    
  } catch (err) {
    console.error("分页接口请求失败:", err);
    error.value = "加载商品失败，请检查网络连接或稍后重试";
    products.value = [];
  } finally {
    loading.value = false;
  }
};

// 切换页码
const changePage = (page) => {
  currentPage.value = page;
  loadProducts();
};

// 加入购物车 - 移除消息提示
const addToCart = (product) => {
  if (product.stockQuantity === 0) {
    return;
  }
  
  let cart = JSON.parse(localStorage.getItem("cart") || "[]");
  
  // 检查商品是否已在购物车中
  const existingItem = cart.find(item => item.id === product.id);
  if (existingItem) {
    if (existingItem.quantity >= product.stockQuantity) {
      // 静默处理，不显示消息
      return;
    }
    existingItem.quantity = (existingItem.quantity || 1) + 1;
  } else {
    cart.push({
      id: product.id,
      name: product.name,
      price: product.price,
      imageUrl: product.imageUrl,
      stockQuantity: product.stockQuantity,
      quantity: 1
    });
  }
  
  localStorage.setItem("cart", JSON.stringify(cart));
  // 移除了消息提示，静默添加
};

// 查看商品详情
const viewProduct = (product) => {
  router.push(`/product/${product.id}`);
};

// 生命周期
onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
.product-list-page {
  padding: 0;
  max-width: 1400px; /* 限制整个页面最大宽度 */
  margin: 0 auto; /* 居中 */
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 0 20px; /* 添加内边距 */
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 10px;
}

/* 商品容器 - 新增 */
.product-container {
  max-width: 1200px; /* 限制商品区域最大宽度 */
  margin: 0 auto; /* 居中 */
  padding: 0 20px; /* 添加内边距 */
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid rgba(106, 17, 203, 0.2);
  border-radius: 50%;
  border-top-color: #6a11cb;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误状态 */
.error-state {
  text-align: center;
  padding: 60px 20px;
  background: #fff5f5;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid #fed7d7;
  max-width: 600px; /* 限制错误状态宽度 */
  margin: 0 auto; /* 居中 */
}

.error-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.error-state h3 {
  font-size: 24px;
  color: #e53e3e;
  margin-bottom: 10px;
}

.error-state p {
  color: #718096;
  font-size: 16px;
  margin-bottom: 20px;
}

.retry-btn {
  background: #e53e3e;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.retry-btn:hover {
  background: #c53030;
}

/* 商品区域 */
.product-section {
  margin-top: 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  max-width: 600px; /* 限制空状态宽度 */
  margin: 0 auto; /* 居中 */
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

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); /* 稍微缩小卡片最小宽度 */
  gap: 25px; /* 减小间距 */
  margin-bottom: 40px;
  justify-items: center; /* 卡片在网格内居中 */
}

.product-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  width: 100%; /* 确保卡片填满网格单元格 */
  max-width: 280px; /* 限制卡片最大宽度 */
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.product-image {
  position: relative;
  height: 180px; /* 缩小图片高度 */
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.quick-view-btn {
  background: white;
  color: #333;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-view-btn:hover {
  background: #6a11cb;
  color: white;
}

.product-info {
  padding: 16px; /* 减小内边距 */
}

.product-name {
  font-size: 16px; /* 稍微缩小字体 */
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 8px;
}

/* 兼容性处理：多行文本截断 */
.product-description {
  color: #718096;
  font-size: 13px; /* 稍微缩小字体 */
  line-height: 1.5;
  margin-bottom: 12px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  box-orient: vertical;
  max-height: 3em;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 12px;
}

.stock {
  color: #38a169;
  font-weight: 500;
}

.stock.low-stock {
  color: #e53e3e;
}

.status {
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.status.active {
  background: #c6f6d5;
  color: #22543d;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 18px; /* 稍微缩小价格字体 */
  font-weight: 700;
  color: #e53e3e;
}

.add-to-cart-btn {
  display: flex;
  align-items: center;
  gap: 6px; /* 减小间距 */
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
  border: none;
  padding: 8px 14px; /* 减小按钮内边距 */
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 13px; /* 稍微缩小按钮字体 */
}

.add-to-cart-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(106, 17, 203, 0.3);
}

.add-to-cart-btn:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.cart-icon {
  font-size: 14px; /* 稍微缩小图标 */
}

/* 分页控件 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 30px 0;
  max-width: 600px; /* 限制分页宽度 */
  margin: 0 auto; /* 居中 */
}

.pagination-btn {
  padding: 10px 20px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #4a5568;
}

.pagination-btn:hover:not(:disabled) {
  background: #6a11cb;
  color: white;
  border-color: #6a11cb;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  color: #718096;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .product-list-page {
    max-width: 100%;
    padding: 0 15px;
  }
  
  .product-container {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 28px;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
  }
  
  .pagination {
    flex-direction: column;
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
  
  .product-card {
    max-width: 100%;
  }
}

/* 兼容性回退样式 */
@supports not ((display: -webkit-box) or (display: box)) {
  .product-description {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>