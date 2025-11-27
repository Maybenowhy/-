<template>
  <div class="user-profile">
    <!-- 页面头部 -->
    <div class="profile-header">
      <h1>个人资料</h1>
      <p>管理您的账户信息和偏好设置</p>
    </div>

    <div class="profile-content">
      <!-- 左侧 - 导航菜单 -->
      <div class="profile-sidebar">
        <nav class="sidebar-nav">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            class="nav-item"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            <i class="nav-icon">{{ tab.icon }}</i>
            <span class="nav-text">{{ tab.name }}</span>
          </button>
        </nav>
      </div>

      <!-- 右侧 - 内容区域 -->
      <div class="profile-main">
        <!-- 基本信息 -->
        <div v-if="activeTab === 'basic'" class="tab-content">
          <div class="section-card">
            <div class="section-header">
              <h3>基本信息</h3>
              <button class="edit-btn" @click="editMode.basic = !editMode.basic">
                {{ editMode.basic ? '取消' : '编辑' }}
              </button>
            </div>

            <div class="avatar-section">
              <div class="avatar-upload">
                <div class="avatar-preview">
                  <img :src="userInfo.avatar || '/default-avatar.png'" alt="用户头像" />
                  <div v-if="editMode.basic" class="avatar-overlay" @click="triggerAvatarUpload">
                    <i class="upload-icon">📷</i>
                    <span>更换头像</span>
                  </div>
                </div>
                <input 
                  ref="avatarInput"
                  type="file" 
                  accept="image/*" 
                  @change="handleAvatarUpload"
                  style="display: none"
                />
              </div>
              <div class="avatar-info">
                <h4>{{ userInfo.username }}</h4>
                <p>会员ID: {{ userInfo.id }}</p>
                <p>注册时间: {{ formatDate(userInfo.createTime) }}</p>
              </div>
            </div>

            <form v-if="editMode.basic" class="info-form" @submit.prevent="saveBasicInfo">
              <div class="form-grid">
                <div class="form-group">
                  <label for="username">用户名</label>
                  <input
                    id="username"
                    v-model="editForm.username"
                    type="text"
                    placeholder="请输入用户名"
                  />
                </div>
                <div class="form-group">
                  <label for="email">邮箱</label>
                  <input
                    id="email"
                    v-model="editForm.email"
                    type="email"
                    placeholder="请输入邮箱地址"
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
                <div class="form-group">
                  <label for="birthday">生日</label>
                  <input
                    id="birthday"
                    v-model="editForm.birthday"
                    type="date"
                  />
                </div>
              </div>
              <div class="form-actions">
                <button type="button" class="btn btn-secondary" @click="cancelEdit('basic')">
                  取消
                </button>
                <button type="submit" class="btn btn-primary">
                  保存更改
                </button>
              </div>
            </form>
            <div v-else class="info-display">
              <div class="info-grid">
                <div class="info-item">
                  <label>用户名</label>
                  <span>{{ userInfo.username }}</span>
                </div>
                <div class="info-item">
                  <label>邮箱</label>
                  <span>{{ userInfo.email || '未设置' }}</span>
                </div>
                <div class="info-item">
                  <label>手机号</label>
                  <span>{{ userInfo.phone || '未设置' }}</span>
                </div>
                <div class="info-item">
                  <label>生日</label>
                  <span>{{ userInfo.birthday ? formatDate(userInfo.birthday) : '未设置' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 安全设置 -->
        <div v-if="activeTab === 'security'" class="tab-content">
          <div class="section-card">
            <div class="section-header">
              <h3>安全设置</h3>
            </div>
            
            <div class="security-items">
              <div class="security-item">
                <div class="security-info">
                  <h4>登录密码</h4>
                  <p>定期更改密码有助于保护账户安全</p>
                </div>
                <button class="btn btn-outline" @click="showChangePassword = true">
                  修改密码
                </button>
              </div>
              
              <div class="security-item">
                <div class="security-info">
                  <h4>登录设备</h4>
                  <p>最近登录的设备信息</p>
                </div>
                <button class="btn btn-outline" @click="showDevices = true">
                  查看设备
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 偏好设置 -->
        <div v-if="activeTab === 'preferences'" class="tab-content">
          <div class="section-card">
            <div class="section-header">
              <h3>偏好设置</h3>
              <button class="edit-btn" @click="editMode.preferences = !editMode.preferences">
                {{ editMode.preferences ? '取消' : '编辑' }}
              </button>
            </div>

            <form v-if="editMode.preferences" class="preferences-form" @submit.prevent="savePreferences">
              <div class="preference-group">
                <h4>通知设置</h4>
                <div class="preference-items">
                  <label class="preference-item">
                    <input type="checkbox" v-model="preferences.emailNotifications" />
                    <span class="checkmark"></span>
                    <span>邮件通知</span>
                  </label>
                  <label class="preference-item">
                    <input type="checkbox" v-model="preferences.smsNotifications" />
                    <span class="checkmark"></span>
                    <span>短信通知</span>
                  </label>
                  <label class="preference-item">
                    <input type="checkbox" v-model="preferences.pushNotifications" />
                    <span class="checkmark"></span>
                    <span>推送通知</span>
                  </label>
                </div>
              </div>

              <div class="preference-group">
                <h4>隐私设置</h4>
                <div class="preference-items">
                  <label class="preference-item">
                    <input type="checkbox" v-model="preferences.showEmail" />
                    <span class="checkmark"></span>
                    <span>公开邮箱</span>
                  </label>
                  <label class="preference-item">
                    <input type="checkbox" v-model="preferences.showPhone" />
                    <span class="checkmark"></span>
                    <span>公开手机号</span>
                  </label>
                </div>
              </div>

              <div class="form-actions">
                <button type="button" class="btn btn-secondary" @click="cancelEdit('preferences')">
                  取消
                </button>
                <button type="submit" class="btn btn-primary">
                  保存设置
                </button>
              </div>
            </form>

            <div v-else class="preferences-display">
              <div class="preference-group">
                <h4>通知设置</h4>
                <div class="preference-status">
                  <span>邮件通知: {{ preferences.emailNotifications ? '开启' : '关闭' }}</span>
                  <span>短信通知: {{ preferences.smsNotifications ? '开启' : '关闭' }}</span>
                  <span>推送通知: {{ preferences.pushNotifications ? '开启' : '关闭' }}</span>
                </div>
              </div>
              <div class="preference-group">
                <h4>隐私设置</h4>
                <div class="preference-status">
                  <span>公开邮箱: {{ preferences.showEmail ? '是' : '否' }}</span>
                  <span>公开手机号: {{ preferences.showPhone ? '是' : '否' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 账户管理 -->
        <div v-if="activeTab === 'account'" class="tab-content">
          <div class="section-card">
            <div class="section-header">
              <h3>账户管理</h3>
            </div>

            <div class="account-actions">
              <div class="account-item danger">
                <div class="account-info">
                  <h4>注销账户</h4>
                  <p>永久删除您的账户和所有相关数据</p>
                </div>
                <button class="btn btn-danger" @click="showDeleteConfirm = true">
                  注销账户
                </button>
              </div>

              <div class="account-item">
                <div class="account-info">
                  <h4>数据导出</h4>
                  <p>导出您的个人数据和订单记录</p>
                </div>
                <button class="btn btn-outline" @click="exportData">
                  导出数据
                </button>
              </div>
            </div>
          </div>
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

    <!-- 注销确认模态框 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>注销账户</h3>
          <button class="close-btn" @click="showDeleteConfirm = false">×</button>
        </div>
        <div class="modal-body">
          <div class="warning-message">
            <i class="warning-icon">⚠️</i>
            <h4>此操作不可撤销</h4>
            <p>您确定要注销账户吗？这将永久删除您的所有数据，包括订单记录和个人信息。</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showDeleteConfirm = false">
              取消
            </button>
            <button type="button" class="btn btn-danger" @click="deleteAccount">
              确认注销
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 标签页配置
const tabs = [
  { id: 'basic', name: '基本信息', icon: '👤' },
  { id: 'security', name: '安全设置', icon: '🔒' },
  { id: 'preferences', name: '偏好设置', icon: '⚙️' },
  { id: 'account', name: '账户管理', icon: '📋' }
]

const activeTab = ref('basic')
const showChangePassword = ref(false)
const showDeleteConfirm = ref(false)
const avatarInput = ref(null)

// 编辑模式状态
const editMode = reactive({
  basic: false,
  preferences: false
})

// 用户信息
const userInfo = reactive({
  id: '10001',
  username: '星潮用户',
  email: 'user@example.com',
  phone: '138****8888',
  birthday: '1990-01-01',
  avatar: '',
  createTime: '2024-01-01'
})

// 编辑表单
const editForm = reactive({
  username: '',
  email: '',
  phone: '',
  birthday: ''
})

// 偏好设置
const preferences = reactive({
  emailNotifications: true,
  smsNotifications: false,
  pushNotifications: true,
  showEmail: false,
  showPhone: false
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
    phone: userInfo.phone,
    birthday: userInfo.birthday
  })
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 处理头像上传
const handleAvatarUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      userInfo.avatar = e.target.result
      // 这里可以调用API上传头像
      console.log('上传头像:', file.name)
    }
    reader.readAsDataURL(file)
  }
}

// 保存基本信息
const saveBasicInfo = async () => {
  try {
    // 调用API更新用户信息
    // await updateUserInfo(editForm)
    Object.assign(userInfo, editForm)
    editMode.basic = false
    showMessage('基本信息更新成功')
  } catch (error) {
    console.error('保存失败:', error)
    showMessage('保存失败，请重试', 'error')
  }
}

// 保存偏好设置
const savePreferences = async () => {
  try {
    // 调用API保存偏好设置
    // await saveUserPreferences(preferences)
    editMode.preferences = false
    showMessage('偏好设置更新成功')
  } catch (error) {
    console.error('保存失败:', error)
    showMessage('保存失败，请重试', 'error')
  }
}

// 取消编辑
const cancelEdit = (type) => {
  editMode[type] = false
  if (type === 'basic') {
    initEditForm()
  }
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

// 导出数据
const exportData = async () => {
  try {
    // 调用API导出数据
    showMessage('数据导出请求已提交，请查收邮箱')
  } catch (error) {
    console.error('导出失败:', error)
    showMessage('导出失败，请重试', 'error')
  }
}

// 注销账户
const deleteAccount = async () => {
  try {
    // 调用API注销账户
    // await deleteAccountApi()
    showDeleteConfirm.value = false
    showMessage('账户已注销')
    
    // 清除本地存储并跳转到首页
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('username')
    router.push('/')
  } catch (error) {
    console.error('注销失败:', error)
    showMessage('注销失败，请重试', 'error')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 显示消息提示
const showMessage = (message, type = 'success') => {
  // 这里可以实现一个消息提示组件
  alert(message)
}

onMounted(() => {
  // 加载用户信息
  // loadUserInfo()
  initEditForm()
})
</script>

<style scoped>
.user-profile {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
}

.profile-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.profile-header p {
  font-size: 16px;
  color: #6c757d;
  margin: 0;
}

.profile-content {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 30px;
}

/* 侧边栏样式 */
.profile-sidebar {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  padding: 20px 0;
  height: fit-content;
  position: sticky;
  top: 100px;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #6c757d;
  font-size: 14px;
}

.nav-item:hover {
  background: #f8f9fa;
  color: #495057;
}

.nav-item.active {
  background: #e3f2fd;
  color: #1976d2;
  border-right: 3px solid #1976d2;
}

.nav-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.nav-text {
  font-weight: 500;
}

/* 主内容区样式 */
.profile-main {
  min-height: 600px;
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.section-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  padding: 30px;
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.section-header h3 {
  font-size: 20px;
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

/* 头像区域 */
.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 30px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f8f9fa;
}

.avatar-upload {
  position: relative;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  border: 3px solid #f8f9fa;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-preview:hover .avatar-overlay {
  opacity: 1;
}

.upload-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.avatar-overlay span {
  font-size: 12px;
}

.avatar-info h4 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #1a1a1a;
}

.avatar-info p {
  margin: 4px 0;
  color: #6c757d;
  font-size: 14px;
}

/* 表单样式 */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
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
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f8f9fa;
}

/* 信息显示样式 */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-size: 14px;
  color: #6c757d;
  font-weight: 500;
}

.info-item span {
  font-size: 16px;
  color: #1a1a1a;
  font-weight: 500;
}

/* 安全设置 */
.security-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.security-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.security-info p {
  margin: 0;
  font-size: 14px;
  color: #6c757d;
}

/* 偏好设置 */
.preferences-form {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.preference-group h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #1a1a1a;
}

.preference-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preference-item {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  font-size: 14px;
  color: #495057;
}

.preference-item input {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 2px solid #dee2e6;
  border-radius: 4px;
  position: relative;
  transition: all 0.3s ease;
}

.preference-item input:checked + .checkmark {
  background: #3498db;
  border-color: #3498db;
}

.preference-item input:checked + .checkmark::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.preference-status {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.preference-status span {
  font-size: 14px;
  color: #495057;
}

/* 账户管理 */
.account-actions {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.account-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.account-item.danger {
  background: #fff5f5;
  border: 1px solid #fed7d7;
}

.account-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.account-info p {
  margin: 0;
  font-size: 14px;
  color: #6c757d;
}

.account-item.danger .account-info h4 {
  color: #e53e3e;
}

.account-item.danger .account-info p {
  color: #c53030;
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
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
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

.btn-outline {
  background: transparent;
  border: 1px solid #dee2e6;
  color: #495057;
}

.btn-outline:hover {
  background: #f8f9fa;
  border-color: #adb5bd;
}

.btn-danger {
  background: #e53e3e;
  color: white;
}

.btn-danger:hover {
  background: #c53030;
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

.warning-message {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

.warning-message h4 {
  font-size: 18px;
  color: #e53e3e;
  margin: 0 0 12px 0;
}

.warning-message p {
  color: #6c757d;
  line-height: 1.5;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
  }
  
  .profile-sidebar {
    position: static;
  }
  
  .sidebar-nav {
    flex-direction: row;
    overflow-x: auto;
    padding: 0 20px;
  }
  
  .nav-item {
    flex-direction: column;
    gap: 8px;
    padding: 12px 16px;
    min-width: 80px;
  }
  
  .nav-text {
    font-size: 12px;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .security-item,
  .account-item {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
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
  .user-profile {
    padding: 16px;
  }
  
  .section-card {
    padding: 20px;
  }
  
  .modal-body {
    padding: 20px;
  }
}
</style>