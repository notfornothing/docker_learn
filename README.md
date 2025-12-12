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
│   ├── README.md                 # Docker Compose 基础教程
│   ├── TEMPLATE.md               # 服务文档模板
│   ├── nginx-proxy-manager/      # Nginx Proxy Manager 服务
│   │   ├── docker-compose.yml
│   │   └── README.md
│   ├── portainer/                # Portainer 容器管理工具
│   │   ├── docker-compose.yml
│   │   └── README.md
│   ├── neo4j/                    # Neo4j 图数据库
│   │   ├── docker-compose.yml
│   │   └── README.md
│   ├── mysql/                     # MySQL 8.0 数据库
│   │   ├── docker-compose.yml
│   │   └── README.md
│   └── nacos/                     # Nacos 服务发现和配置管理
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
- **Neo4j Browser**: http://localhost:7474
  - 默认用户名: `neo4j`
  - 默认密码: `password`
- **Nacos Console**: http://localhost:8848/nacos
  - 默认用户名: `nacos`
  - 默认密码: `nacos`
- **MySQL**: localhost:3306
  - 默认用户名: `root`
  - 默认密码: `123456`

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

## 📚 Docker 知识库

本项目包含完整的 Docker 学习文档，涵盖 Docker Compose、命令使用、多架构镜像、Volumes 等核心知识。

### 📖 目录

- [Docker Compose 基础](#docker-compose-基础)
- [Docker 命令详解](#docker-命令详解)
- [Docker 多架构镜像](#docker-多架构镜像)
- [Docker Volumes 详解](#docker-volumes-详解)

---

### 🎯 Docker Compose 基础

#### 最简单的 compose 文件

一个最简单的 `docker-compose.yml` 只需要 3 个参数就能启动：

```yaml
version: '3.8'

services:
  web:
    image: nginx:latest
    ports:
      - "8080:80"
```

**必须的参数：**
1. `version` - compose 文件格式版本（推荐 `3.8`）
2. `services` - 定义服务
3. `image` - 镜像名称

**常用参数：**
- `ports` - 端口映射（格式：`"宿主机端口:容器端口"`）
- `volumes` - 数据卷（持久化数据）
- `environment` - 环境变量
- `container_name` - 容器名称
- `restart` - 重启策略（`always`、`unless-stopped`、`on-failure`、`no`）

#### 启动和重启相关参数

| 参数 | 作用 | 推荐值 |
|------|------|--------|
| `restart` | 重启策略和开机自启 | `unless-stopped` |
| `depends_on` | 控制启动顺序 | `- 服务名` |
| `healthcheck` | 健康检查 | 根据服务类型配置 |
| `stop_grace_period` | 停止宽限期 | `30s`（数据库等） |

**重启策略对比：**

| 策略 | 容器崩溃 | 手动停止 | 服务器重启 | 适用场景 |
|------|---------|---------|-----------|---------|
| `always` | ✅ 重启 | ✅ 重启 | ✅ 自动启动 | 需要一直运行的服务 |
| `unless-stopped` | ✅ 重启 | ❌ 不重启 | ✅ 自动启动 | **推荐，最常用** |
| `on-failure` | ✅ 重启 | ❌ 不重启 | ❌ 不启动 | 测试环境 |
| `no` | ❌ 不重启 | ❌ 不重启 | ❌ 不启动 | 临时服务 |

**完整配置模板：**

```yaml
version: '3.8'

services:
  服务名:
    image: 镜像名:标签
    container_name: 容器名
    restart: unless-stopped  # 开机自启动 + 崩溃重启
    ports:
      - "8080:80"
    environment:
      - 变量名=值
    volumes:
      - 数据卷名:/容器内路径
    depends_on:
      - 依赖的服务名
    healthcheck:
      test: ["CMD", "检查命令"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  数据卷名:
```

**常用命令：**

```bash
# 启动服务（后台运行）
docker compose up -d

# 停止服务
docker compose down

# 停止并删除数据卷（⚠️ 数据会丢失！）
docker compose down -v

# 查看日志
docker compose logs -f 服务名

# 重启服务
docker compose restart 服务名
```

---

### 🔍 Docker 命令详解

#### 核心区别

- **`docker manifest inspect`** = 查询远程仓库（Registry）
- **`docker image inspect`** = 查询本地镜像（Local）

#### 命令规律

| 命令类型 | 操作对象 | 是否需要本地镜像 | 是否访问远程 |
|---------|---------|----------------|-------------|
| `docker image *` | 本地镜像 | ✅ 必须 | ❌ 不访问 |
| `docker manifest *` | 远程仓库 | ❌ 不需要 | ✅ 访问 |
| `docker pull` | 远程→本地 | ❌ 不需要 | ✅ 访问 |
| `docker push` | 本地→远程 | ✅ 必须 | ✅ 访问 |
| `docker images` | 本地镜像列表 | - | ❌ 不访问 |
| `docker search` | 远程仓库搜索 | ❌ 不需要 | ✅ 访问 |

#### 快速判断

- **`image` 开头** → 操作本地镜像
- **`manifest` 开头** → 操作远程 Manifest
- **`pull/push`** → 远程操作
- **不带前缀的通用命令** → 通常操作本地

**示例：**

```bash
# 远程查询（不需要本地有镜像）
docker manifest inspect nginx:latest

# 本地查询（必须本地有镜像）
docker image inspect nginx:latest

# 如果本地没有，会报错：Error: No such image
```

---

### 🏗️ Docker 多架构镜像

#### Index Digest vs Manifest Digest

- **Index Digest（索引摘要）** - 多架构镜像的"目录"摘要
  - 包含所有架构的 Manifest 引用
  - 1 个标签 = 1 个 Index Digest

- **Manifest Digest（清单摘要）** - 单个架构镜像的"内容"摘要
  - 每个架构都有自己的 Manifest Digest
  - 1 个架构 = 1 个 Manifest Digest

**为什么不一样？**
- Index Digest = 一本书的目录页的哈希值
- Manifest Digest = 书中某一章节内容的哈希值

#### 指定拉取架构

```bash
# 方法1: 使用 --platform 参数
docker pull --platform linux/amd64 nacos/nacos-server:v1.4.8-slim
docker pull --platform linux/arm64 nacos/nacos-server:v1.4.8-slim

# 方法2: 使用 Digest（精确指定）
docker pull nacos/nacos-server@sha256:e03a115433a2...  # amd64
docker pull nacos/nacos-server@sha256:7336edbb236f...  # arm64
```

**在 compose 中指定：**

```yaml
services:
  nacos:
    image: nacos/nacos-server:v1.4.8-slim
    platform: linux/amd64  # 指定架构
```

#### 查看远程仓库架构

```bash
# 查看所有架构
docker manifest inspect nacos/nacos-server:v1.4.8-slim

# 只显示架构列表
docker manifest inspect nacos/nacos-server:v1.4.8-slim | jq '.manifests[].platform'
```

---

### 💾 Docker Volumes 详解

#### 命名卷的独立性

**核心要点：**
1. ✅ **命名卷是独立的** - 不绑定到容器
2. ✅ **删除容器不会删除卷** - 数据会保留
3. ✅ **下次启动会复用同一个卷** - 数据还在
4. ❌ **只有明确删除卷才会丢失数据** - 使用 `-v` 参数或 `docker volume rm`

**实际测试：**

```bash
# 1. 启动服务
docker compose up -d

# 2. 创建一些数据（写入卷）

# 3. 删除容器
docker compose down

# 4. 查看卷（还在！）
docker volume ls | grep nacos

# 5. 重新启动
docker compose up -d

# 6. 数据恢复！配置还在！
```

#### 卷名的隔离机制

Docker Compose 会自动给卷名加前缀：`项目名_卷名`

**命名规则：**
- 默认使用**目录名**作为项目名
- 或者使用 `-p` 参数指定项目名

**示例：**

```yaml
# docker/mysql/docker-compose.yml
volumes:
  - data:/var/lib/mysql
volumes:
  data:
```

**实际卷名：** `mysql_data`（项目名_卷名）

**不同目录的同名卷不会冲突：**
- `docker/mysql/docker-compose.yml` → `mysql_data`
- `docker/redis/docker-compose.yml` → `redis_data`
- ✅ **不会冲突，数据独立**

**同一项目内的同名卷会共享：**
- 两个容器使用同一个卷名 → 共享数据（这是设计如此）

#### 在不同 compose 文件中复用卷

```yaml
# 文件 1：创建卷
volumes:
  mysql_data:

# 文件 2：复用卷
volumes:
  mysql_data:
    external: true
    name: mysql_mysql_data  # 使用已存在的卷名
```

#### 查看和管理卷

```bash
# 查看所有卷
docker volume ls

# 查看卷的详细信息
docker volume inspect 卷名

# 删除卷（⚠️ 数据会丢失！）
docker volume rm 卷名

# 删除未使用的卷
docker volume prune
```

#### 对比表

| 操作 | 容器状态 | 卷状态 | 数据状态 |
|------|---------|--------|---------|
| `docker rm 容器名` | ❌ 删除 | ✅ 保留 | ✅ 保留 |
| `docker compose down` | ❌ 删除 | ✅ 保留 | ✅ 保留 |
| `docker compose down -v` | ❌ 删除 | ❌ 删除 | ❌ 丢失 |
| `docker volume rm 卷名` | ✅ 保留 | ❌ 删除 | ❌ 丢失 |

**记忆口诀：**
> **卷是独立的，容器只是租客**  
> **删除租客（容器），房子（卷）还在**  
> **只有拆房子（删除卷），数据才丢失**

---

### 📚 详细文档

更多详细信息请查看：

- [Docker Compose 基础教程](docker/README.md) - 完整的 Compose 参数说明和示例
- [Docker 命令详解](docker/DOCKER_COMMANDS_GUIDE.md) - 本地 vs 远程命令对比
- [Docker 多架构镜像](docker/DOCKER_MULTIARCH_GUIDE.md) - Index Digest、Manifest Digest 详解
- [Docker Volumes 指南](docker/VOLUMES_GUIDE.md) - 命名卷复用和共享
- [Volumes 和容器关系](docker/VOLUMES_CONTAINER_RELATION.md) - 卷的生命周期
- [Volumes 命名和隔离](docker/VOLUMES_NAMING_ISOLATION.md) - 卷名生成规则

---

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
