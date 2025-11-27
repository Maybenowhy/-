import axios from 'axios'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000, // 增加超时时间到10秒
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加 token 到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.authentication = token
    }
    
    // 记录请求日志（开发环境）
    if (process.env.NODE_ENV === 'development') {
      console.log(`📤 ${config.method?.toUpperCase()} ${config.url}`, config.params || config.data)
    }
    
    return config
  },
  (error) => {
    console.error('请求配置错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 记录响应日志（开发环境）
    if (process.env.NODE_ENV === 'development') {
      console.log(`📥 ${response.status} ${response.config.url}`, response.data)
    }
    
    // 排除登录/注册接口的特殊处理
    const excludeUrls = ['/user/login', '/user/register']
    const isExcluded = excludeUrls.some(url => response.config.url.includes(url))
    
    if (isExcluded) {
      return response.data
    }
    
    // 统一处理响应数据格式
    const { data } = response
    
    // 如果返回的数据有 code 字段
    if (data && typeof data === 'object' && 'code' in data) {
      // 操作成功
      if (data.code === 1) {
        return data.data !== undefined ? data.data : data
      } 
      // 操作失败
      else {
        const errorMsg = data.message || '请求失败'
        return Promise.reject(new Error(errorMsg))
      }
    }
    
    // 如果没有 code 字段，直接返回数据
    return data
  },
  (error) => {
    const { status, data } = error.response || {}
    
    // 记录错误日志
    console.error('请求失败:', {
      url: error.config?.url,
      method: error.config?.method,
      status: status,
      message: error.message,
      response: data
    })
    
    // 处理特定状态码
    switch (status) {
      case 401:
        handleUnauthorized()
        break
      case 403:
        alert('权限不足，无法访问该资源')
        break
      case 404:
        alert('请求的资源不存在')
        break
      case 500:
        alert('服务器内部错误，请稍后重试')
        break
      case 502:
      case 503:
        alert('服务暂时不可用，请稍后重试')
        break
      default:
        if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
          alert('请求超时，请检查网络连接')
        } else {
          alert(data?.message || '网络错误，请稍后重试')
        }
    }
    
    return Promise.reject(error)
  }
)

// 处理未授权错误
function handleUnauthorized() {
  // 清除本地存储
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
  localStorage.removeItem('userId')
  
  // 显示提示
  alert('登录已过期，请重新登录')
  
  // 跳转到登录页
  setTimeout(() => {
    // 使用 window.location 确保完全跳转
    if (!window.location.pathname.includes('/login')) {
      window.location.href = '/login'
    }
  }, 1000)
}

export default request