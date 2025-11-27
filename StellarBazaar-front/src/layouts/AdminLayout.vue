<template>
  <div class="admin-layout" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <i class="logo-icon">🌟</i>
          <span class="logo-text" v-show="!sidebarCollapsed">星潮管理后台</span>
        </div>
        <button class="sidebar-toggle" @click="toggleSidebar">
          <i class="toggle-icon" :class="sidebarCollapsed ? 'icon-expand' : 'icon-collapse'">≡</i>
        </button>
      </div>

      <nav class="sidebar-nav">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path"
          :to="item.path" 
          class="nav-item"
          :class="{ active: $route.path.startsWith(item.path) }"
        >
          <i class="nav-icon">{{ item.icon }}</i>
          <span class="nav-text" v-show="!sidebarCollapsed">{{ item.name }}</span>
          <span v-if="item.badge && !sidebarCollapsed" class="nav-badge">{{ item.badge }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer" v-show="!sidebarCollapsed">
        <div class="system-info">
          <p>系统版本 v1.0.0</p>
          <p>© 2024 星潮市集</p>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="admin-main">
      <!-- 顶部栏 -->
      <header class="admin-header">
        <div class="header-left">
          <div class="breadcrumb">
            <span class="breadcrumb-item">{{ currentBreadcrumb }}</span>
          </div>
        </div>

        <div class="header-right">
          <!-- 用户菜单 -->
          <div class="user-dropdown">
            <button class="user-btn" @click="toggleUserMenu">
              <div class="user-avatar">
                {{ userInitials }}
              </div>
              <span class="user-name" v-show="!sidebarCollapsed">{{ userInfo.name }}</span>
              <i class="user-arrow" :class="{ 'arrow-up': showUserMenu }"></i>
            </button>
            <div v-if="showUserMenu" class="user-menu">
              <div class="user-info">
                <div class="user-avatar-large">
                  {{ userInitials }}
                </div>
                <div class="user-details">
                  <h4>{{ userInfo.name }}</h4>
                  <p>{{ userInfo.role }}</p>
                </div>
              </div>
              <div class="menu-items">
                <div class="menu-divider"></div>
                <button class="menu-item logout" @click="handleLogout">
                  <i class="menu-icon">🚪</i>
                  <span>退出登录</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="admin-content">
        <router-view />
      </main>
    </div>

    <!-- 移动端侧边栏遮罩 -->
    <div 
      v-if="isMobile && sidebarCollapsed" 
      class="sidebar-overlay" 
      @click="sidebarCollapsed = false"
    ></div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { userLogout } from '@/api/user'

const router = useRouter()
const route = useRoute()

// 响应式状态
const sidebarCollapsed = ref(false)
const showUserMenu = ref(false)
const isMobile = ref(false)

// 用户信息 - 从 localStorage 获取
const userInfo = reactive({
  name: localStorage.getItem('username') || '管理员',
  role: localStorage.getItem('role') || '管理员',
  avatar: ''
})

// 菜单配置 - 添加用户日志管理
const menuItems = [
  { path: '/admin/dashboard', name: '数据概览', icon: '📊', badge: null },
  { path: '/admin/product', name: '商品管理', icon: '📦', badge: 5 },
  { path: '/admin/order', name: '订单管理', icon: '📋', badge: 12 },
  { path: '/admin/user', name: '用户管理', icon: '👥', badge: null },
  { path: '/admin/user-log', name: '用户日志', icon: '📝', badge: null }, // 新增的用户日志
  { path: '/admin/report', name: '数据报表', icon: '📈', badge: null }
]

// 计算属性
const currentBreadcrumb = computed(() => {
  const currentMenu = menuItems.find(item => route.path.startsWith(item.path))
  return currentMenu ? currentMenu.name : '管理后台'
})

const userInitials = computed(() => {
  return userInfo.name.substring(0, 2)
})

// 方法
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const handleLogout = async () => {
  try {
    await userLogout()
  } catch (error) {
    console.error('注销失败:', error)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('username')
    router.push('/login')
  }
}

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) {
    sidebarCollapsed.value = true
  }
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event) => {
  if (!event.target.closest('.user-dropdown')) {
    showUserMenu.value = false
  }
}

// 生命周期
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  document.addEventListener('click', handleClickOutside)
  
  // 加载用户信息
  // loadUserInfo()
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f6fa;
  transition: all 0.3s ease;
}

/* 侧边栏样式 */
.admin-sidebar {
  width: 260px;
  background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 1000;
  box-shadow: 2px 0 10px rgba(0,0,0,0.1);
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  font-size: 18px;
}

.logo-icon {
  font-size: 24px;
}

.sidebar-toggle {
  background: rgba(255,255,255,0.1);
  border: none;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.sidebar-toggle:hover {
  background: rgba(255,255,255,0.2);
}

/* 导航菜单 */
.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  color: rgba(255,255,255,0.8);
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: rgba(255,255,255,0.1);
  color: white;
}

.nav-item.active {
  background: rgba(255,255,255,0.15);
  color: white;
  border-left-color: #e74c3c;
}

.nav-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
  flex-shrink: 0;
}

.nav-text {
  font-weight: 500;
  white-space: nowrap;
  transition: opacity 0.3s ease;
}

.nav-badge {
  background: #e74c3c;
  color: white;
  border-radius: 10px;
  padding: 2px 8px;
  font-size: 12px;
  font-weight: 600;
  margin-left: auto;
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.system-info {
  text-align: center;
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

.system-info p {
  margin: 4px 0;
}

/* 主内容区 */
.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 260px;
  transition: margin-left 0.3s ease;
}

/* 顶部栏样式 */
.admin-header {
  background: white;
  padding: 0 30px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
  position: sticky;
  top: 0;
  z-index: 999;
}

.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 用户下拉菜单 */
.user-dropdown {
  position: relative;
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  background: none;
  border: none;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s ease;
  color: #2c3e50;
}

.user-btn:hover {
  background: #f8f9fa;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #e9ecef;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
}

.user-name {
  font-weight: 500;
  font-size: 14px;
}

.user-arrow {
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #6c757d;
  transition: transform 0.3s ease;
}

.arrow-up {
  transform: rotate(180deg);
}

.user-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
  width: 280px;
  margin-top: 8px;
  z-index: 1000;
  overflow: hidden;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.user-avatar-large {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255,255,255,0.3);
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  font-weight: bold;
}

.user-details h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.user-details p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.menu-items {
  padding: 8px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 20px;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  font-size: 14px;
  color: #495057;
  text-decoration: none;
  transition: background 0.2s ease;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-item.logout {
  color: #e74c3c;
}

.menu-item.logout:hover {
  background: #ffebee;
}

.menu-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.menu-divider {
  height: 1px;
  background: #e9ecef;
  margin: 8px 0;
}

/* 内容区域 */
.admin-content {
  flex: 1;
  padding: 30px;
  overflow: auto;
}

/* 侧边栏折叠状态 */
.admin-layout.sidebar-collapsed .admin-sidebar {
  width: 70px;
}

.admin-layout.sidebar-collapsed .admin-main {
  margin-left: 70px;
}

.admin-layout.sidebar-collapsed .logo-text,
.admin-layout.sidebar-collapsed .nav-text,
.admin-layout.sidebar-collapsed .nav-badge,
.admin-layout.sidebar-collapsed .sidebar-footer {
  opacity: 0;
  visibility: hidden;
}

/* 移动端遮罩 */
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  z-index: 999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-sidebar {
    transform: translateX(-100%);
  }
  
  .admin-layout.sidebar-collapsed .admin-sidebar {
    transform: translateX(0);
    width: 260px;
  }
  
  .admin-main {
    margin-left: 0 !important;
  }
  
  .admin-header {
    padding: 0 20px;
  }
  
  .header-right {
    gap: 15px;
  }
  
  .user-name {
    display: none;
  }
}

@media (max-width: 480px) {
  .admin-content {
    padding: 20px 15px;
  }
  
  .admin-header {
    padding: 0 15px;
  }
}
</style>