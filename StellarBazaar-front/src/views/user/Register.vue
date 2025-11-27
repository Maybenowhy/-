<template>
  <div class="register-page">
    <div class="register-card">
      <h2 class="title">用户注册</h2>

      <div class="auth-form">
        <div class="form-item">
          <label>用户名</label>
          <input v-model="form.username" placeholder="请输入用户名" />
        </div>
        
        <div class="form-item">
          <label>邮箱</label>
          <input v-model="form.email" type="email" placeholder="请输入邮箱" />
        </div>
        
        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" />
        </div>
        
        <div class="form-item">
          <label>角色</label>
          <select v-model="form.role">
            <option value="customer">顾客</option>
          </select>
        </div>
        
        <button class="register-btn" @click="register">注册</button>
      </div>

      <p class="tip">已有账号？<span class="link" @click="goLogin">去登录</span></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { userRegister } from "@/api/user";
import { useRouter } from "vue-router";

const router = useRouter();

const form = ref({
  username: "",
  email: "",
  password: "",
  role: "customer", // 默认顾客
});

const register = async () => {
  try {
    await userRegister(form.value);
    alert("注册成功");
    // 跳转到登录页
    router.push("/login");
  } catch (err) {
    alert("注册失败：" + (err.response?.data?.msg || err.message));
  }
};

const goLogin = () => {
  router.push("/login");
};
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.register-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.register-card {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 40px 32px;
  border-radius: 20px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  z-index: 1;
  animation: cardEntrance 0.6s ease-out;
}

@keyframes cardEntrance {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.title {
  color: #2c3e50;
  text-align: center;
  margin-bottom: 32px;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.auth-form {
  margin-bottom: 24px;
}

.form-item {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #5a67d8;
  font-size: 14px;
  font-weight: 600;
  transition: color 0.3s ease;
}

input, select {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  color: #2d3748;
  background: #ffffff;
  transition: all 0.3s ease;
  box-sizing: border-box;
  appearance: none;
  background-image: url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%235a67d8%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E");
  background-repeat: no-repeat;
  background-position: right 16px center;
  background-size: 12px auto;
}

select {
  cursor: pointer;
}

input:focus, select:focus {
  border-color: #667eea;
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

input::placeholder {
  color: #a0aec0;
}

.register-btn {
  width: 100%;
  padding: 16px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
  position: relative;
  overflow: hidden;
}

.register-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.register-btn:hover::before {
  left: 100%;
}

.register-btn:active {
  transform: translateY(0);
}

.tip {
  margin-top: 24px;
  font-size: 14px;
  text-align: center;
  color: #718096;
}

.link {
  color: #667eea;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  position: relative;
}

.link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #667eea;
  transition: width 0.3s ease;
}

.link:hover {
  color: #764ba2;
}

.link:hover::after {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-card {
    padding: 32px 24px;
    margin: 0 16px;
  }
  
  .title {
    font-size: 24px;
  }
  
  input, select {
    padding: 12px 14px;
    font-size: 14px;
  }
  
  .register-btn {
    padding: 14px 0;
    font-size: 15px;
  }
}

/* 加载动画 */
.register-btn.loading {
  pointer-events: none;
  opacity: 0.8;
}

.register-btn.loading::after {
  content: '';
  position: absolute;
  width: 20px;
  height: 20px;
  top: 50%;
  left: 50%;
  margin: -10px 0 0 -10px;
  border: 2px solid transparent;
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>