# Docker Compose 命名卷（Volumes）复用指南

## 📋 目录

- [命名卷的命名规则](#命名卷的命名规则)
- [重复使用同名卷会怎样？](#重复使用同名卷会怎样)
- [如何在不同 compose 文件中复用同一个卷](#如何在不同-compose-文件中复用同一个卷)
- [如何指定使用已存在的卷](#如何指定使用已存在的卷)
- [查看和管理命名卷](#查看和管理命名卷)
- [实际示例](#实际示例)

---

## 🎯 命名卷的命名规则

### Docker Compose 自动命名规则

当你这样写：

```yaml
volumes:
  - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

**Docker Compose 会自动生成卷名：** `项目名_卷名`

**项目名规则：**
- 如果 `docker-compose.yml` 在 `docker/mysql/` 目录下，项目名通常是 `mysql`
- 如果 `docker-compose.yml` 在 `docker/neo4j/` 目录下，项目名通常是 `neo4j`
- 或者使用 `-p` 参数指定项目名：`docker compose -p myproject up`

**示例：**
- 文件位置：`docker/mysql/docker-compose.yml`
- 卷名：`mysql_data`
- **实际卷名：** `mysql_mysql_data` 或 `docker_mysql_data`（取决于目录结构）

---

## ✅ 重复使用同名卷会怎样？

### 情况 1：同一个 compose 文件中重复使用

```yaml
services:
  app1:
    volumes:
      - mysql_data:/var/lib/mysql
  
  app2:
    volumes:
      - mysql_data:/var/lib/mysql  # 使用同一个卷

volumes:
  mysql_data:  # 只定义一次
```

**结果：** ✅ **会使用同一个卷**，两个服务共享数据

---

### 情况 2：不同的 compose 文件中使用同名卷

**文件 1：** `docker/mysql/docker-compose.yml`
```yaml
volumes:
  - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

**文件 2：** `docker/backup/docker-compose.yml`
```yaml
volumes:
  - mysql_data:/var/lib/mysql  # 同名

volumes:
  mysql_data:
```

**结果：** ❌ **不会使用同一个卷**，会创建两个不同的卷！

**原因：** Docker Compose 会根据项目名生成不同的卷名：
- `mysql_mysql_data`（来自 mysql 项目）
- `backup_mysql_data`（来自 backup 项目）

---

## 🔗 如何在不同 compose 文件中复用同一个卷

### 方法一：使用外部卷（external: true）（推荐）

**文件 1：** `docker/mysql/docker-compose.yml`（创建卷）
```yaml
services:
  mysql:
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
    # 不写 external，正常创建
```

**文件 2：** `docker/backup/docker-compose.yml`（复用卷）
```yaml
services:
  backup:
    volumes:
      - mysql_data:/var/lib/mysql  # 使用已存在的卷

volumes:
  mysql_data:
    external: true  # 关键：声明这是外部已存在的卷
    name: mysql_mysql_data  # 指定实际的卷名（可选，不写就用项目名_卷名）
```

**说明：**
- `external: true` - 告诉 Docker Compose 这个卷已经存在，不要创建新的
- `name:` - 指定实际的卷名（如果卷名和这里写的不一样）

---

### 方法二：使用完整卷名

**文件 1：** `docker/mysql/docker-compose.yml`
```yaml
volumes:
  mysql_data:
    name: shared_mysql_data  # 指定固定名称
```

**文件 2：** `docker/backup/docker-compose.yml`
```yaml
volumes:
  mysql_data:
    external: true
    name: shared_mysql_data  # 使用相同的固定名称
```

---

### 方法三：使用相同的项目名

```bash
# 两个 compose 文件都使用相同的项目名
cd docker/mysql
docker compose -p myproject up -d

cd ../backup
docker compose -p myproject up -d  # 使用相同的项目名
```

这样两个 compose 文件会使用相同的卷名前缀。

---

## 🔍 如何指定使用已存在的卷

### 步骤 1：查看已存在的卷

```bash
# 列出所有卷
docker volume ls

# 查看卷的详细信息
docker volume inspect mysql_mysql_data
```

**输出示例：**
```
[
    {
        "CreatedAt": "2025-12-10T10:00:00Z",
        "Driver": "local",
        "Labels": {},
        "Mountpoint": "/var/lib/docker/volumes/mysql_mysql_data/_data",
        "Name": "mysql_mysql_data",
        "Options": {},
        "Scope": "local"
    }
]
```

---

### 步骤 2：在 compose 文件中指定使用

```yaml
volumes:
  mysql_data:
    external: true
    name: mysql_mysql_data  # 使用上面查到的实际卷名
```

---

## 📊 查看和管理命名卷

### 查看所有卷

```bash
# 列出所有卷
docker volume ls

# 查看特定卷的详细信息
docker volume inspect 卷名

# 查看卷的使用情况
docker volume inspect 卷名 | grep -A 5 Mountpoint
```

---

### 删除卷

```bash
# 删除单个卷（⚠️ 数据会丢失！）
docker volume rm 卷名

# 删除未使用的卷
docker volume prune

# 删除所有卷（⚠️ 危险！）
docker volume rm $(docker volume ls -q)
```

---

### 备份和恢复卷

```bash
# 备份卷
docker run --rm \
  -v mysql_mysql_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/mysql-backup.tar.gz -C /data .

# 恢复卷
docker run --rm \
  -v mysql_mysql_data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/mysql-backup.tar.gz -C /data
```

---

## 💡 实际示例

### 示例 1：MySQL 和备份服务共享数据卷

**文件 1：** `docker/mysql/docker-compose.yml`
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

**文件 2：** `docker/backup/docker-compose.yml`
```yaml
version: '3.8'

services:
  backup:
    image: mysql:8.0
    command: mysqldump -u root -p123456 --all-databases > /backup/dump.sql
    volumes:
      - mysql_data:/var/lib/mysql  # 使用 MySQL 的数据卷
      - ./backup:/backup

volumes:
  mysql_data:
    external: true
    name: mysql_mysql_data  # 使用 MySQL 创建的卷
```

---

### 示例 2：多个服务共享配置卷

**文件 1：** `docker/nginx/docker-compose.yml`
```yaml
version: '3.8'

services:
  nginx:
    image: nginx:latest
    volumes:
      - shared_config:/etc/nginx/conf.d

volumes:
  shared_config:
    name: shared_nginx_config  # 固定名称
```

**文件 2：** `docker/nginx-reload/docker-compose.yml`
```yaml
version: '3.8'

services:
  reloader:
    image: nginx:latest
    volumes:
      - shared_config:/etc/nginx/conf.d  # 共享配置

volumes:
  shared_config:
    external: true
    name: shared_nginx_config  # 使用相同的固定名称
```

---

### 示例 3：查看当前项目的卷名

```bash
# 进入项目目录
cd docker/mysql

# 启动服务
docker compose up -d

# 查看创建的卷（会显示实际卷名）
docker compose config --volumes

# 或者查看容器使用的卷
docker compose ps
docker inspect mysql | grep -A 10 Mounts
```

---

## 📋 总结

### 关键点

1. **同名卷在不同 compose 文件中不会自动共享**
   - 每个 compose 文件会创建自己的卷（项目名_卷名）

2. **要复用卷，必须使用 `external: true`**
   ```yaml
   volumes:
     卷名:
       external: true
       name: 实际卷名
   ```

3. **查看实际卷名**
   ```bash
   docker volume ls
   docker volume inspect 卷名
   ```

4. **推荐做法**
   - 需要共享：使用 `external: true` + `name:` 指定固定名称
   - 不需要共享：让 Docker Compose 自动命名

---

## ❓ 常见问题

### Q: 如何知道我的卷的实际名称？

**A:** 
```bash
# 方法1: 查看所有卷
docker volume ls

# 方法2: 查看容器使用的卷
docker inspect 容器名 | grep -A 10 Mounts

# 方法3: 查看 compose 配置
docker compose config
```

---

### Q: 删除了 compose 文件，卷还在吗？

**A:** ✅ **还在！** 卷是独立于容器的，删除容器或 compose 文件不会删除卷。

要删除卷需要：
```bash
docker compose down -v  # 删除容器和卷
# 或
docker volume rm 卷名    # 手动删除卷
```

---

### Q: 如何重命名卷？

**A:** Docker 不支持直接重命名卷，需要：
1. 创建新卷
2. 复制数据
3. 删除旧卷

```bash
# 创建新卷并复制数据
docker run --rm \
  -v 旧卷名:/source \
  -v 新卷名:/target \
  alpine sh -c "cp -a /source/. /target/"

# 删除旧卷
docker volume rm 旧卷名
```

---

### Q: 如何让卷名更清晰？

**A:** 使用 `name:` 指定固定名称：

```yaml
volumes:
  mysql_data:
    name: myproject_mysql_data  # 固定名称，不依赖项目名
```

---

**最后更新**: 2025-12-10
