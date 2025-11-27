<template>
  <div class="user-info">
    <div class="info-header">
      <h1>账户信息</h1>
      <p>查看和管理您的基本账户信息</p>
    </div>

    <div class="info-content">
      <div class="info-card">
        <div class="card-header">
          <h3>基本信息</h3>
          <button class="edit-btn" @click="editMode = !editMode">
            {{ editMode ? '取消' : '编辑' }}
          </button>
        </div>

        <form v-if="editMode" class="info-form" @submit.prevent="saveInfo">
          <div class="form-group">
            <label for="username">用户名</label>
            <input
              id="username"
              v-model="editForm.username"
              type="text"
              placeholder="请输入用户名"
              required
            />
          </div>
          
          <div class="form-group">
            <label for="email">邮箱</label>
            <input
              id="email"
              v-model="editForm.email"
              type="email"
              placeholder="请输入邮箱"
              required
            />
          </div>
          
          <div class="form-group">
            <label for="phone">手机号</label>
            <input
              id="phone"
              v-model="editForm.phone"
              type="tel"
              placeholder="请输入手机号"
            />
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-secondary" @click="cancelEdit">
              取消
            </button>
            <button type="submit" class="btn btn-primary">
              保存
            </button>
          </div>
        </form>

        <div v-else class="info-display">
          <div class="info-item">
            <span class="label">用户ID</span>
            <span class="value">{{ userInfo.id }}</span>
          </div>
          <div class="info-item">
            <span class="label">用户名</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱</span>
            <span class="value">{{ userInfo.email }}</span>
          </div>
          <div class="info-item">
            <span class="label">角色</span>
            <span class="value role-badge" :class="userInfo.role">
              {{ userInfo.role === 'customer' ? '顾客' : '销售员' }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">手机号</span>
            <span class="value">{{ userInfo.phone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">注册时间</span>
            <span class="value">{{ formatDate(userInfo.createdAt) }}</span>
          </div>
          <div class="info-item">
            <span class="label">最后登录</span>
            <span class="value">{{ formatDate(userInfo.lastLogin) }}</span>
          </div>
        </div>
      </div>

      <!-- 安全操作 -->
      <div class="action-card">
        <h3>安全操作</h3>
        <div class="action-items">
          <button class="action-btn change-password" @click="showChangePassword = true">
            <i class="btn-icon">🔒</i>
            <span>修改密码</span>
          </button>
          <button class="action-btn logout" @click="handleLogout">
            <i class="btn-icon">🚪</i>
            <span>退出登录</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 修改密码模态框 -->
    <div v-if="showChangePassword" class="modal-overlay" @click.self="showChangePassword = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>修改密码</h3>
          <button class="close-btn" @click="showChangePassword = false">×</button>
        </div>
        <form class="modal-body" @submit.prevent="changePassword">
          <div class="form-group">
            <label for="currentPassword">当前密码</label>
            <input
              id="currentPassword"
              v-model="passwordForm.currentPassword"
              type="password"
              placeholder="请输入当前密码"
              required
            />
          </div>
          <div class="form-group">
            <label for="newPassword">新密码</label>
            <input
              id="newPassword"
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              required
            />
          </div>
          <div class="form-group">
            <label for="confirmPassword">确认新密码</label>
            <input
              id="confirmPassword"
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              required
            />
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showChangePassword = false">
              取消
            </button>
            <button type="submit" class="btn btn-primary">
              确认修改
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userLogout } from '@/api/user'

const router = useRouter()

// 状态
const editMode = ref(false)
const showChangePassword = ref(false)

// 用户信息
const userInfo = reactive({
  id: '',
  username: '',
  email: '',
  phone: '',
  role: '',
  createdAt: '',
  lastLogin: ''
})

// 编辑表单
const editForm = reactive({
  username: '',
  email: '',
  phone: ''
})

// 密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 初始化编辑表单
const initEditForm = () => {
  Object.assign(editForm, {
    username: userInfo.username,
    email: userInfo.email,
    phone: userInfo.phone
  })
}

// 保存信息
const saveInfo = async () => {
  try {
    // 调用API更新用户信息
    // await updateUserInfo(editForm)
    Object.assign(userInfo, editForm)
    editMode.value = false
    showMessage('信息更新成功')
  } catch (error) {
    console.error('保存失败:', error)
    showMessage('保存失败，请重试', 'error')
  }
}

// 取消编辑
const cancelEdit = () => {
  editMode.value = false
  initEditForm()
}

// 修改密码
const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    showMessage('两次输入的密码不一致', 'error')
    return
  }

  try {
    // 调用API修改密码
    // await changePasswordApi(passwordForm)
    showChangePassword.value = false
    showMessage('密码修改成功')
    // 清空表单
    Object.keys(passwordForm).forEach(key => {
      passwordForm[key] = ''
    })
  } catch (error) {
    console.error('修改密码失败:', error)
    showMessage('修改密码失败，请重试', 'error')
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await userLogout()
    showMessage('退出登录成功')
  } catch (error) {
    console.error('退出登录失败:', error)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('username')
    router.push('/login')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '从未登录'
  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 显示消息提示
const showMessage = (message, type = 'success') => {
  // 这里可以实现一个消息提示组件
  alert(message)
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    // 调用API获取用户信息
    // const response = await getUserInfo()
    // Object.assign(userInfo, response.data)
    
    // 模拟数据
    Object.assign(userInfo, {
      id: '10001',
      username: '星潮用户',
      email: 'user@example.com',
      phone: '13800138000',
      role: 'customer',
      createdAt: '2024-01-01T10:00:00',
      lastLogin: '2024-01-20T15:30:00'
    })
    
    initEditForm()
  } catch (error) {
    console.error('加载用户信息失败:', error)
    showMessage('加载用户信息失败', 'error')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.user-info {
  max-width: 600px;
  margin: 0 auto;
  padding: 24px;
}

.info-header {
  text-align: center;
  margin-bottom: 40px;
}

.info-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.info-header p {
  font-size: 16px;
  color: #6c757d;
  margin: 0;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-card, .action-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.edit-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.edit-btn:hover {
  background: #5a6268;
}

/* 表单样式 */
.info-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #495057;
}

.form-group input {
  padding: 10px 12px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f8f9fa;
}

/* 信息显示样式 */
.info-display {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f8f9fa;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-size: 14px;
  color: #6c757d;
  font-weight: 500;
}

.info-item .value {
  font-size: 14px;
  color: #1a1a1a;
  font-weight: 500;
}

.role-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.customer {
  background: #d4edda;
  color: #155724;
}

.role-badge.sales {
  background: #cce7ff;
  color: #004085;
}

/* 安全操作 */
.action-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 20px 0;
}

.action-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #495057;
  width: 100%;
  text-align: left;
}

.action-btn:hover {
  background: #f8f9fa;
  border-color: #3498db;
  transform: translateY(-1px);
}

.action-btn.logout {
  color: #e74c3c;
  border-color: #fed7d7;
}

.action-btn.logout:hover {
  background: #fff5f5;
  border-color: #e74c3c;
}

.btn-icon {
  font-size: 18px;
  width: 20px;
  text-align: center;
}

/* 按钮样式 */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 80px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 480px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6c757d;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.close-btn:hover {
  background: #f8f9fa;
  color: #495057;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #f8f9fa;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-info {
    padding: 16px;
  }
  
  .info-card, .action-card {
    padding: 20px;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .modal-content {
    margin: 0;
    max-height: 100vh;
    border-radius: 0;
  }
}

@media (max-width: 480px) {
  .modal-body {
    padding: 20px;
  }
}
</style>