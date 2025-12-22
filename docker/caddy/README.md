# Caddy - 自动 HTTPS 的 Web 服务器

Caddy 是一个现代化的 Web 服务器，具有自动 HTTPS、简洁配置和强大的扩展性。

## 📖 简介

**Caddy** 是一个支持自动 HTTPS 的开源 Web 服务器，配置简单，适合快速部署和管理网站。

### 主要特性

- ✅ 自动 HTTPS（Let's Encrypt）
- ✅ HTTP/2 和 HTTP/3 支持
- ✅ 简洁的 Caddyfile 配置
- ✅ 反向代理功能
- ✅ 内置压缩和缓存
- ✅ 支持 WebSocket
- ✅ 轻量级，性能优秀

## 🚀 快速开始

### 1. 创建 Caddyfile

创建 `docker/caddy/Caddyfile`:

```
example.com {
    reverse_proxy backend:8080
}

:80 {
    respond "Hello from Caddy"
}
```

### 2. 启动服务

```bash
cd docker/caddy
docker compose up -d
```

### 3. 端口说明

| 端口 | 用途 |
|------|------|
| 80 | HTTP |
| 443 | HTTPS |
| 2019 | 管理 API（可选） |

## ⚙️ 配置说明

Caddy 使用 `Caddyfile` 进行配置，语法简洁：

```
# 反向代理
example.com {
    reverse_proxy localhost:8080
}

# 静态文件
example.com {
    root * /var/www/html
    file_server
}

# 负载均衡
example.com {
    reverse_proxy backend1:8080 backend2:8080 backend3:8080
}
```

## 🔧 常用操作

### 重新加载配置

```bash
docker compose exec caddy caddy reload --config /etc/caddy/Caddyfile
```

### 验证配置

```bash
docker compose exec caddy caddy validate --config /etc/caddy/Caddyfile
```

## 📚 参考链接

- [官方文档](https://caddyserver.com/docs/)
- [GitHub](https://github.com/caddyserver/caddy)
- [Docker Hub](https://hub.docker.com/_/caddy)

---

**最后更新**: 2025-12-10





