import request from "./request";

export function pageUsers(params) {
  return request.get("/sales/user/page", { params });
}

export function getUserById(id) {
  return request.get(`/sales/user/${id}`);
}

export function searchUser(usernameOrEmail) {
  return request.get("/sales/user/search", {
    params: { usernameOrEmail },
  });
}

export default {
  pageUsers,
  getUserById,
  searchUser,
};
