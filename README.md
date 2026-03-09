# 文件管理系统

一个基于 Vue 3 + Spring Boot + MyBatis-Plus 的文件管理系统，支持硬盘扫描、文件分类、标签管理、文件预览等功能。

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- Hutool 工具库

### 前端
- Vue 3.3.8
- Ant Design Vue 4.0.7
- Vue Router 4
- Pinia
- Axios
- Vite 5

## 功能特性

- 硬盘管理：自动检测系统挂载的硬盘，支持扫描硬盘文件
- 文件管理：文件的增删改查、上传下载、预览功能
- 分类管理：支持树形分类结构
- 标签管理：自定义标签，支持按标签搜索
- 文件预览：支持PDF等文件在线预览
- 高级搜索：支持关键词、类型、分类、标签等多条件搜索

## 快速开始

### 1. 数据库配置

```sql
创建数据库并执行初始化脚本：
mysql -u root -p < backend/src/main/resources/db/init.sql
```

或者手动在 MySQL 中执行 [init.sql](backend/src/main/resources/db/init.sql) 文件。

### 2. 后端启动

```bash
cd backend

修改数据库配置：
编辑 src/main/resources/application.yml，修改数据库连接信息

启动项目：
mvn spring-boot:run
```

后端服务将在 http://localhost:8081 启动

### 3. 前端启动

```bash
cd frontend

安装依赖：
npm install

启动开发服务器：
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 项目结构

```
disk-manager/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/diskmanager/
│   │   │   │   ├── config/     # 配置类
│   │   │   │   ├── controller/ # 控制器
│   │   │   │   ├── entity/     # 实体类
│   │   │   │   ├── mapper/     # Mapper接口
│   │   │   │   ├── service/    # 服务层
│   │   │   │   ├── dto/        # 数据传输对象
│   │   │   │   └── common/     # 公共类
│   │   │   └── resources/
│   │   │       ├── mapper/     # Mapper XML
│   │   │       ├── db/         # 数据库脚本
│   │   │       └── application.yml
│   │   └── pom.xml
│   └──
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── router/            # 路由配置
│   │   ├── views/             # 页面组件
│   │   ├── styles/            # 样式文件
│   │   ├── utils/             # 工具函数
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## API 接口

### 磁盘管理
- `GET /api/disk/list` - 获取磁盘列表
- `GET /api/disk/{id}` - 获取磁盘详情
- `POST /api/disk/refresh/{id}` - 刷新磁盘信息
- `POST /api/disk/scan/{id}` - 扫描磁盘文件

### 文件管理
- `POST /api/file/search` - 搜索文件
- `GET /api/file/{id}` - 获取文件详情
- `PUT /api/file` - 更新文件信息
- `DELETE /api/file/{id}` - 删除文件
- `DELETE /api/file/batch` - 批量删除文件
- `POST /api/file/upload` - 上传文件
- `GET /api/file/download/{id}` - 下载文件
- `GET /api/file/preview/{id}` - 预览文件

### 分类管理
- `GET /api/category/tree` - 获取分类树
- `GET /api/category/list` - 获取所有分类
- `POST /api/category` - 添加分类
- `PUT /api/category` - 更新分类
- `DELETE /api/category/{id}` - 删除分类

### 标签管理
- `GET /api/tag/list` - 获取所有标签
- `GET /api/tag/file/{fileId}` - 获取文件的标签
- `POST /api/tag` - 添加标签
- `PUT /api/tag` - 更新标签
- `DELETE /api/tag/{id}` - 删除标签

## 页面截图

系统包含以下页面：

1. **仪表盘** - 显示系统概览、磁盘使用情况、最近文件
2. **文件管理** - 文件列表、搜索、上传、编辑、删除、预览
3. **磁盘管理** - 磁盘列表、扫描文件、查看文件
4. **标签管理** - 标签增删改查、按标签搜索文件
5. **分类管理** - 分类树形结构管理

## 注意事项

1. 首次使用需要先执行数据库初始化脚本
2. Windows 系统会自动检测所有盘符
3. Linux/Mac 系统会检测 /media、/mnt 和根目录
4. 文件预览目前支持 PDF 和图片格式
5. 删除操作都有二次确认弹窗
