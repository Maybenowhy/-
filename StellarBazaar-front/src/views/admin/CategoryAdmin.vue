<template>
  <div class="page-container">
    <h2>商品分类管理</h2>
    
    <!-- 添加分类按钮 -->
    <div class="mb-20">
      <button class="btn" @click="addCategory">添加分类</button>
    </div>

    <!-- 分类列表 -->
    <table class="sb-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>分类名称</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="category in categories" :key="category.id">
          <td>{{ category.id }}</td>
          <td>{{ category.name }}</td>
          <td>
            <button class="btn" @click="editCategory(category)">编辑</button>
            <button class="btn btn-danger" @click="deleteCategory(category.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref } from "vue";

// 商品分类数据
const categories = ref([
  { id: 1, name: "分类A" },
  { id: 2, name: "分类B" },
]);

// 添加分类
const addCategory = () => {
  alert("跳转到添加分类页面");
};

// 编辑分类
const editCategory = (category) => {
  alert("编辑分类 " + category.name);
};

// 删除分类
const deleteCategory = (categoryId) => {
  if (confirm("确定删除该分类吗？")) {
    // 模拟删除
    const index = categories.value.findIndex((c) => c.id === categoryId);
    if (index !== -1) {
      categories.value.splice(index, 1);
    }
  }
};
</script>

<style scoped>
/* 表格样式 */
.sb-table th {
  background-color: #f5f6f7;
  font-weight: 600;
}

.sb-table td {
  padding: 12px;
}

.sb-table tbody tr:hover {
  background-color: #fafafa;
}

.sb-table td button {
  margin-right: 10px;
  font-size: 12px;
}
</style>
