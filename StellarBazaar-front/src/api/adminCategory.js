import request from "./request";

export function listCategories() {
  return request.get("/admin/category/list");
}

export function addCategory(data) {
  return request.post("/admin/category/add", data);
}

export function updateCategory(data) {
  return request.put("/admin/category/update", data);
}

export function deleteCategory(id) {
  return request.delete(`/admin/category/${id}`);
}

export default {
  listCategories,
  addCategory,
  updateCategory,
  deleteCategory,
};
