import request from './request'

export const cartAdd = data => request.post("/user/cart/add", data)
export const cartList = () => request.get("/user/cart/list")
export const cartDelete = productId => request.delete(`/user/cart/delete/${productId}`)
