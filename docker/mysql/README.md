# MySQL 8.0 - Docker Compose 部署指南

## 📋 目录

- [简介](#简介)
- [快速开始](#快速开始)
- [Volumes 说明](#volumes-说明)
- [常用操作](#常用操作)
- [Docker Compose 命令](#docker-compose-命令)

---

## 📖 简介

**MySQL 8.0** 是最流行的开源关系型数据库之一。

### 配置说明

- **镜像**: `mysql:8.0`
- **端口**: `3306`
- **默认密码**: `123456`（建议修改）
- **数据持久化**: 使用命名卷 `mysql_data`

---

## 🚀 快速开始

### 1. 启动服务

```bash
# 进入目录
cd docker/mysql

# 启动 MySQL（后台运行）
docker compose up -d

# 查看启动日志
docker compose logs -f mysql
```

### 2. 连接数据库

**连接信息：**
- 主机: `localhost`
- 端口: `3306`
- 用户名: `root`
- 密码: `123456`

**连接方式：**

```bash
# 方式1: 使用 mysql 客户端
mysql -h localhost -P 3306 -u root -p123456

# 方式2: 使用 Docker 命令
docker compose exec mysql mysql -u root -p123456

# 方式3: 进入容器后连接
docker compose exec mysql bash
mysql -u root -p123456
```

### 3. 修改默认密码

编辑 `docker-compose.yml`，修改 `MYSQL_ROOT_PASSWORD`：

```yaml
environment:
  - MYSQL_ROOT_PASSWORD=你的新密码
```

然后重启服务：

```bash
docker compose down
docker compose up -d
```

---

## 💾 Volumes 说明

### 当前配置（命名卷）

```yaml
volumes:
  - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

**说明：**
- 使用 **命名卷（Named Volume）**，数据保存在 Docker 管理的目录中
- 容器删除后数据不会丢失
- 数据位置：Docker 默认在 `/var/lib/docker/volumes/` 下

**优点：**
- ✅ 简单，不需要手动创建目录
- ✅ Docker 自动管理
- ✅ 跨平台兼容性好

**缺点：**
- ❌ 数据位置不直观，需要 `docker volume inspect` 查看
- ❌ 备份需要 Docker 命令

---

### 如果没写 volumes 会怎样？

**⚠️ 数据会丢失！**

- 容器删除后，所有数据都会丢失
- 重启容器数据还在，但 `docker compose down` 后数据就没了
- **生产环境必须配置 volumes**

**示例（不推荐，仅测试用）：**

```yaml
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=123456
    # 没有 volumes，数据不会持久化！
```

---

### 推荐的 Volumes 组织方式

#### 方式一：目录映射（推荐用于开发环境）

```yaml
volumes:
  - ./data:/var/lib/mysql        # 数据目录
  - ./conf:/etc/mysql/conf.d     # 配置文件目录（可选）
  - ./logs:/var/log/mysql        # 日志目录（可选）
```

**目录结构：**
```
docker/mysql/
├── docker-compose.yml
├── README.md
├── data/          # MySQL 数据文件（自动创建）
├── conf/          # 自定义配置（可选）
└── logs/          # 日志文件（可选）
```

**优点：**
- ✅ 数据位置直观，直接看到 `data/` 文件夹
- ✅ 方便备份（直接复制 `data/` 文件夹）
- ✅ 方便查看和调试

**缺点：**
- ❌ 需要手动创建目录（或 Docker 自动创建）
- ❌ 权限问题（可能需要 `chmod`）

---

#### 方式二：命名卷（推荐用于生产环境）

```yaml
volumes:
  - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

**优点：**
- ✅ Docker 自动管理，不需要手动创建目录
- ✅ 跨平台兼容性好
- ✅ 适合生产环境

**缺点：**
- ❌ 数据位置不直观

---

#### 方式三：统一数据目录（推荐用于多服务管理）

```yaml
volumes:
  - ../data/mysql:/var/lib/mysql
```

**目录结构：**
```
docker/
├── mysql/
│   ├── docker-compose.yml
│   └── README.md
├── redis/
│   ├── docker-compose.yml
│   └── README.md
└── data/              # 统一数据目录
    ├── mysql/         # MySQL 数据
    ├── redis/         # Redis 数据
    └── neo4j/         # Neo4j 数据
```

**优点：**
- ✅ 所有服务数据集中管理
- ✅ 方便统一备份
- ✅ 目录结构清晰

---

### 📁 推荐的目录分类

#### 方案 A：每个服务独立目录（当前项目使用）

```
docker/
├── mysql/
│   ├── docker-compose.yml
│   ├── README.md
│   └── data/          # MySQL 数据
├── redis/
│   ├── docker-compose.yml
│   └── data/          # Redis 数据
└── neo4j/
    ├── docker-compose.yml
    └── data/          # Neo4j 数据
```

**适用场景：** 服务独立管理，数据分散

---

#### 方案 B：统一数据目录

```
docker/
├── mysql/
│   ├── docker-compose.yml
│   └── README.md
├── redis/
│   ├── docker-compose.yml
│   └── README.md
└── data/              # 统一数据目录
    ├── mysql/
    ├── redis/
    └── neo4j/
```

**适用场景：** 需要统一备份和管理

---

#### 方案 C：分类数据目录（推荐）

```
docker/
├── mysql/
│   ├── docker-compose.yml
│   └── README.md
├── redis/
│   ├── docker-compose.yml
│   └── README.md
└── data/
    ├── databases/     # 数据库类
    │   ├── mysql/
    │   └── postgres/
    ├── caches/        # 缓存类
    │   └── redis/
    └── files/         # 文件存储类
        └── uploads/
```

**适用场景：** 服务较多，需要分类管理

---

## 🔧 常用操作

### 创建数据库和用户

```sql
-- 连接 MySQL
mysql -h localhost -P 3306 -u root -p123456

-- 创建数据库
CREATE DATABASE mydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'myuser'@'%' IDENTIFIED BY 'mypassword';

-- 授权
GRANT ALL PRIVILEGES ON mydb.* TO 'myuser'@'%';

-- 刷新权限
FLUSH PRIVILEGES;
```

### 导入 SQL 文件

```bash
# 方式1: 从宿主机导入
docker compose exec -T mysql mysql -u root -p123456 < backup.sql

# 方式2: 从容器内导入
docker compose exec mysql bash
mysql -u root -p123456 < /path/to/backup.sql
```

### 导出数据

```bash
# 导出整个数据库
docker compose exec mysql mysqldump -u root -p123456 mydb > backup.sql

# 导出指定表
docker compose exec mysql mysqldump -u root -p123456 mydb mytable > table.sql
```

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
docker compose restart mysql

# 查看服务状态
docker compose ps

# 查看日志（实时）
docker compose logs -f mysql
```

### 数据备份

```bash
# 备份数据库
docker compose exec mysql mysqldump -u root -p123456 --all-databases > backup-$(date +%Y%m%d).sql

# 备份数据卷（命名卷）
docker run --rm \
  -v docker_mysql_mysql_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/mysql-backup-$(date +%Y%m%d).tar.gz -C /data .
```

---

## 📝 配置示例

### 完整配置（目录映射方式）

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: mysql
    restart: unless-stopped
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=123456
      - TZ=Asia/Shanghai
    volumes:
      - ./data:/var/lib/mysql           # 数据目录
      - ./conf:/etc/mysql/conf.d        # 配置文件目录（可选）
      - ./logs:/var/log/mysql           # 日志目录（可选）
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
    stop_grace_period: 30s
```

---

## ⚠️ 注意事项

1. **密码安全**
   - 生产环境必须修改默认密码
   - 使用强密码（至少16位，包含大小写字母、数字、特殊字符）

2. **数据备份**
   - 定期备份数据
   - 使用 `mysqldump` 或备份数据卷

3. **性能优化**
   - 根据服务器内存调整 MySQL 配置
   - 可以挂载自定义配置文件到 `/etc/mysql/conf.d`

4. **权限问题**
   - 如果使用目录映射，确保目录权限正确
   - MySQL 容器内用户是 `mysql`（UID 999）

---

## 📚 参考链接

- [MySQL 官方文档](https://dev.mysql.com/doc/)
- [MySQL Docker Hub](https://hub.docker.com/_/mysql)
- [Docker Compose 文档](https://docs.docker.com/compose/)

---

**最后更新**: 2025-12-10

