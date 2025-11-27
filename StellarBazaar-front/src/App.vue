<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  created() {
    // 简单的登录路由控制逻辑
    const role = localStorage.getItem('role'); // 'customer' | 'seller' (admin)
    const token = localStorage.getItem('token');

    if (!token) {
      this.$router.replace('/login');
    } else if (role === 'customer') {
      this.$router.replace('/customer/home');
    } else if (role === 'seller') {
      this.$router.replace('/admin/product'); // 销售端/管理员路径
    }
  }
}
</script>

<style>
#app {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--bg-color);
  color: var(--text-color);
  font-family: var(--font-family);
}
</style>
