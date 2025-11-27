import request from './request'

export const userLogin = data => request.post("/user/user/login", data, {
  headers: { "Content-Type": "application/json" }
})

// 获取用户列表
export const getUserList = () => {
  return request({
    url: '/user/user/list',
    method: 'get'
  })
}

// 更新用户信息
export const updateUser = (userId, data) => {
  return request({
    url: `/user/user/${userId}`,
    method: 'put',
    data
  })
}

// 更新用户状态
export const updateUserStatus = (userId, status) => {
  return request({
    url: `/user/user/${userId}/status`,
    method: 'put',
    params: { status }
  })
}

// 搜索用户
export const searchUsers = (params) => {
  return request({
    url: '/user/user/search',
    method: 'get',
    params
  })
}

// 获取用户详情
export const getUserDetail = (userId) => {
  return request({
    url: `/user/user/${userId}`,
    method: 'get'
  })
}

// 获取用户统计
export const getUserStats = () => {
  return request({
    url: '/user/user/stats',
    method: 'get'
  })
}

// 删除用户
export const deleteUser = (userId) => {
  return request({
    url: `/user/user/${userId}`,
    method: 'delete'
  })
}

export const userRegister = data => request.post("/user/user/register", data)
export const userLogout = () => request.post("/user/user/logout")