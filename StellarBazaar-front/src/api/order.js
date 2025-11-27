import request from './request'

/**
 * 订单相关 API
 */

// 提交订单
export const submitOrder = (data) => request.post('/user/order/submit', data)

// 支付订单
export const payOrder = (orderId) => request.post(`/user/order/pay/${orderId}`)

// 确认收货
export const confirmOrder = (orderId) => request.post(`/user/order/confirm/${orderId}`)

// 获取订单列表
export const getOrderList = (userId) => {
  const params = userId ? { userId } : {}
  return request.get('/user/order/list', { params })
}

// 获取订单详情
export const getOrderDetail = (orderId) => request.get(`/user/order/${orderId}`)

// 为了兼容性，同时导出别名
export const orderList = getOrderList
export const getOrder = getOrderDetail  // 确保有这个导出