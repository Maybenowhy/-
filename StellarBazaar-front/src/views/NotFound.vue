<template>
  <div class="not-found">
    <div class="error-content">
      <h1>404</h1>
      <p>页面未找到</p>
      <router-link to="/" class="home-link">返回首页</router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #f8f9fa;
}

.error-content {
  text-align: center;
}

.error-content h1 {
  font-size: 120px;
  margin: 0;
  color: #e74c3c;
}

.error-content p {
  font-size: 24px;
  color: #7f8c8d;
  margin: 0 0 30px 0;
}

.home-link {
  display: inline-block;
  padding: 12px 24px;
  background: #3498db;
  color: white;
  text-decoration: none;
  border-radius: 6px;
  transition: background 0.3s ease;
}

.home-link:hover {
  background: #2980b9;
}
</style>