import { createRouter, createWebHistory } from 'vue-router'

// 懒加载导入组件
const Login = () => import('../views/user/Login.vue')
const Register = () => import('../views/user/Register.vue')
const ProductList = () => import('../views/user/ProductList.vue')
const ProductDetail = () => import('../views/user/ProductDetail.vue')
const Cart = () => import('../views/user/Cart.vue')
const OrderList = () => import('../views/user/OrderList.vue')
const OrderDetail = () => import('../views/user/OrderDetail.vue')
const UserInfo = () => import('../views/user/UserInfo.vue')
const ProductAdmin = () => import('../views/admin/ProductAdmin.vue')
const CategoryAdmin = () => import('../views/admin/CategoryAdmin.vue')
const OrderAdmin = () => import('../views/admin/OrderAdmin.vue')
const ReportAdmin = () => import('../views/admin/ReportAdmin.vue')
const AdminDashboard = () => import('../views/admin/AdminDashboard.vue')
const UserAdmin = () => import('../views/admin/UserList.vue') // 添加用户管理组件
const UserLayout = () => import('../layouts/UserLayout.vue')
const AdminLayout = () => import('../layouts/AdminLayout.vue')

const routes = [
  // 重定向到首页
  {
    path: '/',
    redirect: '/customer/home'
  },

  // 顾客端路由
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录 - 星潮市集',
      requiresGuest: true
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: {
      title: '注册 - 星潮市集',
      requiresGuest: true
    }
  },
  {
    path: '/customer',
    component: UserLayout,
    meta: {
      requiresAuth: true,
      role: 'customer'
    },
    children: [
      {
        path: 'home',
        name: 'ProductList',
        component: ProductList,
        meta: { title: '精选商品 - 星潮市集' }
      },
      {
        path: 'info',
        name: 'UserInfo',
        component: UserInfo,
        meta: { title: '用户信息 - 星潮市集' }
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: OrderList,
        meta: { title: '我的订单 - 星潮市集' }
      }
    ]
  },

  // 独立路由（不需要布局）
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail,
    meta: {
      title: '商品详情 - 星潮市集',
      requiresAuth: true
    }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: Cart,
    meta: {
      title: '购物车 - 星潮市集',
      requiresAuth: true,
      role: 'customer'
    }
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: OrderDetail,
    meta: {
      title: '订单详情 - 星潮市集',
      requiresAuth: true
    }
  },

  // 管理员端路由 - 修改为支持 admin 和 sales 角色
  {
    path: '/admin',
    component: AdminLayout,
    meta: {
      requiresAuth: true,
      role: ['admin', 'sales'] // 修改为数组，支持多个角色
    },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
        meta: { title: '仪表板 - 管理后台' }
      },
      {
        path: 'product',
        name: 'ProductAdmin',
        component: ProductAdmin,
        meta: { title: '商品管理 - 管理后台' }
      },
      {
        path: 'category',
        name: 'CategoryAdmin',
        component: CategoryAdmin,
        meta: { title: '分类管理 - 管理后台' }
      },
      {
        path: 'order',
        name: 'OrderAdmin',
        component: OrderAdmin,
        meta: { title: '订单管理 - 管理后台' }
      },
      {
        path: 'user', // 添加用户管理路由
        name: 'UserAdmin',
        component: UserAdmin,
        meta: { title: '用户管理 - 管理后台' }
      },
      {
        path: 'report',
        name: 'ReportAdmin',
        component: ReportAdmin,
        meta: { title: '报表统计 - 管理后台' }
      },
      {
        path: '/admin/user-log',
        name: 'UserLog',
        component: () => import('@/views/admin/UserLog.vue'),
        meta: { title: '用户日志' }
      }
    ]
  },

  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { title: '页面未找到 - 星潮市集' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('role')

  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    if (!token) {
      console.log('需要认证但无token，跳转到登录页')
      next('/login')
      return
    }

    // 检查角色权限 - 修改为支持数组角色检查
    if (to.meta.role) {
      const requiredRoles = Array.isArray(to.meta.role) ? to.meta.role : [to.meta.role]

      if (!requiredRoles.includes(userRole)) {
        console.log(`角色不匹配，需要: ${requiredRoles.join(' 或 ')}，当前角色: ${userRole}`)

        // 根据当前角色重定向到对应首页
        let targetPath = '/customer/home'
        if (userRole === 'admin' || userRole === 'sales') {
          targetPath = '/admin/dashboard'
        }

        if (to.path !== targetPath) {
          next(targetPath)
          return
        }
      }
    }
  }

  // 如果已登录，阻止访问登录/注册页
  if (to.meta.requiresGuest && token) {
    console.log('已登录，阻止访问登录注册页')
    let targetPath = '/customer/home'
    if (userRole === 'admin' || userRole === 'sales') {
      targetPath = '/admin/dashboard'
    }
    next(targetPath)
    return
  }

  next()
})

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error)
})

export default router