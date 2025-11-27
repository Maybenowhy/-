import request from './request'

// 获取所有商品列表
export const productList = () => request.get("/user/product/list")

// 分页查询商品（支持分类筛选）
export const productPage = (params) => request.get("/user/product/page", { params })

// 获取商品详情
export const productDetail = (productId) => {
  return request({
    url: `/user/product/${productId}`,
    method: 'get'
  })
}

// 分页查询商品
export const getProductList = (params) => {
  return request({
    url: '/admin/product/page',
    method: 'get',
    params
  })
}

// 新增商品
export const addProduct = (data) => {
  return request({
    url: '/admin/product',
    method: 'post',
    data
  })
}

// 修改商品
export const updateProduct = (data) => {
  return request({
    url: '/admin/product',
    method: 'put',
    data
  })
}

// 删除商品
export const deleteProduct = (id) => {
  return request({
    url: `/admin/product/${id}`,
    method: 'delete'
  })
}

// 批量上下架
export const batchUpdateStatus = (ids, isActive) => {
  return request({
    url: '/admin/product/status',
    method: 'post',
    params: {
      ids: ids.join(','),
      isActive
    }
  })
}

// 修改库存
export const updateStock = (id, stockQuantity) => {
  return request({
    url: '/admin/product/stock',
    method: 'post',
    params: { id, stockQuantity }
  })
}

// 根据分类ID查询商品列表
export const productListByCategory = (categoryId) => request.get(`/user/product/${categoryId}`)

export default {
  productList,
  productPage,
  productDetail,
  productListByCategory
}