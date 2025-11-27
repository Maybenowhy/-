import request from './request'

/**
 * 客户行为日志 API
 */

// 查询客户行为日志
export const getUserLogList = (params = {}) => {
  // 过滤掉空值参数
  const cleanParams = {}
  Object.keys(params).forEach(key => {
    if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
      cleanParams[key] = params[key]
    }
  })
  
  return request({
    url: '/sales/user-log/list',
    method: 'get',
    params: cleanParams
  })
}

// 查询某客户最近 N 条日志
export const getUserRecentLogs = (userId, limit = 10) => {
  return request({
    url: '/sales/user-log/recent',
    method: 'get',
    params: {
      userId,
      limit
    }
  })
}

// 添加用户行为日志
export const addUserLog = (data) => {
  return request({
    url: '/sales/user-log/add',
    method: 'post',
    data: data
  })
}

// 记录浏览行为
export const logUserBrowse = (data) => {
  return addUserLog({
    userId: data.userId,
    actionType: 'browse',
    targetId: data.productId,
    ipAddress: data.ipAddress
  })
}

// 记录购买行为
export const logUserPurchase = (data) => {
  return addUserLog({
    userId: data.userId,
    actionType: 'purchase',
    targetId: data.orderId
  })
}

export default {
  getUserLogList,
  getUserRecentLogs,
  addUserLog,
  logUserBrowse,
  logUserPurchase
}