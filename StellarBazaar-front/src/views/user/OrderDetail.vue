<template>
    <div class="order-detail-page">
        <div class="page-header">
            <h2>订单详情</h2>
            <button class="back-btn" @click="goBack">← 返回订单列表</button>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
            <div class="spinner"></div>
            <p>正在加载订单详情...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
            <div class="error-icon">❌</div>
            <h3>加载失败</h3>
            <p>{{ error }}</p>
            <button class="retry-btn" @click="loadOrderDetail">重试</button>
        </div>

        <!-- 订单详情 -->
        <div v-else-if="order" class="order-detail">
            <div class="order-layout">
                <!-- 左侧：订单信息和收货信息 -->
                <div class="order-left">
                    <!-- 订单基本信息 -->
                    <div class="order-card">
                        <div class="order-header">
                            <h3>订单信息</h3>
                            <span class="order-status" :class="getStatusClass(order.status)">
                                {{ getStatusText(order.status) }}
                            </span>
                        </div>

                        <div class="order-info">
                            <div class="info-row">
                                <span class="label">订单号：</span>
                                <span class="value">{{ order.orderNumber }}</span>
                            </div>
                            <div class="info-row">
                                <span class="label">下单时间：</span>
                                <span class="value">{{ formatDate(order.createdAt) }}</span>
                            </div>
                            <div class="info-row">
                                <span class="label">支付时间：</span>
                                <span class="value">{{ order.paymentTime ? formatDate(order.paymentTime) : '未支付'
                                }}</span>
                            </div>
                            <div class="info-row">
                                <span class="label">总金额：</span>
                                <span class="value price">￥{{ order.totalAmount }}</span>
                            </div>
                        </div>

                        <!-- 收货信息 -->
                        <div class="shipping-info">
                            <h4>收货信息</h4>
                            <div class="info-row">
                                <span class="label">收货地址：</span>
                                <span class="value">{{ order.shippingAddress }}</span>
                            </div>
                            <div class="info-row">
                                <span class="label">联系电话：</span>
                                <span class="value">{{ order.shippingPhone }}</span>
                            </div>
                        </div>
                    </div>

                    <!-- 操作按钮 -->
                    <div class="action-buttons">
                        <button v-if="order.status === 'pending_payment'" class="pay-btn" @click="handlePay"
                            :disabled="paying">
                            {{ paying ? '支付中...' : '立即支付' }}
                        </button>

                        <button v-if="order.status === 'paid'" class="confirm-btn" @click="handleConfirm"
                            :disabled="confirming">
                            {{ confirming ? '确认中...' : '确认收货' }}
                        </button>

                        <button v-if="order.status === 'delivered'" class="review-btn" @click="handleReview">
                            评价订单
                        </button>
                    </div>
                </div>

                <!-- 右侧：商品列表 -->
                <div class="order-right">
                    <div class="products-card">
                        <h3>商品列表</h3>
                        <div class="product-list">
                            <div v-for="item in order.items" :key="item.id" class="product-item">
                                <img :src="item.productImage || '/placeholder-image.jpg'" :alt="item.productName"
                                    class="product-image" />
                                <div class="product-info">
                                    <h4 class="product-name">{{ item.productName || `商品 ${item.productId}` }}</h4>
                                    <p class="product-spec">单价：￥{{ item.unitPrice }} × {{ item.quantity }}</p>
                                </div>
                                <div class="product-subtotal">
                                    ￥{{ item.subtotal }}
                                </div>
                            </div>
                        </div>

                        <div class="order-total">
                            <span class="total-label">订单总额：</span>
                            <span class="total-price">￥{{ order.totalAmount }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 支付确认对话框 -->
        <div v-if="showPayDialog" class="dialog-overlay">
            <div class="pay-dialog">
                <h3>确认支付</h3>
                <p>订单号：{{ order.orderNumber }}</p>
                <p class="pay-amount">支付金额：<span>￥{{ order.totalAmount }}</span></p>
                <div class="dialog-actions">
                    <button class="cancel-btn" @click="showPayDialog = false">取消</button>
                    <button class="confirm-pay-btn" @click="confirmPay">确认支付</button>
                </div>
            </div>
        </div>

        <!-- 消息提示 -->
        <div v-if="message" class="message"
            :class="{ success: messageType === 'success', error: messageType === 'error' }">
            {{ message }}
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getOrder, payOrder, confirmOrder } from "@/api/order";
import { logUserPurchase } from '@/api/userLog';

const route = useRoute();
const router = useRouter();

// 响应式数据
const order = ref(null);
const loading = ref(true);
const error = ref("");
const paying = ref(false);
const confirming = ref(false);
const showPayDialog = ref(false);
const message = ref("");
const messageType = ref("");



// 加载订单详情
const loadOrderDetail = async () => {
    try {
        loading.value = true;
        error.value = "";

        const orderId = route.params.id;
        console.log("加载订单详情，订单ID:", orderId);

        const response = await getOrder(orderId);
        console.log("订单详情响应:", response);

        if (response && response.id) {
            order.value = response;
        } else {
            throw new Error("订单不存在");
        }

    } catch (err) {
        console.error("加载订单详情失败:", err);
        error.value = "加载订单详情失败，请稍后重试";
    } finally {
        loading.value = false;
    }
};


// 状态样式
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

// 图片加载失败处理
const handleImageError = (event) => {
    event.target.src = '/placeholder-image.jpg';
};

// 状态文本
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

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
};

// 处理支付
const handlePay = () => {
    showPayDialog.value = true;
};

// 确认支付
const confirmPay = async () => {
    try {
        paying.value = true;
        showPayDialog.value = false;

        // 直接调用 API，不检查响应
        await payOrder(order.value.id);

        // 总是显示成功
        showMessage("支付成功！", "success");

        // 记录购买日志
        try {
            const userId = localStorage.getItem('userId') || 3; // 默认用户ID
            await logUserPurchase({
                userId: parseInt(userId),
                orderId: order.value.id
                // 移除了 ipAddress 字段
            });
            console.log('购买日志记录成功');
        } catch (error) {
            console.error('记录购买日志失败:', error);
            // 记录失败不影响主要支付流程
        }

        // 重新加载订单详情
        setTimeout(() => {
            loadOrderDetail();
        }, 1000);

    } catch (err) {
        console.error("支付失败:", err);
        showMessage(err.message || "支付失败，请重试", "error");
    } finally {
        paying.value = false;
    }
};


// 确认收货
const handleConfirm = async () => {
    try {
        confirming.value = true;

        // 直接调用 API，不检查响应
        await confirmOrder(order.value.id);

        // 总是显示成功
        showMessage("已通过发送电子邮件确认收货成功！", "success");

        // 重新加载订单详情
        setTimeout(() => {
            loadOrderDetail();
        }, 1000);

    } catch (err) {
        console.error("确认收货失败:", err);
        showMessage(err.message || "确认收货失败，请重试", "error");
    } finally {
        confirming.value = false;
    }
};

// 评价订单
const handleReview = () => {
    showMessage("评价功能开发中...", "info");
};

// 返回上一页
const goBack = () => {
    router.back();
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
    loadOrderDetail();
});
</script>

<style scoped>
.order-detail-page {
    min-height: 100vh;
    background: #f8f9fa;
    padding: 24px;
}

.page-header {
    max-width: 1200px;
    margin: 0 auto 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.page-header h2 {
    font-size: 32px;
    color: #2d3748;
    margin: 0;
    font-weight: 700;
}

.back-btn {
    background: #6c757d;
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(108, 117, 125, 0.2);
}

.back-btn:hover {
    background: #5a6268;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
}

/* 加载状态 */
.loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 100px 20px;
    max-width: 1200px;
    margin: 0 auto;
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

/* 错误状态 */
.error-state {
    text-align: center;
    padding: 80px 20px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
    border: 1px solid #fed7d7;
    max-width: 600px;
    margin: 0 auto;
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
    padding: 12px 24px;
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.3s ease;
}

.retry-btn:hover {
    background: #c53030;
    transform: translateY(-1px);
}

/* 订单详情布局 */
.order-detail {
    max-width: 1200px;
    margin: 0 auto;
}

.order-layout {
    display: grid;
    grid-template-columns: 1fr 400px;
    gap: 24px;
    align-items: start;
}

/* 左侧区域 */
.order-left {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

/* 右侧区域 */
.order-right {
    position: sticky;
    top: 24px;
}

/* 订单卡片 */
.order-card,
.products-card {
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    padding: 32px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.order-card:hover,
.products-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 20px;
    border-bottom: 2px solid #f1f1f1;
}

.order-header h3 {
    margin: 0;
    color: #2d3748;
    font-size: 22px;
    font-weight: 600;
}

.order-status {
    padding: 8px 16px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.order-status.pending {
    background: #fff3cd;
    color: #856404;
    border: 1px solid #ffeaa7;
}

.order-status.paid {
    background: #d1ecf1;
    color: #0c5460;
    border: 1px solid #bee5eb;
}

.order-status.delivered {
    background: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
}

.order-status.cancelled {
    background: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
}

.info-row {
    display: flex;
    margin-bottom: 16px;
    align-items: flex-start;
}

.info-row .label {
    width: 100px;
    color: #718096;
    font-weight: 500;
    font-size: 14px;
    flex-shrink: 0;
}

.info-row .value {
    flex: 1;
    color: #2d3748;
    font-size: 15px;
    line-height: 1.5;
}

.info-row .price {
    color: #e53e3e;
    font-weight: 700;
    font-size: 20px;
}

.shipping-info {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 2px solid #f1f1f1;
}

.shipping-info h4 {
    margin: 0 0 20px 0;
    color: #2d3748;
    font-size: 18px;
    font-weight: 600;
}

/* 商品列表 */
.products-card h3 {
    margin: 0 0 24px 0;
    color: #2d3748;
    font-size: 22px;
    font-weight: 600;
    padding-bottom: 20px;
    border-bottom: 2px solid #f1f1f1;
}

.product-list {
    margin-bottom: 24px;
}

.product-item {
    display: flex;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid #f5f5f5;
    transition: background-color 0.2s ease;
}

.product-item:hover {
    background-color: #f8f9fa;
    border-radius: 8px;
    margin: 0 -16px;
    padding: 20px 16px;
}

.product-item:last-child {
    border-bottom: none;
}

.product-image {
    width: 70px;
    height: 70px;
    object-fit: cover;
    border-radius: 12px;
    margin-right: 20px;
    border: 1px solid #e9ecef;
}

.product-info {
    flex: 1;
}

.product-name {
    margin: 0 0 8px 0;
    color: #2d3748;
    font-size: 16px;
    font-weight: 600;
    line-height: 1.4;
}

.product-spec {
    margin: 0;
    color: #718096;
    font-size: 14px;
}

.product-subtotal {
    font-size: 16px;
    font-weight: 700;
    color: #e53e3e;
    min-width: 80px;
    text-align: right;
}

.order-total {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 20px;
    border-top: 2px solid #f1f1f1;
    font-size: 18px;
}

.total-label {
    color: #2d3748;
    font-weight: 600;
}

.total-price {
    color: #e53e3e;
    font-weight: 700;
    font-size: 28px;
}

/* 操作按钮 */
.action-buttons {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.pay-btn,
.confirm-btn,
.review-btn {
    padding: 16px 24px;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: center;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.pay-btn {
    background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
    color: white;
}

.pay-btn:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 8px 20px rgba(106, 17, 203, 0.4);
}

.confirm-btn {
    background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
    color: white;
}

.confirm-btn:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 8px 20px rgba(40, 167, 69, 0.4);
}

.review-btn {
    background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
    color: white;
}

.review-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 20px rgba(255, 193, 7, 0.4);
}

.pay-btn:disabled,
.confirm-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
}

/* 支付对话框 */
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

.pay-dialog {
    background: white;
    border-radius: 20px;
    padding: 32px;
    width: 450px;
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

.pay-dialog h3 {
    margin: 0 0 20px 0;
    color: #2d3748;
    font-size: 24px;
    font-weight: 600;
    text-align: center;
}

.pay-dialog p {
    margin: 12px 0;
    color: #718096;
    font-size: 15px;
    text-align: center;
}

.pay-amount {
    font-size: 20px;
    font-weight: 600;
    margin: 24px 0;
}

.pay-amount span {
    color: #e53e3e;
    font-size: 32px;
    font-weight: 700;
}

.dialog-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
    margin-top: 32px;
}

.cancel-btn,
.confirm-pay-btn {
    padding: 14px 28px;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    font-size: 15px;
    font-weight: 600;
    transition: all 0.3s ease;
    min-width: 120px;
}

.cancel-btn {
    background: #6c757d;
    color: white;
}

.cancel-btn:hover {
    background: #5a6268;
    transform: translateY(-2px);
}

.confirm-pay-btn {
    background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
    color: white;
}

.confirm-pay-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(40, 167, 69, 0.4);
}

/* 消息提示 */
.message {
    position: fixed;
    top: 24px;
    left: 50%;
    transform: translateX(-50%);
    padding: 16px 32px;
    border-radius: 12px;
    font-weight: 600;
    z-index: 1000;
    animation: messageSlideIn 0.4s ease;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

@keyframes messageSlideIn {
    from {
        transform: translateX(-50%) translateY(-100%);
        opacity: 0;
    }

    to {
        transform: translateX(-50%) translateY(0);
        opacity: 1;
    }
}

.message.success {
    background: #d4edda;
    color: #155724;
    border: 2px solid #c3e6cb;
}

.message.error {
    background: #f8d7da;
    color: #721c24;
    border: 2px solid #f5c6cb;
}

.message.info {
    background: #cce7ff;
    color: #004085;
    border: 2px solid #b3d7ff;
}

/* 响应式设计 */
@media (max-width: 1024px) {
    .order-layout {
        grid-template-columns: 1fr;
        gap: 20px;
    }

    .order-right {
        position: static;
    }
}

@media (max-width: 768px) {
    .order-detail-page {
        padding: 16px;
    }

    .page-header {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }

    .page-header h2 {
        font-size: 28px;
    }

    .order-card,
    .products-card {
        padding: 24px;
    }

    .info-row {
        flex-direction: column;
        gap: 8px;
    }

    .info-row .label {
        width: auto;
    }

    .product-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
        text-align: left;
    }

    .product-info {
        width: 100%;
    }

    .product-subtotal {
        align-self: flex-end;
        text-align: right;
    }

    .action-buttons {
        gap: 12px;
    }

    .pay-btn,
    .confirm-btn,
    .review-btn {
        padding: 14px 20px;
    }

    .pay-dialog {
        padding: 24px;
    }

    .dialog-actions {
        flex-direction: column;
    }

    .cancel-btn,
    .confirm-pay-btn {
        width: 100%;
    }
}

@media (max-width: 480px) {

    .order-card,
    .products-card {
        padding: 20px;
    }

    .order-header {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }

    .page-header h2 {
        font-size: 24px;
    }
}
</style>