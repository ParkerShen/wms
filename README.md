# 🌟 星瀚WMS (Starlink WMS)

**仓储管理系统** — 基于 Spring Boot 3 + Vue 3 重构的云仓项目

---

## 🏗️ 技术栈

| 层级 | 技术 |
|------|------|
| **后端** | Spring Boot 3.2 + JDK 17 + MyBatis-Plus + JWT |
| **前端** | Vue 3 + TypeScript + Vite + Element Plus + Pinia |
| **数据库** | MySQL 8.0 (Docker) |
| **缓存** | Redis 7 (Docker) |

## 📁 项目结构

```
starlink-wms/
├── docker-compose.yml          # Docker 环境（MySQL + Redis）
├── backend/                    # Java 后端
│   ├── pom.xml
│   ├── sql/init.sql            # 建表 SQL + 初始化数据
│   └── src/main/java/com/starlink/wms/
│       ├── WmsApplication.java     # 启动类
│       ├── common/                  # 公共组件
│       │   ├── Result.java              # 统一响应
│       │   ├── constant/Constants.java  # 常量
│       │   └── exception/              # 异常处理
│       ├── config/                 # 配置
│       │   ├── SecurityConfig.java     # Spring Security + JWT
│       │   └── MyBatisPlusConfig.java  # MyBatis-Plus 自动填充
│       ├── filter/                  # JWT 过滤器
│       ├── modules/auth/            # 认证模块
│       │   ├── controller/             # AuthController, HealthController
│       │   ├── service/                # AuthService
│       │   ├── entity/SysUser.java     # 用户实体
│       │   ├── mapper/SysUserMapper.java
│       │   └── dto/                    # LoginReq, LoginResp
│       └── utils/JwtUtils.java     # JWT 工具类
└── frontend/                   # Vue 3 前端
    ├── package.json
    ├── vite.config.ts
    ├── index.html
    └── src/
        ├── main.ts
        ├── App.vue
        ├── router/                  # 路由（含登录守卫）
        ├── store/auth.ts            # Pinia 认证状态
        ├── api/auth.ts              # 登录 API
        ├── utils/request.ts         # Axios 封装
        ├── views/login/             # 登录页
        ├── views/dashboard/         # 工作台
        └── layouts/MainLayout.vue   # 主布局
```

---

## 🚀 快速启动（第1步 → 第N步）

### 前置条件

| 工具 | 版本要求 | 安装方式 |
|------|---------|---------|
| Docker Desktop | ✅ 已安装 (v29.2.1) | 已有 |
| Node.js | v18+ (当前 v16，建议升级) | 从 [nodejs.org](https://nodejs.org) 下载 LTS |
| JDK | 17+ | :arrow_down: 需要安装 |
| Maven | 3.8+ | :arrow_down: 需要安装 |

### 第1步：安装 JDK 17

```powershell
# 下载 Eclipse Temurin JDK 17
# 1. 打开浏览器访问 https://adoptium.net/temurin/releases/
# 2. 下载 Windows x64 MSI 安装包
# 3. 双击安装，一路 Next
# 4. 验证安装
```

验证：

```powershell
java -version
# 输出应为：openjdk version "17.x.x"
```

### 第2步：安装 Maven

```powershell
# 方法1：使用 Chocolatey（推荐）
# 先在 PowerShell（管理员）中安装 Chocolatey：
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# 然后安装 Maven：
choco install maven -y

# 方法2：手动下载
# 1. 从 https://maven.apache.org/download.cgi 下载 Binary zip
# 2. 解压到 C:\tools\apache-maven-3.9.x
# 3. 添加环境变量 MAVEN_HOME 和 %MAVEN_HOME%\bin 到 PATH
```

验证：

```powershell
mvn -version
# 输出应为：Apache Maven 3.9.x
```

### 第3步：启动数据库

```powershell
# 启动 Docker 容器
cd c:\data\starlink-wms
docker compose up -d

# 验证
docker ps
# 应看到 starlink-mysql 和 starlink-redis 正在运行
```

### 第4步：启动后端

```powershell
# 构建并启动 Spring Boot
cd c:\data\starlink-wms\backend
mvn clean install -DskipTests
mvn spring-boot:run

# 或使用 IDEA 打开 backend 目录，直接运行 WmsApplication.main()
```

验证：

```powershell
# 新开终端，测试健康检查
curl http://localhost:8080/api/health
# 返回：{"code":200,"msg":"操作成功","data":{"status":"UP","appName":"starlink-wms",...}}
```

### 第5步：启动前端

```powershell
# 新开终端
cd c:\data\starlink-wms\frontend
npm install
npm run dev

# 浏览器访问 http://localhost:3000
```

### 第6步：测试登录

1. 打开浏览器访问 http://localhost:3000
2. 默认账号：`admin` / `admin123`
3. 点击【登录】即可看到工作台

---

## 🐳 Docker 说明

| 服务 | 端口 | 用户名 | 密码 |
|------|------|--------|------|
| MySQL | 3307 | root | Starlink@2024 |
| Redis | 6380 | - | - |

**常用命令：**

```powershell
# 启动
docker compose up -d

# 停止
docker compose down

# 查看日志
docker compose logs -f mysql
docker compose logs -f redis

# 进入 MySQL
docker exec -it starlink-mysql mysql -uroot -pStarlink@2024 starlink_wms
```

## 🔌 API 接口（v1）

| 方法 | 路径 | 说明 | 需认证 |
|------|------|------|--------|
| GET | /api/health | 健康检查 | ❌ |
| POST | /api/auth/login | 用户登录 | ❌ |
| POST | /api/auth/logout | 用户登出 | ✅ |
| GET | /api/auth/me | 获取当前用户 | ✅ |

### 调用示例

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取用户信息（替换 YOUR_TOKEN）
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🗺️ 下一步规划

| 模块 | 状态 | 说明 |
|------|------|------|
| ✅ 用户认证 | 已完成 | 登录/登出/Token认证 |
| ⏳ 验证码 | 待实现 | 图形验证码/滑块验证码 |
| ⏳ 用户管理 | 待实现 | 用户CRUD + 分页 |
| ⏳ 角色权限 | 待实现 | RBAC权限模型 |
| ⏳ 菜单管理 | 待实现 | 动态菜单 |
| ⏳ 入库管理 | 待实现 | 参考旧项目的 ASN 模块 |
| ⏳ 出库管理 | 待实现 | 参考旧项目的 SO 模块 |
| ⏳ 库存管理 | 待实现 | 库存查询、调整 |
| ⏳ ... | 待实现 | 逐步按旧项目功能迁移 |

---

## 💡 学习 Java 小贴士

1. **多看代码中的注释** — 每个文件都有中文注释解释作用
2. **照猫画虎** — 创建新模块时，照着 `auth` 模块的格式写
3. **MyBatis-Plus 基础用法**：
   - `baseMapper.selectById(id)` — 查单个
   - `baseMapper.selectList(queryWrapper)` — 查列表
   - `baseMapper.insert(entity)` — 新增
   - `baseMapper.updateById(entity)` — 更新
   - `baseMapper.deleteById(id)` — 删除
4. **启动报错先看日志** — 控制台日志会显示详细错误信息
5. **随时可以问我** — 遇到任何 Java 问题都可以问我
