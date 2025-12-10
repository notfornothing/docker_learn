# Docker Learn Project

这是一个基于 Spring Boot 的 Docker 服务管理和 Markdown 文档管理系统。

## 📋 项目结构

```
docker_learn/
├── pom.xml                        # Maven 项目配置
├── docker-compose.yml             # Docker Compose 主配置（统一管理所有服务）
│
├── src/                           # Java 源代码
│   └── main/
│       ├── java/
│       │   └── org/example/
│       │       ├── Main.java                    # Spring Boot 启动类
│       │       ├── config/
│       │       │   └── StartupListener.java     # 启动监听器（显示访问地址）
│       │       ├── controller/                  # REST API 控制器
│       │       │   ├── HelloController.java
│       │       │   ├── NginxController.java     # Nginx 管理 API
│       │       │   └── MarkdownController.java  # Markdown 文档管理 API
│       │       └── service/                      # 业务服务层
│       │           ├── MarkdownService.java      # Markdown 处理（CommonMark）
│       │           └── NginxProxyManagerService.java
│       └── resources/
│           ├── application.yml                  # Spring Boot 配置
│           └── static/
│               └── index.html                   # API 文档页面
│
├── docker/                        # Docker 服务配置目录
│   ├── README.md                 # Docker 服务总览
│   ├── TEMPLATE.md               # 服务文档模板
│   ├── nginx-proxy-manager/      # Nginx Proxy Manager 服务
│   │   ├── docker-compose.yml
│   │   └── README.md
│   └── portainer/                # Portainer 容器管理工具
│       ├── docker-compose.yml
│       └── README.md
│
├── docs/                         # Markdown 文档存储目录（通过 API 管理）
├── target/                       # Maven 编译输出目录
└── README.md                     # 本文件
```

## 🎯 功能特性

### 1. REST API 服务
- ✅ 纯 REST API 设计，返回 JSON 格式
- ✅ 使用 CommonMark 解析 Markdown
- ✅ 启动时在控制台显示访问地址

### 2. Docker 服务管理
- ✅ 自动扫描 `docker/` 目录下的所有服务
- ✅ 读取每个服务的 README.md 文档
- ✅ 获取服务信息和 compose 文件路径
- ✅ Nginx Proxy Manager 管理 API
- ✅ Docker Compose 命令参考
- ✅ 支持多个 Docker 服务独立管理

### 3. Markdown 文档管理
- ✅ 文档列表查询
- ✅ 文档创建/更新/删除
- ✅ Markdown 转 HTML
- ✅ 文档内容获取（返回 markdown 和 html）

## 🚀 快速开始

### 前置要求

- **Java 11+**
- **Maven 3.6+**
- **Docker & Docker Compose**

### 1. 启动 Docker 服务

#### 方式一：独立管理（推荐）

```bash
# 启动 Nginx Proxy Manager
cd docker/nginx-proxy-manager
docker compose up -d

# 启动 Portainer（可选）
cd ../portainer
docker compose up -d
```

#### 方式二：统一管理

```bash
# 在项目根目录，启动所有服务
docker compose up -d

# 或启动指定服务
docker compose up -d nginx-proxy-manager
```

**访问地址：**
- **Nginx Proxy Manager**: http://localhost:81
  - 默认账号: `admin@example.com`
  - 默认密码: `changeme`
- **Portainer**: http://localhost:9000

### 2. 启动 Spring Boot 应用

```bash
# 编译项目
mvn clean package

# 运行应用
mvn spring-boot:run

# 或直接运行 jar
java -jar target/docker_learn-1.0-SNAPSHOT.jar
```

启动成功后，控制台会显示：

```
============================================================
🚀 应用启动成功！
============================================================
📍 访问地址:
   本地:   http://localhost:10086
   网络:   http://192.168.x.x:10086
   主机名: http://hostname:10086

📚 API 文档:
   http://localhost:10086/

🔗 Nginx Proxy Manager:
   http://localhost:81
============================================================
```

## 📚 API 接口文档

访问 http://localhost:10086 查看完整的 API 文档页面。

### Docker 服务管理 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/docker/services` | 获取所有 Docker 服务列表（自动扫描 docker/ 目录） |
| GET | `/api/docker/services/{serviceName}` | 获取指定服务信息（包含 README 内容） |
| GET | `/api/docker/services/{serviceName}/readme` | 获取指定服务的 README.md 内容 |

### Nginx 管理 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/nginx/info` | 获取 Nginx Proxy Manager 信息 |
| GET | `/api/nginx/commands` | 获取 Docker Compose 命令列表 |

### Markdown 文档 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/docs/list` | 获取所有文档列表 |
| GET | `/api/docs/{filename}` | 获取文档内容（返回 markdown 和 html） |
| POST | `/api/docs/{filename}` | 创建或更新文档 |
| DELETE | `/api/docs/{filename}` | 删除文档 |
| POST | `/api/docs/convert` | 将 Markdown 文本转换为 HTML |

## 💡 API 使用示例

### 获取所有 Docker 服务列表

```bash
curl http://localhost:10086/api/docker/services
```

响应：
```json
[
  {
    "name": "nginx-proxy-manager",
    "title": "Nginx Proxy Manager - Docker Compose 部署指南",
    "description": "Nginx Proxy Manager 是一个基于 Docker 的反向代理管理工具...",
    "path": "docker/nginx-proxy-manager",
    "composeFile": "docker/nginx-proxy-manager/docker-compose.yml",
    "hasCompose": true,
    "readme": "# Nginx Proxy Manager...\n..."
  },
  {
    "name": "portainer",
    "title": "Portainer - Docker 容器管理工具",
    "description": "Portainer 是一个轻量级的 Docker 容器管理工具...",
    "path": "docker/portainer",
    "composeFile": "docker/portainer/docker-compose.yml",
    "hasCompose": true,
    "readme": "# Portainer...\n..."
  }
]
```

### 获取指定服务信息

```bash
curl http://localhost:10086/api/docker/services/nginx-proxy-manager
```

### 获取服务的 README

```bash
curl http://localhost:10086/api/docker/services/nginx-proxy-manager/readme
```

### 获取文档列表

```bash
curl http://localhost:10086/api/docs/list
```

响应：
```json
["nginx-proxy-manager-guide.md", "docker-compose-guide.md"]
```

### 获取文档内容

```bash
curl http://localhost:10086/api/docs/nginx-proxy-manager-guide.md
```

响应：
```json
{
  "filename": "nginx-proxy-manager-guide.md",
  "markdown": "# Nginx Proxy Manager 使用指南\n\n...",
  "html": "<h1>Nginx Proxy Manager 使用指南</h1>\n..."
}
```

### 创建/更新文档

```bash
curl -X POST http://localhost:10086/api/docs/test.md \
  -H "Content-Type: application/json" \
  -d '{"content": "# Test Document\n\nThis is a test."}'
```

响应：
```json
{
  "success": true,
  "message": "文档保存成功",
  "filename": "test.md"
}
```

### 转换 Markdown 为 HTML

```bash
curl -X POST http://localhost:10086/api/docs/convert \
  -H "Content-Type: application/json" \
  -d '{"markdown": "# Hello\n\nWorld"}'
```

响应：
```json
{
  "html": "<h1>Hello</h1>\n<p>World</p>\n"
}
```

### 删除文档

```bash
curl -X DELETE http://localhost:10086/api/docs/test.md
```

## 🛠️ 技术栈

- **Spring Boot 2.7.18** - Java 11 兼容版本
- **CommonMark 0.21.0** - Markdown 解析器（Java 实现）
- **Docker Compose** - 容器编排
- **Maven** - 项目构建工具

## 📖 Docker 服务管理

所有 Docker 服务配置都在 `docker/` 目录下，每个服务都有独立的配置和文档。

### 添加新服务

1. 在 `docker/` 目录下创建服务目录
2. 创建 `docker-compose.yml` 配置文件
3. 参考 `docker/TEMPLATE.md` 创建 `README.md` 文档
4. （可选）在根目录 `docker-compose.yml` 中添加服务配置

详细说明请查看：[docker/README.md](docker/README.md)

## 📝 开发说明

### 项目配置

- **端口**: 10086（避免与常用端口冲突）
- **Java 版本**: 11
- **Spring Boot 版本**: 2.7.18

### 编译和运行

```bash
# 编译
mvn clean compile

# 打包
mvn clean package

# 运行测试
mvn test

# 运行应用
mvn spring-boot:run
```

### 目录说明

- `src/main/java/` - Java 源代码
- `src/main/resources/` - 配置文件和静态资源
- `docs/` - Markdown 文档存储目录（通过 API 管理）
- `docker/` - Docker 服务配置目录
- `target/` - Maven 编译输出（已加入 .gitignore）

## 🔗 参考文档

- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [CommonMark 规范](https://commonmark.org/)
- [Docker Compose 文档](https://docs.docker.com/compose/)
- [Nginx Proxy Manager 文档](https://nginxproxymanager.com/guide/)

## 📄 许可证

MIT License

---

**最后更新**: 2025-12-10
