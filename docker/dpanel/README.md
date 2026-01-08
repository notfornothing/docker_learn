# DPanel - Docker 容器管理面板

## 📋 目录

- [简介](#简介)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [常用操作](#常用操作)
- [Docker Compose 命令](#docker-compose-命令)
- [参考链接](#参考链接)

---

## 📖 简介

**DPanel** 是一款轻量级的容器管理面板，支持 Docker 和 Podman。提供简洁美观的 Web 界面来管理容器、镜像、网络和卷。

### 主要特性

- ✅ **多语言支持**：内置中、英、日等多国语言包
- ✅ **轻量化**：资源占用极低，适合各种 NAS、路由器、小型服务器等设备
- ✅ **安全性**：通过容器方式运行，无需特权，对宿主机无依赖
- ✅ **应用商店**：支持多种协议的应用商店，并可同时添加多个商店
- ✅ **多主机管理**：支持通过 API（TLS）、SSH 同时管理多个 Docker 客户端
- ✅ **容器快照**：支持容器全量快照备份、恢复、迁移到其他 Docker 环境
- ✅ **容器文件管理**：支持管理容器以及宿主机的文件
- ✅ **容器快速升级**：支持容器镜像升级检测、快速升级
- ✅ **容器标签分组**：通过 Compose、Swarm、标签对容器进行分组筛选
- ✅ **Compose 支持**：支持多种方式添加、管理 Compose 项目
- ✅ **Swarm 支持**：支持管理 Docker Swarm 集群
- ✅ **暗色皮肤**：提供暗色皮肤、菜单位置、字体大小等界面配置
- ✅ **权限系统**：支持多用户、菜单权限、数据权限管理

---

## 🚀 快速开始

### 前置要求

- Docker Engine 20.10+
- Docker Compose 2.0+

### 1. 启动服务

```bash
# 进入配置目录
cd docker/dpanel

# 启动服务（后台运行）
docker compose up -d

# 查看启动日志
docker compose logs -f dpanel
```

### 2. 访问管理界面

启动成功后，访问以下地址：

- **Web 界面**: http://localhost:8807

首次访问需要创建管理员账号。

### 3. 端口说明

| 端口 | 用途 | 说明 |
|------|------|------|
| 8807 | Web UI | HTTP 管理界面端口 |

---

## ⚙️ 配置说明

### 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `TZ` | `Asia/Shanghai` | 时区设置 |

### 数据目录

```
docker/dpanel/
├── docker-compose.yml    # Compose 配置文件
└── README.md             # 本文件
```

### 挂载说明

- `/var/run/docker.sock:/var/run/docker.sock` - Docker socket，用于管理 Docker 容器
- `dpanel_data:/dpanel` - DPanel 配置和数据（命名卷）

### 网络说明

- 使用 `docker-network` 网络，与其他服务在同一网络中可互相访问

---

## 🔧 常用操作

### 首次设置

1. 访问 http://localhost:8807
2. 创建管理员账号（用户名和密码）
3. 开始管理 Docker 容器

### 管理容器

- 查看容器列表
- 启动/停止/重启容器
- 查看容器日志
- 进入容器终端
- 查看容器资源使用情况

### 管理镜像

- 拉取镜像
- 删除镜像
- 查看镜像详情

### 管理 Compose 项目

- 添加 Compose 项目
- 启动/停止 Compose 服务
- 查看 Compose 服务状态
- 编辑 Compose 文件

---

## 🐳 Docker Compose 命令

### 基本操作

```bash
# 启动服务（后台运行）
docker compose up -d

# 启动服务（前台运行，看日志）
docker compose up

# 停止服务
docker compose down

# 停止并删除数据卷（⚠️ 数据会丢失！）
docker compose down -v

# 重启服务
docker compose restart dpanel

# 查看服务状态
docker compose ps

# 查看日志（实时）
docker compose logs -f dpanel
```

### 更新服务

```bash
# 拉取最新镜像
docker compose pull

# 停止旧容器
docker compose down

# 启动新容器
docker compose up -d
```

---

## ⚠️ 注意事项

1. **Docker Socket 权限**
   - DPanel 需要访问 Docker socket 来管理容器
   - 确保 `/var/run/docker.sock` 有正确的权限

2. **数据备份**
   - 定期备份 `dpanel_data` 卷
   - 数据存储在命名卷中，删除容器不会丢失数据

3. **安全建议**
   - 首次登录后立即修改默认密码
   - 生产环境建议配置 HTTPS
   - 限制访问 IP（通过防火墙或反向代理）

4. **资源占用**
   - DPanel 非常轻量，资源占用极低
   - 适合在资源受限的环境中运行

---

## 📚 参考链接

- [DPanel 官方网站](https://dpanel.dev/)
- [DPanel GitHub 仓库](https://github.com/dpanel/dpanel)
- [DPanel Docker Hub](https://hub.docker.com/r/dpanel/dpanel)

---

**最后更新**: 2025-01-27

