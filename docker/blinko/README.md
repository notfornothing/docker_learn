# Blinko 服务

Blinko 是一个开源的应用服务。

## 快速开始

### 启动服务

```bash
docker-compose up -d
```

### 停止服务

```bash
docker-compose down
```

### 查看日志

```bash
docker-compose logs -f
```

### 查看 Blinko 日志

```bash
docker-compose logs -f blinko
```

### 查看 PostgreSQL 日志

```bash
docker-compose logs -f postgres
```

## 访问地址

启动后，在浏览器中访问：

```
http://localhost:1111
```

## 配置说明

### 服务组成

- **Blinko**: 主应用服务
  - 端口: 1111（默认端口）
  - 容器名: `blinko-website`
  
- **PostgreSQL**: 数据库服务
  - 容器名: `blinko-postgres`
  - 数据库: postgres
  - 用户名: postgres
  - 密码: mysecretpassword（建议修改）

### 环境变量

- **NODE_ENV**: production
- **NEXTAUTH_SECRET**: my_ultra_secure_nextauth_secret（建议修改为随机字符串）
- **DATABASE_URL**: 自动配置，指向 PostgreSQL 服务
- **TZ**: Asia/Shanghai

### 数据持久化

- **Blinko 数据**: `~/.blinko` 目录（即 `${HOME}/.blinko`）
- **PostgreSQL 数据**: `~/.blinko_db` 目录（即 `${HOME}/.blinko_db`）

即使删除容器，数据也不会丢失。

数据目录位置：
- macOS/Linux: `~/.blinko` 或 `/Users/你的用户名/.blinko`
- PostgreSQL: `~/.blinko_db` 或 `/Users/你的用户名/.blinko_db`

## 安全建议

⚠️ **重要**: 在生产环境使用前，请修改以下配置：

1. **PostgreSQL 密码**: 修改 `POSTGRES_PASSWORD` 环境变量
2. **NEXTAUTH_SECRET**: 修改为随机生成的密钥（可以使用 `openssl rand -base64 32` 生成）

修改后需要同步更新 `DATABASE_URL` 中的密码。

## 首次使用

1. 访问 `http://localhost:1111`
2. 按照提示完成初始设置

## 网络配置

服务使用自定义网络 `blinko-network`，Blinko 和 PostgreSQL 通过此网络通信。

## 更多信息

- [Blinko GitHub 仓库](https://github.com/blinko-space/blinko)
- [安装脚本参考](https://raw.githubusercontent.com/blinko-space/blinko/main/install.sh)

