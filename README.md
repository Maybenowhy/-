🌟 项目特色
🛍️ 完整的电商功能 - 商品浏览、购物车、订单管理、支付流程

👨‍💼 多角色权限系统 - 顾客、销售、管理员三级权限控制

📊 数据统计分析 - 销售报表、商品排行、用户行为分析

📱 响应式设计 - 支持PC端和移动端访问

🔒 安全可靠 - JWT认证、BCrypt加密、权限验证

🏗️ 系统架构
技术栈
后端技术

Spring Boot 2.7.x

MyBatis Plus

MySQL 8.0

JWT认证

Swagger文档

前端技术

Vue 3 + Composition API

Vue Router + Pinia

Element Plus UI

Axios HTTP客户端

Vite构建工具

项目结构
text
ecommerce-platform/
├── backend/                 # Spring Boot后端
│   ├── src/main/java/com/an/
│   │   ├── controller/     # 控制层
│   │   ├── service/        # 业务层
│   │   ├── mapper/         # 数据访问层
│   │   ├── entity/         # 实体类
│   │   └── config/         # 配置类
│   └── src/main/resources/
│       ├── application.yml # 配置文件
│       └── mapper/         # MyBatis映射文件
├── frontend/                # Vue前端
│   ├── src/
│   │   ├── views/          # 页面组件
│   │   ├── components/     # 通用组件
│   │   ├── router/         # 路由配置
│   │   └── stores/         # 状态管理
│   └── package.json
└── docs/                   # 项目文档
🚀 快速开始
环境要求
JDK 1.8+

Node.js 16+

MySQL 8.0+

Maven 3.6+

后端部署
克隆项目

bash
git clone https://github.com/your-username/ecommerce-platform.git
cd ecommerce-platform/backend
数据库配置

sql
CREATE DATABASE ecommerce DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'ecommerce_user'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ecommerce.* TO 'ecommerce_user'@'%';
FLUSH PRIVILEGES;
修改配置

yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce
    username: ecommerce_user
    password: your_password
启动应用

bash
mvn clean package
java -jar target/ecommerce-backend-1.0.0.jar
前端部署
安装依赖

bash
cd frontend
npm install
配置API地址

javascript
// src/utils/request.js
const baseURL = 'http://localhost:8080/api'
启动开发服务器

bash
npm run dev
构建生产版本

bash
npm run build
📋 功能模块
顾客端功能
✅ 用户注册/登录

✅ 商品浏览与搜索

✅ 购物车管理

✅ 订单创建与支付

✅ 个人中心

✅ 订单跟踪

管理端功能
✅ 商品管理（CRUD）

✅ 订单管理

✅ 用户管理

✅ 销售统计

✅ 库存管理

✅ 用户行为日志

🔐 API接口
认证接口
POST /api/auth/login - 用户登录

POST /api/auth/register - 用户注册

商品接口
GET /api/products - 商品列表

GET /api/products/{id} - 商品详情

POST /api/admin/products - 新增商品（管理员）

订单接口
POST /api/orders/submit - 提交订单

GET /api/orders - 订单列表

POST /api/orders/{id}/pay - 订单支付

访问 http://localhost:8080/api/swagger-ui.html 查看完整API文档。

🗂️ 数据库设计
主要数据表：

users - 用户表

products - 商品表

orders - 订单表

order_items - 订单明细表

shopping_cart - 购物车表

user_logs - 用户行为日志表

🧪 测试说明
单元测试
bash
# 后端测试
mvn test

# 前端测试
npm run test
功能测试
系统已完成全面功能测试，包括：

用户认证测试

商品管理测试

购物车流程测试

订单流程测试

权限控制测试

🐛 故障排除
常见问题
数据库连接失败

检查MySQL服务是否启动

验证数据库配置信息

检查网络连接

前端无法访问API

确认后端服务已启动

检查CORS配置

验证API地址配置

权限认证失败

检查JWT token是否有效

验证用户角色权限

检查登录状态

日志查看
bash
# 后端日志
tail -f logs/backend.log

# 前端调试
浏览器开发者工具 → Network/Console
📝 部署指南
生产环境部署
服务器要求

CPU: 2核+

内存: 4GB+

系统: CentOS 7+/Ubuntu 18+

部署步骤

bash
# 1. 上传jar包和前端文件
# 2. 配置Nginx反向代理
# 3. 配置MySQL数据库
# 4. 启动后端服务
# 5. 配置域名和SSL证书
详细部署文档请参考 DEPLOYMENT.md

🤝 贡献指南
我们欢迎任何形式的贡献！

Fork本项目

创建特性分支 (git checkout -b feature/AmazingFeature)

提交更改 (git commit -m 'Add some AmazingFeature')

推送到分支 (git push origin feature/AmazingFeature)

开启Pull Request
