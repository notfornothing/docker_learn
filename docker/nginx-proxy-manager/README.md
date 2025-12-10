# Nginx Proxy Manager - Docker Compose 部署指南

## 📋 目录

- [简介](#简介)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [常用操作](#常用操作)
- [Docker Compose 命令](#docker-compose-命令)
- [数据管理](#数据管理)
- [故障排查](#故障排查)
- [参考链接](#参考链接)

---

## 📖 简介

**Nginx Proxy Manager** 是一个基于 Docker 的反向代理管理工具，提供了美观的 Web 界面来管理 Nginx 配置，无需手动编辑配置文件。

### 主要特性

- ✅ 美观的 Web 管理界面
- ✅ 简单的反向代理配置
- ✅ 免费的 Let's Encrypt SSL 证书
- ✅ 访问控制和基础认证
- ✅ 用户管理和权限控制
- ✅ 审计日志

---

## 🚀 快速开始

### 前置要求

- Docker Engine 20.10+
- Docker Compose 2.0+

### 1. 启动服务

```bash
# 进入配置目录
cd docker/nginx-proxy-manager

# 启动服务（后台运行）
docker compose up -d

# 查看启动日志
docker compose logs -f
```

### 2. 访问管理界面

启动成功后，访问以下地址：

- **管理界面**: http://localhost:81
- **默认账号**: `admin@example.com`
- **默认密码**: `changeme`

⚠️ **重要提示**: 首次登录后请立即修改默认密码！

### 3. 端口说明

| 端口 | 用途 | 说明 |
|------|------|------|
| 80 | HTTP | 处理 HTTP 请求 |
| 443 | HTTPS | 处理 HTTPS 请求 |
| 81 | Admin UI | 管理界面端口 |

---

## ⚙️ 配置说明

### 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `TZ` | `Asia/Shanghai` | 时区设置 |
| `DB_MYSQL_HOST` | - | MySQL 主机（可选） |
| `DB_MYSQL_PORT` | 3306 | MySQL 端口（可选） |
| `DB_MYSQL_USER` | - | MySQL 用户名（可选） |
| `DB_MYSQL_PASSWORD` | - | MySQL 密码（可选） |
| `DB_MYSQL_NAME` | - | MySQL 数据库名（可选） |

### 数据目录

```
docker/nginx-proxy-manager/
├── docker-compose.yml    # Compose 配置文件
├── data/                 # 配置数据目录（自动创建）
│   ├── database.sqlite   # SQLite 数据库（默认）
│   └── ...
└── letsencrypt/          # SSL 证书目录（自动创建）
    └── ...
```

### 网络配置

服务使用独立的 Docker 网络 `nginx-network`，便于与其他服务通信。

---

## 🔧 常用操作

### 创建反向代理

1. 登录管理界面 (http://localhost:81)
2. 点击 **"Proxy Hosts"** → **"Add Proxy Host"**
3. 填写配置信息：
   - **Domain Names**: 域名（如 `example.com`）
   - **Scheme**: `http` 或 `https`
   - **Forward Hostname/IP**: 目标服务器地址
   - **Forward Port**: 目标服务器端口
   - **Cache Assets**: 是否缓存静态资源
   - **Block Common Exploits**: 是否阻止常见攻击
   - **Websockets Support**: 是否支持 WebSocket
4. 点击 **"Save"** 保存

### 申请 SSL 证书

1. 在创建/编辑代理主机时，切换到 **"SSL"** 标签
2. 选择 **"Request a new SSL Certificate with Let's Encrypt"**
3. 填写配置：
   - **Email Address**: 邮箱地址（用于证书到期通知）
   - **Agree to Let's Encrypt Terms of Service**: 勾选同意
   - **Use a DNS Challenge**: 可选，使用 DNS 验证
4. 点击 **"Save"** 保存
5. 等待证书申请完成（通常需要几分钟）

### 配置访问控制

1. 在代理主机配置中，切换到 **"Access List"** 标签
2. 选择或创建访问控制列表
3. 配置 IP 白名单或黑名单
4. 保存配置

### 配置基础认证

1. 在代理主机配置中，切换到 **"Access List"** 标签
2. 创建访问列表并启用 **"HTTP Auth"**
3. 添加用户名和密码
4. 保存配置

---

## 🐳 Docker Compose 命令

### 基本操作

```bash
# 启动服务（后台运行）
docker compose up -d

# 启动服务（前台运行，查看日志）
docker compose up

# 停止服务
docker compose down

# 停止服务并删除数据卷（谨慎使用）
docker compose down -v

# 重启服务
docker compose restart nginx-proxy-manager

# 查看服务状态
docker compose ps

# 查看日志（实时）
docker compose logs -f nginx-proxy-manager

# 查看最近 100 行日志
docker compose logs --tail=100 nginx-proxy-manager
```

### 更新服务

```bash
# 拉取最新镜像
docker compose pull

# 停止旧容器
docker compose down

# 启动新容器
docker compose up -d

# 清理未使用的镜像
docker image prune -a
```

### 进入容器

```bash
# 进入容器 shell
docker compose exec nginx-proxy-manager sh

# 查看容器信息
docker compose exec nginx-proxy-manager env
```

---

## 💾 数据管理

### 备份数据

```bash
# 备份配置和证书
tar -czf nginx-proxy-manager-backup-$(date +%Y%m%d).tar.gz \
  docker/nginx-proxy-manager/data \
  docker/nginx-proxy-manager/letsencrypt
```

### 恢复数据

```bash
# 停止服务
docker compose down

# 解压备份文件
tar -xzf nginx-proxy-manager-backup-YYYYMMDD.tar.gz

# 启动服务
docker compose up -d
```

### 数据目录说明

- `data/`: 包含所有配置、数据库、用户数据
- `letsencrypt/`: 包含所有 SSL 证书和密钥

---

## 🔍 故障排查

### 服务无法启动

1. **检查端口占用**
   ```bash
   # 检查端口是否被占用
   lsof -i :80
   lsof -i :443
   lsof -i :81
   ```

2. **查看日志**
   ```bash
   docker compose logs nginx-proxy-manager
   ```

3. **检查权限**
   ```bash
   # 确保数据目录有正确的权限
   chmod -R 755 docker/nginx-proxy-manager/data
   chmod -R 755 docker/nginx-proxy-manager/letsencrypt
   ```

### SSL 证书申请失败

1. **检查域名解析**
   ```bash
   # 确保域名正确解析到服务器 IP
   nslookup your-domain.com
   ```

2. **检查防火墙**
   - 确保端口 80 和 443 对外开放
   - Let's Encrypt 需要通过 HTTP-01 验证

3. **查看证书申请日志**
   - 在管理界面查看证书申请状态
   - 检查 Let's Encrypt 限制（每周每个域名最多 5 次）

### 代理无法访问

1. **检查代理配置**
   - 确认目标服务器地址和端口正确
   - 确认目标服务正在运行

2. **检查网络连接**
   ```bash
   # 从容器内测试连接
   docker compose exec nginx-proxy-manager wget -O- http://target-host:port
   ```

3. **查看 Nginx 错误日志**
   ```bash
   docker compose exec nginx-proxy-manager tail -f /data/logs/error.log
   ```

---

## 📚 参考链接

- [官方文档](https://nginxproxymanager.com/guide/)
- [GitHub 仓库](https://github.com/NginxProxyManager/nginx-proxy-manager)
- [Docker Hub](https://hub.docker.com/r/jc21/nginx-proxy-manager)
- [Let's Encrypt 文档](https://letsencrypt.org/docs/)

---

## 📝 更新日志

### 2025-12-10
- 初始版本
- 添加健康检查配置
- 完善文档说明

---

## ⚠️ 注意事项

1. **安全建议**
   - 首次登录后立即修改默认密码
   - 使用防火墙限制管理端口（81）的访问
   - 定期更新镜像版本
   - 定期备份数据

2. **性能优化**
   - 对于高流量场景，考虑使用 MySQL 数据库
   - 合理配置缓存策略
   - 监控资源使用情况

3. **生产环境**
   - 使用固定版本标签而非 `latest`
   - 配置日志轮转
   - 设置资源限制（CPU、内存）
   - 配置监控和告警

---

**最后更新**: 2025-12-10

