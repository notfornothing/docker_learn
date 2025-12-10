# Docker 服务部署目录

本目录包含所有 Docker Compose 配置和服务文档。每个服务都有独立的配置文件和文档。

## 📁 目录结构

```
docker/
├── README.md                          # 本文件（总览）
├── TEMPLATE.md                        # 文档模板（用于创建新服务）
│
├── nginx-proxy-manager/               # Nginx Proxy Manager 服务
│   ├── docker-compose.yml            # Compose 配置
│   ├── README.md                      # 服务文档
│   ├── data/                          # 数据目录（自动创建）
│   └── letsencrypt/                   # SSL 证书目录（自动创建）
│
├── portainer/                         # Portainer 容器管理工具
│   ├── docker-compose.yml
│   ├── README.md
│   └── data/                          # 数据目录（自动创建）
│
└── [其他服务]/                        # 其他服务的配置
    ├── docker-compose.yml
    └── README.md
```

## 🚀 使用方式

### 方式一：独立管理（推荐）

每个服务独立管理，互不干扰：

```bash
# 启动 Nginx Proxy Manager
cd docker/nginx-proxy-manager
docker compose up -d

# 启动 Portainer
cd docker/portainer
docker compose up -d

# 查看服务状态
docker compose ps
```

### 方式二：统一管理

使用项目根目录的 `docker-compose.yml` 统一管理所有服务：

```bash
# 在项目根目录
# 启动所有服务
docker compose up -d

# 启动指定服务
docker compose up -d nginx-proxy-manager

# 查看所有服务状态
docker compose ps

# 查看所有服务日志
docker compose logs -f
```

## 📋 服务列表

| 服务名称 | 目录 | 端口 | 说明 | 状态 |
|---------|------|------|------|------|
| Nginx Proxy Manager | `nginx-proxy-manager/` | 80, 443, 81 | 反向代理管理工具 | ✅ |
| Portainer | `portainer/` | 9000, 9443 | Docker 容器管理工具 | ✅ |

## ➕ 添加新服务

### 步骤 1: 创建服务目录

```bash
mkdir -p docker/[service-name]
cd docker/[service-name]
```

### 步骤 2: 创建 docker-compose.yml

参考现有服务的配置，创建 `docker-compose.yml` 文件。

### 步骤 3: 创建 README.md

复制 `TEMPLATE.md` 并填写服务相关信息：

```bash
cp ../TEMPLATE.md README.md
# 编辑 README.md，填写服务信息
```

### 步骤 4: 更新服务列表

在 `docker/README.md` 中添加新服务到服务列表。

### 步骤 5: 更新根目录 docker-compose.yml（可选）

如果使用统一管理方式，在根目录的 `docker-compose.yml` 中添加新服务配置。

## 🔧 通用命令

### 查看所有服务状态

```bash
# 方式一：使用根目录 compose 文件
docker compose ps

# 方式二：分别查看
cd docker/nginx-proxy-manager && docker compose ps
cd docker/portainer && docker compose ps
```

### 查看服务日志

```bash
# 查看指定服务日志
docker compose logs -f [service-name]

# 查看最近 100 行日志
docker compose logs --tail=100 [service-name]
```

### 重启服务

```bash
docker compose restart [service-name]
```

### 停止服务

```bash
docker compose down
```

### 更新服务

```bash
# 拉取最新镜像
docker compose pull

# 停止并重新启动
docker compose down
docker compose up -d
```

## 📝 服务配置规范

每个服务目录应包含：

1. **docker-compose.yml** - Docker Compose 配置文件
   - 使用 `version: '3.8'`
   - 包含服务定义、网络、卷等配置
   - 添加健康检查（如适用）

2. **README.md** - 服务文档
   - 使用 `TEMPLATE.md` 作为模板
   - 包含快速开始、配置说明、常用操作等
   - 保持文档更新

3. **数据目录**（如需要）
   - 使用相对路径 `./data`
   - 在 `.gitignore` 中忽略数据目录

## 🔗 服务间通信

服务可以通过 Docker 网络进行通信：

```yaml
# 在 docker-compose.yml 中
networks:
  - nginx-network  # 共享网络
```

## 📚 参考文档

- [Docker Compose 文档](https://docs.docker.com/compose/)
- [Docker 网络文档](https://docs.docker.com/network/)
- [服务文档模板](./TEMPLATE.md)

---

**最后更新**: 2025-12-10
