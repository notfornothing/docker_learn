# 项目结构说明

本文档详细说明项目的目录结构和各文件的作用。

## 📁 目录结构

```
docker_learn/                    # 项目根目录
│
├── pom.xml                      # Maven 项目配置文件
├── docker-compose.yml           # Docker Compose 主配置文件（统一管理所有服务）
├── README.md                    # 项目主文档
├── PROJECT_STRUCTURE.md         # 本文件（项目结构说明）
├── .gitignore                   # Git 忽略文件配置
│
├── src/                         # Java 源代码目录（Maven 标准结构）
│   └── main/
│       ├── java/                # Java 源代码
│       │   └── org/example/
│       │       ├── Main.java                    # Spring Boot 启动类
│       │       ├── config/                       # 配置类
│       │       │   └── StartupListener.java     # 应用启动监听器（显示访问地址）
│       │       ├── controller/                   # REST API 控制器
│       │       │   ├── HelloController.java      # 示例控制器
│       │       │   ├── NginxController.java      # Nginx 管理 API
│       │       │   └── MarkdownController.java   # Markdown 文档管理 API
│       │       └── service/                     # 业务服务层
│       │           ├── MarkdownService.java       # Markdown 处理服务（使用 CommonMark）
│       │           └── NginxProxyManagerService.java  # Nginx 管理服务
│       │
│       └── resources/           # 资源文件目录
│           ├── application.yml  # Spring Boot 配置文件
│           └── static/          # 静态资源目录
│               └── index.html  # API 文档页面（唯一 HTML 页面）
│
├── docker/                      # Docker 服务配置目录
│   ├── README.md                # Docker 服务总览文档
│   ├── TEMPLATE.md              # 服务文档模板（用于创建新服务）
│   │
│   ├── nginx-proxy-manager/     # Nginx Proxy Manager 服务
│   │   ├── docker-compose.yml  # 服务 Compose 配置
│   │   ├── README.md           # 服务使用文档
│   │   ├── data/               # 数据目录（自动创建，已加入 .gitignore）
│   │   └── letsencrypt/        # SSL 证书目录（自动创建，已加入 .gitignore）
│   │
│   └── portainer/              # Portainer 容器管理工具
│       ├── docker-compose.yml
│       ├── README.md
│       └── data/               # 数据目录（自动创建，已加入 .gitignore）
│
├── docs/                        # Markdown 文档存储目录
│   └── .gitkeep                # Git 占位文件（保持目录存在）
│                               # 文档通过 API 管理，存储在此目录
│
└── target/                      # Maven 编译输出目录（已加入 .gitignore）
    ├── classes/                # 编译后的 class 文件
    ├── docker_learn-1.0-SNAPSHOT.jar  # 打包后的 jar 文件
    └── ...
```

## 📝 文件说明

### 根目录文件

| 文件 | 说明 |
|------|------|
| `pom.xml` | Maven 项目配置文件，定义依赖和构建配置 |
| `docker-compose.yml` | Docker Compose 主配置文件，统一管理所有 Docker 服务 |
| `README.md` | 项目主文档，包含快速开始和使用说明 |
| `.gitignore` | Git 忽略文件配置，排除编译产物和数据目录 |

### Java 源代码

| 目录/文件 | 说明 |
|-----------|------|
| `src/main/java/org/example/Main.java` | Spring Boot 应用启动类 |
| `src/main/java/org/example/config/` | 配置类目录 |
| `src/main/java/org/example/controller/` | REST API 控制器目录 |
| `src/main/java/org/example/service/` | 业务服务层目录 |

### 资源文件

| 文件 | 说明 |
|------|------|
| `src/main/resources/application.yml` | Spring Boot 配置文件（端口、应用名等） |
| `src/main/resources/static/index.html` | API 文档页面（唯一 HTML 页面） |

### Docker 服务配置

| 目录 | 说明 |
|------|------|
| `docker/README.md` | Docker 服务总览文档 |
| `docker/TEMPLATE.md` | 服务文档模板，用于创建新服务文档 |
| `docker/[service-name]/docker-compose.yml` | 各服务的 Compose 配置文件 |
| `docker/[service-name]/README.md` | 各服务的使用文档 |

### 文档目录

| 目录 | 说明 |
|------|------|
| `docs/` | Markdown 文档存储目录，文档通过 API 管理 |

## 🔧 目录规范

### Java 代码组织

- **包结构**: `org.example`
  - `config/` - 配置类
  - `controller/` - REST API 控制器（使用 `@RestController`）
  - `service/` - 业务服务层（使用 `@Service`）

### Docker 服务组织

- 每个服务独立目录：`docker/[service-name]/`
- 每个服务包含：
  - `docker-compose.yml` - Compose 配置
  - `README.md` - 使用文档
  - `data/` - 数据目录（自动创建，已忽略）

### 文档组织

- `docs/` - 存储通过 API 管理的 Markdown 文档
- `docker/[service-name]/README.md` - 各服务的详细文档

## 📦 构建产物

- `target/` - Maven 编译输出目录
  - `classes/` - 编译后的 class 文件
  - `docker_learn-1.0-SNAPSHOT.jar` - 打包后的可执行 jar

## 🚫 忽略的文件

以下文件和目录已加入 `.gitignore`：

- `target/` - Maven 编译输出
- `docker/*/data/` - Docker 服务数据目录
- `docker/*/letsencrypt/` - SSL 证书目录
- IDE 配置文件（`.idea/`, `*.iml` 等）
- 日志文件（`*.log`）

## ➕ 添加新功能

### 添加新的 API 接口

1. 在 `src/main/java/org/example/controller/` 创建新的 Controller
2. 在 `src/main/java/org/example/service/` 创建对应的 Service（如需要）
3. 更新 `src/main/resources/static/index.html` 中的 API 文档

### 添加新的 Docker 服务

1. 在 `docker/` 目录下创建服务目录
2. 创建 `docker-compose.yml` 配置文件
3. 参考 `docker/TEMPLATE.md` 创建 `README.md` 文档
4. （可选）在根目录 `docker-compose.yml` 中添加服务配置

---

**最后更新**: 2025-12-10


