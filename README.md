# Docker Learn Project

这是一个学习 Docker 和 Spring Boot 的项目，包含 Nginx Proxy Manager 的管理 API 和 Markdown 文档管理系统。

## 项目结构

```
docker_learn/
├── docker-compose.yml          # Docker Compose 主配置（统一管理所有服务）
├── docker/                     # Docker 服务目录
│   ├── README.md              # Docker 服务总览
│   ├── TEMPLATE.md            # 服务文档模板
│   ├── nginx-proxy-manager/   # Nginx Proxy Manager 服务
│   │   ├── docker-compose.yml
│   │   └── README.md
│   └── portainer/             # Portainer 容器管理工具
│       ├── docker-compose.yml
│       └── README.md
├── docker_learn/               # Spring Boot 应用
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── org/example/
│   │   │   │       ├── Main.java                    # 启动类
│   │   │   │       ├── controller/                  # 控制器
│   │   │   │       │   ├── HelloController.java
│   │   │   │       │   ├── IndexController.java     # 首页
│   │   │   │       │   ├── NginxController.java     # Nginx 管理 API
│   │   │   │       │   └── MarkdownController.java  # Markdown 文档管理 API
│   │   │   │       └── service/                     # 服务层
│   │   │   │           ├── MarkdownService.java     # Markdown 处理（使用 CommonMark）
│   │   │   │           └── NginxProxyManagerService.java
│   │   │   └── resources/
│   │   │       ├── application.yml                  # 应用配置
│   │   │       └── templates/
│   │   │           └── index.html                   # API 文档页面（唯一 HTML）
│   │   └── test/
│   └── pom.xml
└── docs/                       # Markdown 文档目录
    ├── nginx-proxy-manager-guide.md
    └── docker-compose-guide.md
```

## 功能特性

### 1. REST API 设计
- 纯 REST API，无 HTML 页面（除了 API 文档首页）
- 使用 CommonMark 处理 Markdown
- JSON 格式响应

### 2. Nginx Proxy Manager 管理 API
- 获取 Nginx Proxy Manager 基本信息
- Docker Compose 命令参考

### 3. Markdown 文档管理 API
- 文档列表查询
- 文档创建/更新/删除
- Markdown 转 HTML
- 文档内容获取（返回 markdown 和 html）

## 快速开始

### 前置要求

- Java 11+
- Maven 3.6+
- Docker & Docker Compose

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

访问地址：
- **Nginx Proxy Manager**: http://localhost:81
  - 默认账号: admin@example.com
  - 默认密码: changeme
- **Portainer**: http://localhost:9000

### 2. 启动 Spring Boot 应用

```bash
cd docker_learn
mvn spring-boot:run
```

访问应用: http://localhost:8080

## API 接口文档

访问 http://localhost:8080 查看完整的 API 文档。

### Nginx 相关 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/nginx/info` | 获取 Nginx Proxy Manager 信息 |
| GET | `/api/nginx/commands` | 获取 Docker Compose 命令列表 |

### Markdown 文档相关 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/docs/list` | 获取所有文档列表 |
| GET | `/api/docs/{filename}` | 获取文档内容（返回 markdown 和 html） |
| POST | `/api/docs/{filename}` | 创建或更新文档 |
| DELETE | `/api/docs/{filename}` | 删除文档 |
| POST | `/api/docs/convert` | 将 Markdown 文本转换为 HTML |

## API 使用示例

### 获取文档列表

```bash
curl http://localhost:8080/api/docs/list
```

响应：
```json
["nginx-proxy-manager-guide.md", "docker-compose-guide.md"]
```

### 获取文档内容

```bash
curl http://localhost:8080/api/docs/nginx-proxy-manager-guide.md
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
curl -X POST http://localhost:8080/api/docs/test.md \
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
curl -X POST http://localhost:8080/api/docs/convert \
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
curl -X DELETE http://localhost:8080/api/docs/test.md
```

响应：
```json
{
  "success": true,
  "message": "文档删除成功"
}
```

## 技术栈

- **Spring Boot 2.7.18** - Java 11 兼容版本
- **CommonMark 0.21.0** - Markdown 解析器（Java 实现）
- **Docker Compose** - 容器编排
- **REST API** - 纯 API 设计，无 HTML 页面（除首页）

## 使用说明

### Docker Compose 命令

```bash
# 启动
docker compose up -d

# 停止
docker compose down

# 重启
docker compose restart nginx-proxy-manager

# 查看日志
docker compose logs -f nginx-proxy-manager
```

### Markdown 文档

文档存储在 `docs/` 目录下，通过 API 进行管理：
- 使用 CommonMark 标准解析 Markdown
- 支持所有 CommonMark 标准语法
- 文档以 `.md` 文件形式存储在 `docs/` 目录

## CommonMark 说明

本项目使用 [CommonMark](https://commonmark.org/) 作为 Markdown 解析器：
- CommonMark 是 Markdown 的标准化规范
- 提供一致的解析结果
- Java 实现，性能优秀

## 参考文档

- [Nginx Proxy Manager 官方文档](https://nginxproxymanager.com/guide/)
- [Docker Compose 文档](https://docs.docker.com/compose/)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [CommonMark 规范](https://commonmark.org/)

## 许可证

MIT License
