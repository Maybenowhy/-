<template>
  <div class="page-container">
    <!-- 添加页面头部和返回按钮 -->
    <div class="page-header">
      <button class="back-btn" @click="goBack">← 返回</button>
      <h2>购物车</h2>
      <button class="home-btn" @click="goHome">🏠 主页</button>
    </div>

    <div v-if="cart.length === 0" class="empty-cart">
      <div class="empty-icon">🛒</div>
      <p>您的购物车是空的</p>
      <router-link to="/customer/home" class="back-to-shopping">去购物</router-link>
    </div>

    <div v-else>
      <table class="sb-table">
        <thead>
          <tr>
            <th>商品图片</th>
            <th>商品名</th>
            <th>价格</th>
            <th>数量</th>
            <th>小计</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in cart" :key="item.id">
            <td class="product-image-cell">
              <img :src="item.imageUrl || '/placeholder-image.jpg'" :alt="item.name" class="product-thumb" />
            </td>
            <td class="product-name">{{ item.name }}</td>
            <td>￥{{ item.price }}</td>
            <td>
              <div class="quantity-controls">
                <button class="quantity-btn" @click="decreaseQuantity(index)" :disabled="item.quantity <= 1">-</button>
                <span class="quantity">{{ item.quantity }}</span>
                <button class="quantity-btn" @click="increaseQuantity(index)"
                  :disabled="item.quantity >= item.stockQuantity">+</button>
              </div>
            </td>
            <td>￥{{ (item.price * item.quantity).toFixed(2) }}</td>
            <td>
              <button class="remove-btn" @click="removeFromCart(index)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="checkout-section">
        <div class="address-form" v-if="showAddressForm">
          <h3>收货信息</h3>
          <div class="form-group">
            <label>收货地址:</label>
            <input type="text" v-model="shippingAddress" placeholder="请输入详细收货地址" class="address-input" />
          </div>
          <div class="form-group">
            <label>联系电话:</label>
            <input type="tel" v-model="shippingPhone" placeholder="请输入联系电话" class="phone-input" />
          </div>
        </div>

        <div class="total-section">
          <div class="total-line">
            <span>商品总数:</span>
            <span>{{ totalItems }} 件</span>
          </div>
          <div class="total-line">
            <span>商品总价:</span>
            <span>￥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="total-line grand-total">
            <span>应付总额:</span>
            <span>￥{{ totalPrice.toFixed(2) }}</span>
          </div>

          <button v-if="!showAddressForm" class="checkout-btn" @click="showAddressForm = true">
            去结算
          </button>

          <div v-else class="checkout-actions">
            <button class="confirm-btn" @click="submitOrderHandler" :disabled="submitting">
              {{ submitting ? '提交中...' : '确认下单' }}
            </button>
            <button class="cancel-btn" @click="showAddressForm = false">取消</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 消息提示 -->
    <div v-if="message" class="message" :class="{ success: messageType === 'success', error: messageType === 'error' }">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { submitOrder } from "@/api/order";

const router = useRouter();

// 响应式数据
const cart = ref([]);
const showAddressForm = ref(false);
const shippingAddress = ref("");
const shippingPhone = ref("");
const submitting = ref(false);
const message = ref("");
const messageType = ref("");

// 计算属性
const totalItems = computed(() => {
  return cart.value.reduce((total, item) => total + item.quantity, 0);
});

const totalPrice = computed(() => {
  return cart.value.reduce((total, item) => total + (item.price * item.quantity), 0);
});

// 导航功能
const goBack = () => {
  router.back();
};

const goHome = () => {
  router.push("/customer/home");
};

// 加载购物车
const loadCart = () => {
  const savedCart = localStorage.getItem("cart");
  cart.value = savedCart ? JSON.parse(savedCart) : [];
};

// 保存购物车到本地存储
const saveCart = () => {
  localStorage.setItem("cart", JSON.stringify(cart.value));
};

// 增加数量
const increaseQuantity = (index) => {
  if (cart.value[index].quantity < cart.value[index].stockQuantity) {
    cart.value[index].quantity++;
    saveCart();
  }
};

// 减少数量
const decreaseQuantity = (index) => {
  if (cart.value[index].quantity > 1) {
    cart.value[index].quantity--;
    saveCart();
  }
};

// 从购物车移除
const removeFromCart = (index) => {
  cart.value.splice(index, 1);
  saveCart();
  showMessage("商品已从购物车移除", "success");
};

// 提交订单
const submitOrderHandler = async () => {
  if (!shippingAddress.value.trim() || !shippingPhone.value.trim()) {
    showMessage("请填写完整的收货信息", "error");
    return;
  }

  submitting.value = true;

  try {
    // 检查购物车是否为空
    if (cart.value.length === 0) {
      showMessage("购物车为空，无法下单", "error");
      return;
    }

    // 构建订单数据
    const orderData = {
      shippingAddress: shippingAddress.value,
      shippingPhone: shippingPhone.value,
      cartItems: cart.value.map(item => ({
        productId: item.id,           // 商品ID
        quantity: item.quantity,      // 数量
        price: item.price            // 价格
      }))
    };

    console.log("=== 前端提交数据 ===");
    console.log("订单数据:", JSON.stringify(orderData, null, 2));

    // 使用封装好的API提交订单
    const response = await submitOrder(orderData);
    console.log("订单提交响应:", response);
    
    // 修改判断逻辑：直接检查订单ID是否存在，而不是检查code
    if (response && response.orderId) {
      showMessage("订单创建成功！", "success");
      
      // 清空本地购物车
      localStorage.removeItem("cart");
      cart.value = [];
      showAddressForm.value = false;
      
      // 跳转到订单列表页
      setTimeout(() => {
        router.push("/customer/orders");
      }, 1500);
    } else {
      throw new Error(response.message || "订单提交失败");
    }

  } catch (error) {
    console.error("订单提交完整错误:", error);
    console.error("错误响应数据:", error.response?.data);
    console.error("错误状态码:", error.response?.status);
    
    // 显示更详细的错误信息
    let errorMsg = "订单提交失败，请重试";
    if (error.response?.data?.message) {
      errorMsg = error.response.data.message;
    } else if (error.message) {
      errorMsg = error.message;
    }
    showMessage(errorMsg, "error");
  } finally {
    submitting.value = false;
  }
};

// 显示消息
const showMessage = (msg, type = "info") => {
  message.value = msg;
  messageType.value = type;
  setTimeout(() => {
    message.value = "";
  }, 3000);
};

// 生命周期
onMounted(() => {
  loadCart();
});
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

/* 页面头部样式 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 0 10px;
}

.page-header h2 {
  font-size: 28px;
  color: #333;
  margin: 0;
}

.back-btn, .home-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(108, 117, 125, 0.2);
}

.back-btn:hover, .home-btn:hover {
  background: #5a6268;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
}

.empty-cart {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-cart p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}

.back-to-shopping {
  display: inline-block;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.back-to-shopping:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(106, 17, 203, 0.3);
}

.sb-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
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

.product-image-cell {
  width: 80px;
}

.product-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
}

.product-name {
  font-weight: 500;
  color: #333;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.2s ease;
}

.quantity-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #6a11cb;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity {
  min-width: 40px;
  text-align: center;
  font-weight: 500;
}

.remove-btn {
  background: #e74c3c;
  color: #fff;
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.remove-btn:hover {
  background: #c0392b;
}

.checkout-section {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 40px;
  align-items: start;
}

.address-form {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.address-form h3 {
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.address-input,
.phone-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s ease;
}

.address-input:focus,
.phone-input:focus {
  outline: none;
  border-color: #6a11cb;
}

.total-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 20px;
}

.total-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f1f1;
}

.total-line:last-child {
  border-bottom: none;
}

.grand-total {
  font-size: 18px;
  font-weight: 600;
  color: #e53e3e;
  margin-top: 8px;
}

.checkout-btn,
.confirm-btn,
.cancel-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 16px;
}

.checkout-btn,
.confirm-btn {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
}

.checkout-btn:hover,
.confirm-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(106, 17, 203, 0.3);
}

.confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #5a6268;
}

.checkout-actions {
  display: flex;
  gap: 12px;
}

.checkout-actions button {
  flex: 1;
}

.message {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  z-index: 1000;
  animation: slideDown 0.3s ease;
}

.message.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

@keyframes slideDown {
  from {
    transform: translateX(-50%) translateY(-100%);
    opacity: 0;
  }

  to {
    transform: translateX(-50%) translateY(0);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .checkout-section {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .sb-table {
    font-size: 14px;
  }

  .sb-table th,
  .sb-table td {
    padding: 12px 8px;
  }

  .quantity-controls {
    flex-direction: column;
    gap: 4px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: center;
  }
  
  .page-header h2 {
    order: -1;
  }
}

@media (max-width: 480px) {
  .page-container {
    padding: 16px;
  }

  .checkout-actions {
    flex-direction: column;
  }
}
</style>