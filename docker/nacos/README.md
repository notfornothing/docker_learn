# Nacos - Docker Compose 部署指南

## 📋 目录

- [简介](#简介)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [常用操作](#常用操作)
- [Docker Compose 命令](#docker-compose-命令)

---

## 📖 简介

**Nacos** 是阿里巴巴开源的服务发现和配置管理平台。

### 配置说明

- **镜像**: `nacos/nacos-server:v1.4.8-slim`
- **端口**: `8848`
- **模式**: `standalone`（单机模式，最简单）
- **数据持久化**: 使用命名卷

---

## 🚀 快速开始

### 1. 启动服务

```bash
# 进入目录
cd docker/nacos

# 启动 Nacos（后台运行）
docker compose up -d

# 查看启动日志
docker compose logs -f nacos
```

### 2. 访问 Nacos 控制台

启动成功后，访问以下地址：

- **控制台地址**: http://localhost:8848/nacos
- **默认用户名**: `nacos`
- **默认密码**: `nacos`

⚠️ **重要提示**: 首次登录后请立即修改默认密码！

---

## ⚙️ 配置说明

### 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `MODE` | `standalone` | 运行模式（standalone=单机，cluster=集群） |
| `TZ` | `Asia/Shanghai` | 时区设置 |

### 端口说明

| 端口 | 用途 | 说明 |
|------|------|------|
| 8848 | HTTP | Nacos 控制台和 API 端口 |

### 数据卷说明

| 卷名 | 挂载路径 | 说明 |
|------|----------|------|
| `nacos_data` | `/home/nacos/data` | Nacos 数据目录 |
| `nacos_logs` | `/home/nacos/logs` | Nacos 日志目录 |

---

## 🔧 常用操作

### 修改默认密码

1. 访问 http://localhost:8848/nacos
2. 使用默认账号登录（nacos/nacos）
3. 在控制台修改密码

### 配置 MySQL 数据库（可选）

如果需要使用 MySQL 存储数据（推荐生产环境），可以修改配置：

```yaml
environment:
  - MODE=standalone
  - SPRING_DATASOURCE_PLATFORM=mysql
  - MYSQL_SERVICE_HOST=mysql  # MySQL 服务地址
  - MYSQL_SERVICE_PORT=3306
  - MYSQL_SERVICE_DB_NAME=nacos
  - MYSQL_SERVICE_USER=root
  - MYSQL_SERVICE_PASSWORD=123456
```

**注意**: 需要先创建 Nacos 数据库，并导入初始化 SQL。

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
docker compose restart nacos

# 查看服务状态
docker compose ps

# 查看日志（实时）
docker compose logs -f nacos
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

## 📝 配置示例

### 完整配置（使用 MySQL）

```yaml
version: '3.8'

services:
  nacos:
    image: nacos/nacos-server:v1.4.8-slim
    container_name: nacos
    restart: unless-stopped
    ports:
      - "8848:8848"
    environment:
      - MODE=standalone
      - TZ=Asia/Shanghai
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=mysql
      - MYSQL_SERVICE_PORT=3306
      - MYSQL_SERVICE_DB_NAME=nacos
      - MYSQL_SERVICE_USER=root
      - MYSQL_SERVICE_PASSWORD=123456
    volumes:
      - nacos_data:/home/nacos/data
      - nacos_logs:/home/nacos/logs
    depends_on:
      - mysql

  mysql:
    image: mysql:8.0
    container_name: nacos-mysql
    restart: unless-stopped
    ports:
      - "3307:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=123456
      - MYSQL_DATABASE=nacos
    volumes:
      - nacos_mysql_data:/var/lib/mysql

volumes:
  nacos_data:
  nacos_logs:
  nacos_mysql_data:
```

---

## ⚠️ 注意事项

1. **密码安全**
   - 生产环境必须修改默认密码
   - 使用强密码

2. **数据备份**
   - 定期备份 `nacos_data` 卷
   - 如果使用 MySQL，也要备份数据库

3. **性能优化**
   - 单机模式适合开发和小规模使用
   - 生产环境建议使用集群模式 + MySQL

4. **内存配置**
   - 默认 JVM 内存可能不够，可以添加：
     ```yaml
     environment:
       - JVM_XMS=512m
       - JVM_XMX=512m
     ```

---

## 📚 参考链接

- [Nacos 官方文档](https://nacos.io/docs/latest/)
- [Nacos Docker 快速开始](https://nacos.io/docs/latest/quickstart/quick-start-docker/)
- [Nacos Docker Hub](https://hub.docker.com/r/nacos/nacos-server)

---

**最后更新**: 2025-12-10




