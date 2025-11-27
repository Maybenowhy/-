<template>
  <div class="user-layout">
    <!-- 顶部导航 -->
    <header class="user-header">
      <div class="header-content">
        <router-link to="/customer/home" class="logo">星潮市集</router-link>
        <nav class="user-nav">
          <router-link to="/customer/home" class="nav-link">首页</router-link>
          <router-link to="/cart" class="nav-link cart-link">
            购物车
          </router-link>
          <div class="user-menu">
            <button class="user-btn" @click="toggleUserMenu">
              <span class="user-name">{{ userName }}</span>
              <i class="arrow" :class="{ 'arrow-up': showUserMenu }"></i>
            </button>
            <div v-if="showUserMenu" class="user-dropdown">
              <!-- 移除了个人资料菜单项 -->
              <router-link to="/customer/orders" class="dropdown-item">我的订单</router-link>
              <button class="dropdown-item logout" @click="handleLogout">注销</button>
            </div>
          </div>
        </nav>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="user-main">
      <router-view />
    </main>

    <!-- 底部信息 -->
    <footer class="user-footer">
      <div class="footer-content">
        <p>&copy; 2023 星潮市集 - 发现生活之美</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { userLogout } from '@/api/user'

const router = useRouter()
const showUserMenu = ref(false)
const userName = ref(localStorage.getItem('username') || '用户')

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const closeUserMenu = (event) => {
  if (!event.target.closest('.user-menu')) {
    showUserMenu.value = false
  }
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

onMounted(() => {
  document.addEventListener('click', closeUserMenu)
})

onUnmounted(() => {
  document.removeEventListener('click', closeUserMenu)
})
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.user-header {
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
}

.logo {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  text-decoration: none;
}

.user-nav {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-link {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
  position: relative;
}

.nav-link:hover {
  background: #f5f5f5;
}

.nav-link.router-link-active {
  color: #6a11cb;
  background: rgba(106, 17, 203, 0.1);
}

.cart-link {
  position: relative;
}

.user-main {
  flex: 1;
  background: #f8f9fa;
}

.user-footer {
  background: #2c3e50;
  color: white;
  padding: 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  padding: 0 20px;
}

/* 用户菜单样式 */
.user-menu {
  position: relative;
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  color: #333;
  font-weight: 500;
}

.user-btn:hover {
  background: #f5f5f5;
}

.arrow {
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #666;
  transition: transform 0.3s ease;
}

.arrow-up {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  padding: 8px 0;
  min-width: 150px;
  z-index: 10;
  margin-top: 8px;
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 12px 20px;
  text-decoration: none;
  color: #333;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s ease;
}

.dropdown-item:hover {
  background: #f5f5f5;
}

.dropdown-item.logout {
  color: #e74c3c;
  border-top: 1px solid #f0f0f0;
}

.dropdown-item.logout:hover {
  background: #ffebee;
}
</style>