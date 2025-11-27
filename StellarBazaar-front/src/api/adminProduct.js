import request from "./request";

export function pageProducts(params) {
  return request.get("/admin/product/page", { params });
}

export function addProduct(data) {
  return request.post("/admin/product", data);
}

export function updateProduct(data) {
  return request.put("/admin/product", data);
}

export function deleteProduct(id) {
  return request.delete(`/admin/product/${id}`);
}

export function batchUpdateStatus(ids, isActive) {
  return request.post("/admin/product/status", null, {
    params: { ids, isActive },
  });
}

export function updateStock(id, stockQuantity) {
  return request.post("/admin/product/stock", null, {
    params: { id, stockQuantity },
  });
}

export default {
  pageProducts,
  addProduct,
  updateProduct,
  deleteProduct,
  batchUpdateStatus,
  updateStock,
};
