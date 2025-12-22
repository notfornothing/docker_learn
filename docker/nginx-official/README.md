# Nginx 官方镜像 - 直接使用配置文件

如果你需要完全控制 Nginx 配置，可以直接使用官方 Nginx Docker 镜像，通过挂载配置文件的方式管理。

## 📖 简介

**Nginx 官方镜像** 允许你直接编辑 `nginx.conf` 和站点配置文件，完全控制 Nginx 的行为。

### 主要特性

- ✅ 完全控制 Nginx 配置
- ✅ 支持直接编辑 `nginx.conf`
- ✅ 支持导入现有配置文件
- ✅ 轻量级，官方维护
- ✅ 灵活性强

## 🚀 快速开始

### 1. 创建配置文件目录

```bash
mkdir -p docker/nginx-official/conf.d
mkdir -p docker/nginx-official/ssl
mkdir -p docker/nginx-official/html
mkdir -p docker/nginx-official/logs
```

### 2. 创建 nginx.conf

创建 `docker/nginx-official/nginx.conf`:

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    include /etc/nginx/conf.d/*.conf;
}
```

### 3. 创建站点配置

创建 `docker/nginx-official/conf.d/default.conf`:

```nginx
server {
    listen 80;
    server_name localhost;

    location / {
        root /usr/share/nginx/html;
        index index.html index.htm;
    }

    # 反向代理示例
    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### 4. 启动服务

```bash
cd docker/nginx-official
docker compose up -d
```

## 📝 配置文件说明

- `nginx.conf` - 主配置文件
- `conf.d/*.conf` - 站点配置文件（可以创建多个）
- `ssl/` - SSL 证书目录
- `html/` - 静态文件目录
- `logs/` - 日志目录

## 🔧 常用操作

### 重新加载配置

```bash
docker compose exec nginx nginx -s reload
```

### 测试配置文件

```bash
docker compose exec nginx nginx -t
```

### 查看日志

```bash
docker compose logs -f nginx
```

## ⚠️ 注意事项

- 配置文件修改后需要重新加载：`nginx -s reload`
- 建议先测试配置：`nginx -t`
- 配置文件语法错误会导致容器无法启动

---

**最后更新**: 2025-12-10





