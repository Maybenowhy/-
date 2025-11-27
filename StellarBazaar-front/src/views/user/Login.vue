<template>
  <div class="login-page">
    <div class="login-card">
      <h2 class="title">星潮市集登录</h2>

      <div class="form-item">
        <label>用户名</label>
        <input v-model="form.username" type="text" placeholder="请输入用户名" />
      </div>

      <div class="form-item">
        <label>密码</label>
        <input v-model="form.password" type="password" placeholder="请输入密码" />
      </div>

      <button class="login-btn" @click="handleLogin">登录</button>

      <p class="tip">没有账号？<span class="link" @click="goRegister">去注册</span></p>
    </div>
  </div>
</template>

<script>
import { userLogin } from "@/api/user"; 
import router from "@/router";

export default {
  name: "Login",
  data() {
    return {
      form: {
        username: "",
        password: "",
      },
    };
  },
  methods: {
    async handleLogin() {
      if (!this.form.username || !this.form.password) {
        alert("请输入完整信息");
        return;
      }

      // 将前端的 username 映射到 loginAccount
      const loginData = {
        loginAccount: this.form.username,  // 映射字段
        password: this.form.password,
      };


      try {
        const res = await userLogin(loginData);

        console.log(res.code);

        if (res.code !== 1) {
          alert(res.msg || "登录失败");
          return;
        }
        
        const token = res.data.token;
        const role = res.data.role; // 假设后端返回角色字段
        

        // 保存 Token 和角色
        localStorage.setItem("token", token);
        localStorage.setItem("role", role);
        // 在登录页面的登录成功处理中
        localStorage.setItem('username', res.data.username);
        localStorage.setItem('userId', res.data.id); // 或者 response.id，根据实际响应结构

        // 根据角色跳转
        if (role === "customer") {
          router.push("/customer/home");
        } else if (role === "sales") {
          router.push("/admin/product");
        } else {
          alert("未知角色");
        }
      } catch (e) {
        console.error("登录错误:", e);
        alert("登录请求失败，请稍后再试");
      }
    },

    goRegister() {
      router.push("/register");
    },
  },
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.login-card {
  width: 100%;
  max-width: 400px;
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

.form-item {
  margin-bottom: 24px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #5a67d8;
  font-size: 14px;
  font-weight: 600;
  transition: color 0.3s ease;
}

input {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  color: #2d3748;
  background: #ffffff;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

input:focus {
  border-color: #667eea;
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

input::placeholder {
  color: #a0aec0;
}

.login-btn {
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
  margin-top: 16px;
  position: relative;
  overflow: hidden;
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:active {
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
  .login-card {
    padding: 32px 24px;
    margin: 0 16px;
  }
  
  .title {
    font-size: 24px;
  }
  
  input {
    padding: 12px 14px;
    font-size: 14px;
  }
  
  .login-btn {
    padding: 14px 0;
    font-size: 15px;
  }
}

/* 加载动画 */
.login-btn.loading {
  pointer-events: none;
  opacity: 0.8;
}

.login-btn.loading::after {
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