<template>
  <div class="page-container">
    <h2>商品详情</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>正在加载商品信息...</p>
    </div>

    <!-- 商品详情 -->
    <div v-else class="product-detail">
      <img :src="product.imageUrl || '/placeholder-image.jpg'" :alt="product.name" class="product-image"
        @error="handleImageError" />
      <div class="product-info">
        <h3 class="product-name">{{ product.name || '商品名称' }}</h3>
        <p class="product-description">{{ product.description || '商品描述' }}</p>
        <p class="price">￥{{ product.price || 0 }}</p>
        <p class="stock" v-if="product.stockQuantity !== undefined">
          库存: {{ product.stockQuantity }}件
        </p>
        <button @click="addToCart(product)" class="add-to-cart-btn"
          :disabled="!product.stockQuantity || product.stockQuantity <= 0">
          {{ (!product.stockQuantity || product.stockQuantity <= 0) ? '缺货中' : '加入购物车' }} </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { productDetail } from "@/api/product";
import { logUserBrowse } from '@/api/userLog';

const route = useRoute();
const product = ref({});
const loading = ref(true);

// 获取商品详情
const loadProduct = async () => {
  const productId = route.params.id;
  loading.value = true;

  try {
    const response = await productDetail(productId);
    console.log("商品详情响应:", response);

    // 直接使用响应数据，因为后端返回的是商品对象本身
    if (response && response.id) {
      product.value = response;
      console.log("商品数据加载成功:", product.value);
      
      // 商品加载成功后记录浏览日志
      await recordBrowseLog(productId);
    } else {
      console.warn("商品信息结构异常:", response);
      // 使用响应数据，即使结构不完全符合预期
      product.value = response || {};
      
      // 即使结构异常，如果productId存在也记录浏览日志
      if (productId) {
        await recordBrowseLog(productId);
      }
    }

  } catch (error) {
    console.error("加载商品详情失败:", error);
    // 设置默认商品数据
    product.value = {
      id: productId,
      name: `商品 ${productId}`,
      description: '商品描述加载中...',
      price: 0,
      imageUrl: '/placeholder-image.jpg',
      stockQuantity: 0
    };
    
    // 即使加载失败，如果productId存在也尝试记录浏览日志
    if (productId) {
      await recordBrowseLog(productId);
    }
  } finally {
    loading.value = false;
  }
};

// 记录浏览日志的方法
const recordBrowseLog = async (productId) => {
  try {
    // 获取用户ID，如果不存在则使用默认值
    const userId = localStorage.getItem('userId') || 3;
    
    // 调用记录浏览日志的API，不传IP地址
    await logUserBrowse({
      userId: parseInt(userId),
      actionType: 'browse',
      productId: parseInt(productId)
      // 移除了 ipAddress 字段
    });
    
    console.log('浏览日志记录成功 - 商品ID:', productId, '用户ID:', userId);
  } catch (error) {
    console.error('记录浏览日志失败:', error);
    // 这里可以选择不抛出错误，避免影响主要功能
  }
};

// 图片加载失败处理
const handleImageError = (event) => {
  console.log('图片加载失败，使用默认图片');
  event.target.src = '/placeholder-image.jpg';
};

// 加入购物车功能
const addToCart = (product) => {
  // 检查库存
  if (!product.stockQuantity || product.stockQuantity <= 0) {
    alert("商品缺货，无法加入购物车");
    return;
  }

  let cart = JSON.parse(localStorage.getItem("cart") || "[]");

  // 检查商品是否已在购物车中
  const existingItemIndex = cart.findIndex(item => item.id === product.id);
  if (existingItemIndex !== -1) {
    // 如果商品已在购物车中，增加数量
    cart[existingItemIndex].quantity = (cart[existingItemIndex].quantity || 1) + 1;
  } else {
    // 否则添加新商品
    cart.push({
      id: product.id,
      name: product.name,
      price: product.price,
      imageUrl: product.imageUrl,
      quantity: 1
    });
  }

  localStorage.setItem("cart", JSON.stringify(cart));
  alert("商品已成功加入购物车！");
};

onMounted(loadProduct);
</script>

<style scoped>
.page-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-container h2 {
  text-align: center;
  font-size: 32px;
  color: #2d3748;
  margin-bottom: 30px;
  font-weight: 700;
}

/* 加载状态 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(106, 17, 203, 0.2);
  border-radius: 50%;
  border-top-color: #6a11cb;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.loading p {
  font-size: 16px;
  color: #718096;
}

/* 商品详情布局 */
.product-detail {
  display: flex;
  gap: 40px;
  max-width: 800px;
  margin: 0 auto;
  align-items: flex-start;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 40px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.product-detail:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.product-image {
  width: 300px;
  height: 300px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  text-align: left;
}

.product-name {
  font-size: 28px;
  color: #2d3748;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.3;
}

.product-description {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.price {
  font-size: 28px;
  color: #e53935;
  font-weight: bold;
  margin-bottom: 16px;
}

.stock {
  font-size: 16px;
  color: #4caf50;
  margin-bottom: 24px;
  font-weight: 500;
}

.add-to-cart-btn {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: #fff;
  padding: 14px 28px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(106, 17, 203, 0.3);
  width: 100%;
  max-width: 200px;
}

.add-to-cart-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(106, 17, 203, 0.4);
}

.add-to-cart-btn:active {
  transform: translateY(0);
}

.add-to-cart-btn:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .page-container h2 {
    font-size: 28px;
    margin-bottom: 20px;
  }

  .product-detail {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 24px;
    gap: 24px;
  }

  .product-info {
    text-align: center;
  }

  .product-image {
    width: 250px;
    height: 250px;
  }

  .product-name {
    font-size: 24px;
  }

  .price {
    font-size: 24px;
  }

  .add-to-cart-btn {
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  .product-detail {
    padding: 20px;
  }

  .product-image {
    width: 200px;
    height: 200px;
  }

  .product-name {
    font-size: 22px;
  }

  .price {
    font-size: 22px;
  }
}
</style>