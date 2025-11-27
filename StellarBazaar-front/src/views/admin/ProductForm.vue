<template>
  <div class="dialog-overlay">
    <div class="product-form-dialog">
      <h3>{{ mode === 'add' ? '添加商品' : '编辑商品' }}</h3>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>商品名称:</label>
          <input 
            v-model="formData.name" 
            type="text" 
            required 
            placeholder="请输入商品名称"
          />
        </div>
        <div class="form-group">
          <label>商品描述:</label>
          <textarea 
            v-model="formData.description" 
            rows="3" 
            placeholder="请输入商品描述"
          ></textarea>
        </div>
        <div class="form-group">
          <label>价格:</label>
          <input 
            v-model.number="formData.price" 
            type="number" 
            required 
            min="0" 
            step="0.01"
            placeholder="请输入价格"
          />
        </div>
        <div class="form-group">
          <label>库存:</label>
          <input 
            v-model.number="formData.stockQuantity" 
            type="number" 
            required 
            min="0"
            placeholder="请输入库存数量"
          />
        </div>
        <div class="form-group">
          <label>商品图片:</label>
          <input 
            v-model="formData.imageUrl" 
            type="text" 
            placeholder="请输入图片URL"
          />
        </div>
        <!-- 移除状态选择，新增商品默认上架 -->
        <div class="dialog-actions">
          <button type="button" class="cancel-btn" @click="handleCancel">取消</button>
          <button type="submit" class="confirm-btn">{{ mode === 'add' ? '添加' : '更新' }}</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  product: Object,
  mode: String // 'add' 或 'edit'
});

const emit = defineEmits(['save', 'cancel']);

const formData = ref({
  name: '',
  description: '',
  price: 0,
  stockQuantity: 0,
  imageUrl: '',
  status: 1 // 新增商品默认上架
});

// 监听product变化，用于编辑模式
watch(() => props.product, (newProduct) => {
  if (newProduct) {
    // 将商品数据映射到表单字段
    formData.value = {
      name: newProduct.name || '',
      description: newProduct.description || '',
      price: newProduct.price || 0,
      stockQuantity: newProduct.stock || newProduct.stockQuantity || 0,
      imageUrl: newProduct.imageUrl || '',
      status: newProduct.status || 1
    };
  } else {
    // 重置表单数据
    formData.value = {
      name: '',
      description: '',
      price: 0,
      stockQuantity: 0,
      imageUrl: '',
      status: 1
    };
  }
}, { immediate: true });

const handleSubmit = () => {
  // 验证必填字段
  if (!formData.value.name.trim()) {
    alert('请输入商品名称');
    return;
  }
  if (formData.value.price <= 0) {
    alert('请输入有效的价格');
    return;
  }
  if (formData.value.stockQuantity < 0) {
    alert('库存不能为负数');
    return;
  }

  console.log('提交表单数据:', formData.value);
  emit('save', formData.value);
};

const handleCancel = () => {
  emit('cancel');
};
</script>

<style scoped>
/* 样式保持不变 */
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

.product-form-dialog {
  background: white;
  border-radius: 16px;
  padding: 32px;
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
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

.product-form-dialog h3 {
  margin: 0 0 24px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #2c3e50;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #6a11cb;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.cancel-btn, .confirm-btn {
  padding: 12px 24px;
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

.confirm-btn {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(106, 17, 203, 0.3);
}
</style>