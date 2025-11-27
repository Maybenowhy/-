<template>
  <div class="user-management-page">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <div class="header-actions">
        <div class="search-box">
          <input 
            v-model="searchKeyword" 
            type="text" 
            placeholder="搜索用户名、邮箱..." 
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            <i>🔍</i>
          </button>
        </div>
      </div>
    </div>

    <!-- 用户数据统计 -->
    <div class="user-stats">
      <div class="stat-card">
        <div class="stat-icon total">👥</div>
        <div class="stat-info">
          <h3>{{ userStats.total }}</h3>
          <p>总用户数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon active">✅</div>
        <div class="stat-info">
          <h3>{{ userStats.active }}</h3>
          <p>活跃用户</p>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="user-table-container">
      <table class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>注册时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td class="user-id">{{ user.id }}</td>
            <td class="user-name">
              {{ user.username }}
            </td>
            <td class="user-email">{{ user.email }}</td>
            <td class="user-role">
              <span class="role-badge" :class="user.role">{{ getRoleName(user.role) }}</span>
            </td>
            <td class="user-register-time">{{ formatDate(user.createdAt) }}</td>
            <td class="user-status">
              <span class="status-badge" :class="user.status">
                {{ user.status === 'active' ? '活跃' : '禁用' }}
              </span>
            </td>
            <td class="user-actions">
              <button class="action-btn edit" @click="editUser(user)" title="编辑">
                <i>✏️</i>
              </button>
              <button 
                class="action-btn toggle-status" 
                @click="toggleUserStatus(user)"
                :title="user.status === 'active' ? '禁用用户' : '启用用户'"
              >
                <i>{{ user.status === 'active' ? '⛔' : '✅' }}</i>
              </button>
              <button class="action-btn delete" @click="deleteUser(user)" title="删除">
                <i>🗑️</i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="filteredUsers.length === 0" class="empty-state">
        <div class="empty-icon">👥</div>
        <h3>暂无用户数据</h3>
        <p v-if="searchKeyword">没有找到匹配"{{ searchKeyword }}"的用户</p>
        <p v-else>还没有用户数据</p>
      </div>
    </div>

    <!-- 分页控件 -->
    <div v-if="filteredUsers.length > 0" class="pagination">
      <button 
        class="pagination-btn" 
        :disabled="currentPage === 1" 
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span class="pagination-info">
        第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
      </span>
      <button 
        class="pagination-btn" 
        :disabled="currentPage === totalPages" 
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 编辑用户对话框 -->
    <div v-if="showEditUserDialog" class="dialog-overlay">
      <div class="user-dialog">
        <h3>编辑用户</h3>
        <form @submit.prevent="saveUser">
          <div class="form-group">
            <label>用户名:</label>
            <input 
              v-model="userForm.username" 
              type="text" 
              required 
              placeholder="请输入用户名"
            />
          </div>
          <div class="form-group">
            <label>邮箱:</label>
            <input 
              v-model="userForm.email" 
              type="text" 
              required 
              placeholder="请输入邮箱"
            />
          </div>
          <div class="form-group">
            <label>角色:</label>
            <select v-model="userForm.role">
              <option value="customer">顾客</option>
              <option value="sales">销售</option>
            </select>
          </div>
          <div class="form-group">
            <label>状态:</label>
            <select v-model="userForm.status">
              <option value="active">活跃</option>
              <option value="inactive">禁用</option>
            </select>
          </div>
          <div class="dialog-actions">
            <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
            <button type="submit" class="confirm-btn">更新</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="dialog-overlay">
      <div class="confirm-dialog">
        <h3>确认删除</h3>
        <p>确定要删除用户 "{{ userToDelete?.username }}" 吗？此操作不可恢复。</p>
        <div class="dialog-actions">
          <button class="cancel-btn" @click="showDeleteConfirm = false">取消</button>
          <button class="delete-confirm-btn" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { 
  getUserList, 
  searchUsers, 
  getUserStats, 
  deleteUser as deleteUserApi,
  updateUser,           // 新增
  updateUserStatus      // 新增
} from '@/api/user';

// 响应式数据
const users = ref([]);
const searchKeyword = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const showEditUserDialog = ref(false);
const showDeleteConfirm = ref(false);
const editingUser = ref(null);
const userToDelete = ref(null);

// 用户表单
const userForm = reactive({
  username: "",
  email: "",
  role: "customer",
  status: "active"
});

// 用户统计
const userStats = reactive({
  total: 0,
  active: 0
});

// 计算属性
const filteredUsers = computed(() => {
  let filtered = users.value;
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter(user => 
      user.username.toLowerCase().includes(keyword) ||
      user.email.toLowerCase().includes(keyword)
    );
  }
  
  // 分页
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filtered.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(users.value.length / pageSize.value);
});

// 方法
const loadUsers = async () => {
  try {
    console.log('开始加载用户列表...');
    const response = await getUserList();
    console.log('用户列表响应:', response);
    
    // 如果response是数组，直接使用
    if (Array.isArray(response)) {
      users.value = response.map(user => {
        // 确保每个用户都有必要的字段
        return {
          id: user.id,
          username: user.username || '未知用户',
          email: user.email || '',
          role: user.role || 'customer',
          phone: user.phone || '',
          status: determineUserStatus(user), // 根据业务逻辑确定状态
          createdAt: user.createdAt || new Date().toISOString(),
          updatedAt: user.updatedAt || new Date().toISOString(),
          lastLogin: user.lastLogin,
          lastLogoutTime: user.lastLogoutTime
        };
      });
      updateUserStats();
      console.log('用户列表加载成功，共', users.value.length, '个用户');
    } 
    // 如果response是对象且有data数组
    else if (response && response.code === 1 && Array.isArray(response.data)) {
      users.value = response.data.map(user => {
        return {
          id: user.id,
          username: user.username || '未知用户',
          email: user.email || '',
          role: user.role || 'customer',
          phone: user.phone || '',
          status: determineUserStatus(user),
          createdAt: user.createdAt || new Date().toISOString(),
          updatedAt: user.updatedAt || new Date().toISOString(),
          lastLogin: user.lastLogin,
          lastLogoutTime: user.lastLogoutTime
        };
      });
      updateUserStats();
      console.log('用户列表加载成功，共', users.value.length, '个用户');
    } 
    // 如果response是对象但结构不同
    else if (response && response.data && Array.isArray(response.data)) {
      users.value = response.data.map(user => {
        return {
          id: user.id,
          username: user.username || '未知用户',
          email: user.email || '',
          role: user.role || 'customer',
          phone: user.phone || '',
          status: determineUserStatus(user),
          createdAt: user.createdAt || new Date().toISOString(),
          updatedAt: user.updatedAt || new Date().toISOString(),
          lastLogin: user.lastLogin,
          lastLogoutTime: user.lastLogoutTime
        };
      });
      updateUserStats();
      console.log('用户列表加载成功，共', users.value.length, '个用户');
    } 
    else {
      console.error('获取用户列表失败: 响应格式不正确', response);
      // 使用模拟数据作为备选
      users.value = [
        { id: 4, username: 'testAdmin', email: '123', role: 'customer', status: 'active', createdAt: new Date().toISOString(), avatar: '/default-avatar.png' },
        { id: 3, username: 'test', email: 'h13542176184@gmail.com', role: 'customer', status: 'active', createdAt: new Date().toISOString(), avatar: '/default-avatar.png' },
        { id: 1, username: '测试', email: '246', role: 'customer', status: 'active', createdAt: new Date().toISOString(), avatar: '/default-avatar.png' },
        { id: 5, username: 'testSale', email: '2462862358@qq.com', role: 'sales', status: 'active', createdAt: new Date().toISOString(), avatar: '/default-avatar.png' }
      ];
      updateUserStats();
    }
  } catch (error) {
    console.error('加载用户数据失败:', error);
    // 使用模拟数据作为备选
    users.value = [
      { id: 4, username: 'testAdmin', email: '123', role: 'customer', status: 'active', createdAt: new Date().toISOString(), avatar: '/default-avatar.png' },
      { id: 3, username: 'test', email: 'h13542176184@gmail.com', role: 'customer', status: 'active', createdAt: new Date().toISOString(), avatar: '/default-avatar.png' },
      { id: 1, username: '测试', email: '246', role: 'customer', status: 'active', createdAt: new Date().toISOString(), avatar: '/default-avatar.png' },
      { id: 5, username: 'testSale', email: '2462862358@qq.com', role: 'sales', status: 'active', createdAt: new Date().toISOString(), avatar: '/default-avatar.png' }
    ];
    updateUserStats();
  }
};

// 根据用户最后活动时间确定状态
const determineUserStatus = (user) => {
  // 简单的状态判断逻辑：如果最后登录时间存在且合理，则为活跃
  if (user.lastLogin) {
    const lastLogin = new Date(user.lastLogin);
    const now = new Date();
    const diffDays = (now - lastLogin) / (1000 * 60 * 60 * 24);
    return diffDays <= 30 ? 'active' : 'inactive';
  }
  return 'inactive';
};

const loadUserStats = async () => {
  try {
    const response = await getUserStats();
    if (response && response.code === 1) {
      userStats.total = response.data.total || 0;
      userStats.active = response.data.active || 0;
    } else {
      // 如果统计接口不可用，从用户列表计算
      updateUserStats();
    }
  } catch (error) {
    console.error('加载用户统计失败:', error);
    updateUserStats();
  }
};

const updateUserStats = () => {
  userStats.total = users.value.length;
  userStats.active = users.value.filter(user => user.status === 'active').length;
};

const handleSearch = async () => {
  try {
    if (searchKeyword.value.trim()) {
      const response = await searchUsers({ keyword: searchKeyword.value });
      if (response && response.code === 1) {
        users.value = response.data.map(user => ({
          ...user,
          status: determineUserStatus(user),
          createdAt: user.createdAt || new Date().toISOString()
        }));
      }
    } else {
      await loadUsers();
    }
    currentPage.value = 1;
  } catch (error) {
    console.error('搜索用户失败:', error);
  }
};

const changePage = (page) => {
  currentPage.value = page;
};

const editUser = (user) => {
  editingUser.value = user;
  Object.assign(userForm, {
    username: user.username,
    email: user.email,
    role: user.role,
    status: user.status
  });
  showEditUserDialog.value = true;
};

const saveUser = async () => {
  try {
    // 调用更新用户接口
    const response = await updateUser(editingUser.value.id, userForm);
    if (response) {
      // 更新本地数据
      const index = users.value.findIndex(u => u.id === editingUser.value.id);
      if (index !== -1) {
        users.value[index] = { ...users.value[index], ...userForm };
      }
      updateUserStats();
      closeDialog();
    } else {
      alert('更新失败: ' + (response?.msg || '未知错误'));
    }
  } catch (error) {
    console.error('更新用户失败:', error);
    alert('操作失败，请重试');
  }
};

const toggleUserStatus = async (user) => {
  try {
    const newStatus = user.status === 'active' ? 'inactive' : 'active';
    // 调用更新用户状态接口
    const response = await updateUserStatus(user.id, newStatus);
    if (response) {
      // 更新本地数据
      const index = users.value.findIndex(u => u.id === user.id);
      if (index !== -1) {
        users.value[index].status = newStatus;
      }
      updateUserStats();
    } else {
      alert('更新状态失败: ' + (response?.msg || '未知错误'));
    }
  } catch (error) {
    console.error('更新用户状态失败:', error);
    alert('操作失败，请重试');
  }
};

const deleteUser = (user) => {
  userToDelete.value = user;
  showDeleteConfirm.value = true;
};

const confirmDelete = async () => {
  try {
    if (!userToDelete.value) return;
    
    const response = await deleteUserApi(userToDelete.value.id);
    if (response) {
      // 重新加载用户列表以确保数据同步
      await loadUsers();
      showDeleteConfirm.value = false;
      userToDelete.value = null;
    } else {
      alert('删除失败: ' + (response?.msg || '未知错误'));
    }
  } catch (error) {
    console.error('删除用户失败:', error);
    alert('删除失败，不能删除管理员用户或存在关联数据的用户');
  }
};

const closeDialog = () => {
  showEditUserDialog.value = false;
  editingUser.value = null;
  Object.assign(userForm, {
    username: "",
    email: "",
    role: "customer",
    status: "active"
  });
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
  } catch (error) {
    return '-';
  }
};

const getRoleName = (role) => {
  const roleMap = {
    'customer': '顾客',
    'sales': '销售',
    'admin': '管理员'
  };
  return roleMap[role] || role;
};

// 生命周期
onMounted(() => {
  loadUsers();
  loadUserStats();
});
</script>

<style scoped>
/* 样式保持不变 */
.user-management-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 0;
  border-bottom: 1px solid #e9ecef;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-box {
  display: flex;
  align-items: center;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  overflow: hidden;
}

.search-box input {
  border: none;
  padding: 10px 16px;
  font-size: 14px;
  width: 250px;
  outline: none;
}

.search-btn {
  background: #f8f9fa;
  border: none;
  padding: 10px 16px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.search-btn:hover {
  background: #e9ecef;
}

.user-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.total { background: #e3f2fd; }
.stat-icon.active { background: #e8f5e8; }

.stat-info h3 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
}

.stat-info p {
  margin: 4px 0 0 0;
  color: #6c757d;
  font-size: 14px;
}

.user-table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  overflow: hidden;
  margin-bottom: 30px;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th {
  background: #f8f9fa;
  padding: 16px 20px;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 1px solid #e9ecef;
}

.user-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f8f9fa;
  vertical-align: middle;
}

.user-table tbody tr:hover {
  background: #f8f9fa;
}

.user-id {
  font-weight: 600;
  color: #6c757d;
}

.user-name {
  font-weight: 500;
}

.user-email {
  color: #6c757d;
}

.role-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: capitalize;
}

.role-badge.customer { background: #e3f2fd; color: #1976d2; }
.role-badge.sales { background: #fff3e0; color: #f57c00; }
.role-badge.admin { background: #fce4ec; color: #c2185b; }

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active { background: #e8f5e8; color: #2e7d32; }
.status-badge.inactive { background: #ffebee; color: #c62828; }

.user-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: none;
  border: none;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
  font-size: 14px;
}

.action-btn.edit:hover { background: #e3f2fd; }
.action-btn.toggle-status:hover { background: #fff3e0; }
.action-btn.delete:hover { background: #ffebee; }

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 8px;
}

.empty-state p {
  color: #6c757d;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
}

.pagination-btn {
  padding: 10px 20px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #4a5568;
}

.pagination-btn:hover:not(:disabled) {
  background: #6a11cb;
  color: white;
  border-color: #6a11cb;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  color: #718096;
  font-size: 14px;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.user-dialog, .confirm-dialog {
  background: white;
  border-radius: 16px;
  padding: 32px;
  width: 480px;
  max-width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: dialogSlideIn 0.3s ease;
}

@keyframes dialogSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.user-dialog h3, .confirm-dialog h3 {
  margin: 0 0 24px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #2c3e50;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s ease;
}

.form-group input:focus, .form-group select:focus {
  outline: none;
  border-color: #6a11cb;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.cancel-btn, .confirm-btn, .delete-confirm-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #5a6268;
}

.confirm-btn {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(106, 17, 203, 0.3);
}

.delete-confirm-btn {
  background: #e74c3c;
  color: white;
}

.delete-confirm-btn:hover {
  background: #c0392b;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .search-box input {
    width: 200px;
  }
  
  .user-stats {
    grid-template-columns: 1fr;
  }
  
  .user-table {
    font-size: 14px;
  }
  
  .user-table th,
  .user-table td {
    padding: 12px 8px;
  }
  
  .user-actions {
    flex-direction: column;
    gap: 4px;
  }
}

@media (max-width: 480px) {
  .search-box input {
    width: 150px;
  }
  
  .user-dialog, .confirm-dialog {
    padding: 24px;
  }
}
</style>