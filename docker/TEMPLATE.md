# [服务名称] - Docker Compose 部署指南

> 📝 这是一个文档模板，用于创建新的 Docker 服务文档

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

**[服务名称]** 是一个 [服务描述]。

### 主要特性

- ✅ 特性 1
- ✅ 特性 2
- ✅ 特性 3

---

## 🚀 快速开始

### 前置要求

- Docker Engine 20.10+
- Docker Compose 2.0+

### 1. 启动服务

```bash
# 进入配置目录
cd docker/[service-name]

# 启动服务（后台运行）
docker compose up -d

# 查看启动日志
docker compose logs -f
```

### 2. 访问服务

启动成功后，访问以下地址：

- **Web 界面**: http://localhost:[port]
- **默认账号**: `[username]`
- **默认密码**: `[password]`

⚠️ **重要提示**: 首次登录后请立即修改默认密码！

### 3. 端口说明

| 端口 | 用途 | 说明 |
|------|------|------|
| [port1] | [用途] | [说明] |
| [port2] | [用途] | [说明] |

---

## ⚙️ 配置说明

### 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `VAR1` | `default` | 变量说明 |
| `VAR2` | `default` | 变量说明 |

### 数据目录

```
docker/[service-name]/
├── docker-compose.yml    # Compose 配置文件
├── data/                 # 数据目录（自动创建）
└── config/               # 配置目录（可选）
```

### 网络配置

服务使用独立的 Docker 网络 `[network-name]`，便于与其他服务通信。

---

## 🔧 常用操作

### 操作 1

1. 步骤 1
2. 步骤 2
3. 步骤 3

### 操作 2

```bash
# 命令示例
docker compose exec [service-name] [command]
```

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
docker compose restart [service-name]

# 查看服务状态
docker compose ps

# 查看日志（实时）
docker compose logs -f [service-name]

# 查看最近 100 行日志
docker compose logs --tail=100 [service-name]
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
docker compose exec [service-name] sh

# 查看容器信息
docker compose exec [service-name] env
```

---

## 💾 数据管理

### 备份数据

```bash
# 备份数据目录
tar -czf [service-name]-backup-$(date +%Y%m%d).tar.gz \
  docker/[service-name]/data
```

### 恢复数据

```bash
# 停止服务
docker compose down

# 解压备份文件
tar -xzf [service-name]-backup-YYYYMMDD.tar.gz

# 启动服务
docker compose up -d
```

---

## 🔍 故障排查

### 问题 1: 服务无法启动

1. **检查端口占用**
   ```bash
   lsof -i :[port]
   ```

2. **查看日志**
   ```bash
   docker compose logs [service-name]
   ```

3. **检查权限**
   ```bash
   chmod -R 755 docker/[service-name]/data
   ```

### 问题 2: [其他问题]

[问题描述和解决方案]

---

## 📚 参考链接

- [官方文档](https://example.com/docs)
- [GitHub 仓库](https://github.com/example/repo)
- [Docker Hub](https://hub.docker.com/r/example/image)

---

## 📝 更新日志

### YYYY-MM-DD
- 初始版本
- [更新内容]

---

## ⚠️ 注意事项

1. **安全建议**
   - 首次登录后立即修改默认密码
   - 使用防火墙限制管理端口访问
   - 定期更新镜像版本
   - 定期备份数据

2. **性能优化**
   - [优化建议 1]
   - [优化建议 2]

3. **生产环境**
   - 使用固定版本标签而非 `latest`
   - 配置日志轮转
   - 设置资源限制（CPU、内存）
   - 配置监控和告警

---

**最后更新**: YYYY-MM-DD

