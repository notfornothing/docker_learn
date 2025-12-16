# Redis Docker Compose 部署指南

## 快速开始

### 1. 启动 Redis

```bash
cd /Users/mac/Project_IDEA/docker_learn/docker/redis
docker compose up -d
```

### 2. 验证 Redis 是否运行

```bash
# 查看容器状态
docker compose ps

# 测试连接
docker compose exec redis redis-cli ping
# 应该返回：PONG
```

### 3. 连接 Redis

```bash
# 方式1：使用 redis-cli（容器内）
docker compose exec redis redis-cli

# 方式2：使用本地 redis-cli（如果已安装）
redis-cli -h localhost -p 6379

# 方式3：使用 Docker 命令
docker exec -it redis redis-cli
```

## 配置说明

### 端口映射

- **宿主机端口：** 6379
- **容器端口：** 6379
- **访问地址：** `localhost:6379`

### 数据持久化

- **数据卷：** `redis_data`
- **数据目录：** `/data`（容器内）
- **持久化方式：** AOF（Append Only File）

### 配置文件

- **配置文件路径：** `./redis.conf`
- **容器内路径：** `/usr/local/etc/redis/redis.conf`

## 常用操作

### 查看日志

```bash
docker compose logs -f redis
```

### 重启 Redis

```bash
docker compose restart redis
```

### 停止 Redis

```bash
docker compose down
```

### 停止并删除数据（⚠️ 数据会丢失）

```bash
docker compose down -v
```

## Redis 命令示例

### 基本操作

```bash
# 连接 Redis
docker compose exec redis redis-cli

# 设置键值
SET mykey "Hello Redis"

# 获取值
GET mykey

# 查看所有键
KEYS *

# 删除键
DEL mykey

# 查看信息
INFO

# 退出
exit
```

### 数据类型操作

```bash
# String（字符串）
SET name "Redis"
GET name

# List（列表）
LPUSH mylist "item1"
LPUSH mylist "item2"
LRANGE mylist 0 -1

# Set（集合）
SADD myset "member1"
SADD myset "member2"
SMEMBERS myset

# Hash（哈希）
HSET myhash field1 "value1"
HGET myhash field1
HGETALL myhash

# Sorted Set（有序集合）
ZADD myzset 1 "member1"
ZADD myzset 2 "member2"
ZRANGE myzset 0 -1
```

## 配置选项

### 使用自定义配置文件

编辑 `redis.conf` 文件，然后重启：

```bash
docker compose restart redis
```

### 使用默认配置（不使用配置文件）

修改 `docker-compose.yml`：

```yaml
services:
  redis:
    # 注释掉 volumes 中的配置文件映射
    # volumes:
    #   - ./redis.conf:/usr/local/etc/redis/redis.conf:ro
    
    # 使用默认命令
    command: redis-server --appendonly yes
```

## 数据备份和恢复

### 备份数据

```bash
# 方式1：备份数据卷
docker run --rm \
  -v redis_redis_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/redis-backup.tar.gz -C /data .

# 方式2：使用 redis-cli 导出
docker compose exec redis redis-cli --rdb /data/dump.rdb
```

### 恢复数据

```bash
# 恢复数据卷
docker run --rm \
  -v redis_redis_data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/redis-backup.tar.gz -C /data
```

## 性能调优

### 内存限制

在 `redis.conf` 中设置：

```
maxmemory 512mb
maxmemory-policy allkeys-lru
```

### 持久化策略

- **RDB（快照）：** 适合备份
- **AOF（追加文件）：** 适合数据安全

在 `redis.conf` 中配置：

```
# RDB 配置
save 900 1
save 300 10
save 60 10000

# AOF 配置
appendonly yes
appendfsync everysec
```

## 故障排查

### 查看 Redis 日志

```bash
docker compose logs redis
```

### 检查 Redis 状态

```bash
docker compose exec redis redis-cli INFO
```

### 测试连接

```bash
docker compose exec redis redis-cli ping
```

### 查看内存使用

```bash
docker compose exec redis redis-cli INFO memory
```

## 安全建议

### 设置密码

在 `redis.conf` 中添加：

```
requirepass your_password_here
```

连接时使用：

```bash
redis-cli -a your_password_here
```

### 限制访问

在 `docker-compose.yml` 中只绑定本地：

```yaml
ports:
  - "127.0.0.1:6379:6379"  # 只允许本地访问
```

## 相关链接

- [Redis 官方文档](https://redis.io/documentation)
- [Redis 配置文件示例](https://redis.io/topics/config)
- [Docker Hub Redis 镜像](https://hub.docker.com/_/redis)

---

**最后更新**: 2025-12-12


