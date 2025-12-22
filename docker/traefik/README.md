# Traefik - 现代反向代理和负载均衡器

Traefik 是一个现代化的 HTTP 反向代理和负载均衡器，专为微服务和容器化应用设计。

## 📖 简介

**Traefik** 是一个开源的边缘路由器，可以自动发现和配置服务。它支持多种后端（Docker、Kubernetes、Consul 等），并提供动态配置。

### 主要特性

- ✅ 自动服务发现（Docker、Kubernetes）
- ✅ 自动 SSL 证书（Let's Encrypt）
- ✅ 动态配置，无需重启
- ✅ 内置监控和指标
- ✅ 支持多种负载均衡算法
- ✅ Web UI 管理界面
- ✅ 支持配置文件或标签配置

## 🚀 快速开始

### 1. 启动服务

```bash
cd docker/traefik
docker compose up -d
```

### 2. 访问管理界面

- **Web UI**: http://localhost:8080
- **API**: http://localhost:8080/api

### 3. 端口说明

| 端口 | 用途 |
|------|------|
| 80 | HTTP |
| 443 | HTTPS |
| 8080 | Web UI 和 API |

## ⚙️ 配置方式

Traefik 支持两种配置方式：

### 方式一：配置文件（traefik.yml）

```yaml
api:
  dashboard: true
  insecure: true

entryPoints:
  web:
    address: ":80"
  websecure:
    address: ":443"

providers:
  docker:
    endpoint: "unix:///var/run/docker.sock"
    exposedByDefault: false
```

### 方式二：Docker 标签（推荐）

在 docker-compose.yml 中使用标签：

```yaml
services:
  app:
    image: myapp:latest
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.app.rule=Host(`app.example.com`)"
      - "traefik.http.routers.app.entrypoints=web"
```

## 🔧 常用操作

### 查看配置

访问 http://localhost:8080/api/http/routers 查看所有路由

### 重新加载配置

Traefik 会自动检测配置变化，无需手动重载

## 📚 参考链接

- [官方文档](https://doc.traefik.io/traefik/)
- [GitHub](https://github.com/traefik/traefik)
- [Docker Hub](https://hub.docker.com/_/traefik)

---

**最后更新**: 2025-12-10





