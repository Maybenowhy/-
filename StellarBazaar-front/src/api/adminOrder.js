import request from "./request";

export function getOrderPage(params) {
  return request.get("/admin/order/page", { params });
}

export function getOrderDetail(id) {
  return request.get(`/admin/order/${id}`);
}


export function updateOrder(data) {
  return request.put("/admin/order", data);
}

export function deleteOrder(id) {
  return request.delete(`/admin/order/${id}`);
}

export default {
  getOrderPage,
  updateOrder,
  getOrderDetail,
  deleteOrder,
};
