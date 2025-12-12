# Docker & Docker Compose 完整指南

> 从基础结构到高级命令，一站式 Docker 学习指南

## 📋 目录

- [第一部分：Docker Compose 基础结构](#第一部分docker-compose-基础结构)
- [第二部分：Docker Compose 命令](#第二部分docker-compose-命令)
- [第三部分：Docker 基础命令](#第三部分docker-基础命令)
- [第四部分：容器管理命令](#第四部分容器管理命令)
- [第五部分：Volumes 数据卷管理](#第五部分volumes-数据卷管理)
- [第六部分：高级主题](#第六部分高级主题)

---

## 第一部分：Docker Compose 基础结构

### 最简单的 compose 文件

一个最简单的 `docker-compose.yml` 只需要 3 个参数就能启动：

```yaml
version: '3.8'

services:
  web:
    image: nginx:latest
    ports:
      - "8080:80"
```

**运行：** `docker compose up -d`  
**访问：** http://localhost:8080

---

### 必须的参数

#### 1. `version` - 版本号

```yaml
version: '3.8'  # 必须写，推荐用 3.8
```

**作用：** 告诉 Docker Compose 使用哪个版本的配置格式  
**可选值：** `'3.6'`, `'3.7'`, `'3.8'`（推荐用 3.8）

#### 2. `services` - 服务定义

```yaml
services:
  服务名:
    image: 镜像名
```

**作用：** 定义要启动的容器服务  
**说明：** `services:` 是固定关键字，`服务名` 随便起

#### 3. `image` - 镜像名称

```yaml
image: nginx:latest
```

**格式：** `镜像名:标签`
- `nginx:latest` - 最新版本
- `nginx:1.25` - 指定版本
- `mysql:8.0` - MySQL 8.0

---

### 常用参数

#### `ports` - 端口映射

```yaml
ports:
  - "8080:80"        # 宿主机8080端口 -> 容器80端口
  - "3306:3306"      # 宿主机3306端口 -> 容器3306端口
```

**格式：** `"宿主机端口:容器端口"`

#### `volumes` - 数据卷（持久化）

```yaml
volumes:
  # 目录映射（常用）
  - ./data:/data              # 当前目录的data文件夹 -> 容器的/data
  
  # 命名卷（推荐）
  - my_volume:/var/lib/mysql  # 命名卷 -> 容器的/var/lib/mysql

volumes:  # 在文件最下面定义命名卷
  my_volume:
```

**作用：** 把数据保存到宿主机，容器删了数据还在

#### `environment` - 环境变量

```yaml
environment:
  - MYSQL_ROOT_PASSWORD=123456
  - TZ=Asia/Shanghai
```

**格式：** `变量名=值`

#### `container_name` - 容器名称

```yaml
container_name: my-nginx
```

**作用：** 给容器起个名字，方便管理  
**不写的话：** Docker 会自动生成名字（通常是 `目录名_服务名_1`）

#### `restart` - 重启策略

```yaml
restart: unless-stopped
```

**可选值：**

| 策略 | 容器崩溃 | 手动停止 | 服务器重启 | 适用场景 |
|------|---------|---------|-----------|---------|
| `always` | ✅ 重启 | ✅ 重启 | ✅ 自动启动 | 需要一直运行的服务 |
| `unless-stopped` | ✅ 重启 | ❌ 不重启 | ✅ 自动启动 | **推荐，最常用** |
| `on-failure` | ✅ 重启 | ❌ 不重启 | ❌ 不启动 | 测试环境 |
| `no` | ❌ 不重启 | ❌ 不重启 | ❌ 不启动 | 临时服务 |

#### `depends_on` - 服务依赖（启动顺序）

```yaml
services:
  web:
    image: nginx:latest
    depends_on:
      - db
      - redis
  
  db:
    image: mysql:8.0
  
  redis:
    image: redis:latest
```

**作用：** 控制服务启动顺序，`web` 会等 `db` 和 `redis` 启动后再启动

**等待服务就绪：**

```yaml
depends_on:
  mysql:
    condition: service_healthy  # 等 mysql 健康后才启动
  redis:
    condition: service_started   # redis 启动后就启动
```

#### `healthcheck` - 健康检查

```yaml
healthcheck:
  test: ["CMD", "wget", "--spider", "-q", "-T", "5", "http://127.0.0.1/"]
  interval: 30s      # 每30秒检查一次
  timeout: 10s       # 10秒没响应就超时
  retries: 3         # 失败3次才认为不健康
  start_period: 40s  # 启动后40秒内不检查
```

**常用检查方式：**

```yaml
# HTTP 检查（Web 服务）
test: ["CMD", "wget", "--spider", "-q", "-T", "5", "http://localhost:80"]

# TCP 检查（数据库等）
test: ["CMD", "nc", "-z", "localhost", "3306"]

# 命令检查（自定义）
test: ["CMD", "pg_isready", "-U", "postgres"]
```

#### `extra_hosts` - 主机名映射（Linux 访问宿主机）

```yaml
extra_hosts:
  - "host.docker.internal:host-gateway"
```

**作用：** 让容器能访问宿主机服务（Linux 需要，Mac/Windows 自动支持）

**使用场景：** 容器内需要访问宿主机上的服务

```nginx
# nginx 配置中
proxy_pass http://host.docker.internal:9000/;  # ✅ 正确！指向宿主机
proxy_pass http://127.0.0.1:9000/;              # ❌ 错误！指向容器自己
```

---

### 完整配置模板

```yaml
version: '3.8'

services:
  服务名:
    image: 镜像名:标签
    container_name: 容器名
    restart: unless-stopped  # 开机自启动 + 崩溃重启
    ports:
      - "8080:80"
    environment:
      - 变量名=值
    volumes:
      - 数据卷名:/容器内路径
      - ./目录:/容器内路径
    depends_on:
      - 依赖的服务名
    healthcheck:
      test: ["CMD", "检查命令"]
      interval: 30s
      timeout: 10s
      retries: 3
    extra_hosts:  # Linux 需要
      - "host.docker.internal:host-gateway"

volumes:
  数据卷名:
```

---

## 第二部分：Docker Compose 命令

### 启动和停止

```bash
# 启动服务（后台运行）
docker compose up -d

# 启动服务（前台运行，看日志）
docker compose up

# 停止服务（保留 volumes）
docker compose down

# 停止并删除数据卷（⚠️ 数据会丢失！）
docker compose down -v

# 重启服务
docker compose restart

# 重启某个服务
docker compose restart 服务名
```

### 查看状态

```bash
# 查看服务状态
docker compose ps

# 查看日志
docker compose logs

# 实时查看日志
docker compose logs -f

# 查看某个服务的日志
docker compose logs -f 服务名

# 查看最后 N 行日志
docker compose logs --tail=100 服务名
```

### 其他操作

```bash
# 进入容器
docker compose exec 服务名 sh
# 或
docker compose exec 服务名 bash

# 更新镜像并重启
docker compose pull
docker compose up -d

# 强制重新创建容器（保留 volumes）
docker compose up -d --force-recreate

# 查看配置
docker compose config

# 查看 volumes
docker compose config --volumes
```

---

## 第三部分：Docker 基础命令

### 镜像操作

```bash
# 拉取镜像
docker pull nginx:latest

# 拉取指定架构的镜像
docker pull --platform linux/amd64 nginx:latest

# 列出本地镜像
docker images
# 或
docker image ls

# 删除镜像
docker rmi 镜像名:标签
# 或
docker image rm 镜像名:标签

# 查看镜像详情（本地）
docker image inspect 镜像名:标签

# 查看镜像架构
docker image inspect 镜像名:标签 --format '{{.Architecture}}'
```

### 容器操作

```bash
# 列出运行中的容器
docker ps

# 列出所有容器（包括停止的）
docker ps -a

# 启动容器
docker start 容器名

# 停止容器
docker stop 容器名

# 重启容器
docker restart 容器名

# 删除容器
docker rm 容器名

# 强制删除运行中的容器
docker rm -f 容器名

# 查看容器日志
docker logs 容器名

# 实时查看日志
docker logs -f 容器名

# 进入容器
docker exec -it 容器名 sh
# 或
docker exec -it 容器名 bash

# 查看容器详情
docker inspect 容器名
```

### 查询远程镜像信息

```bash
# 查看远程镜像的 Manifest（多架构信息）
docker manifest inspect 镜像名:标签

# 查看远程镜像支持的架构
docker manifest inspect 镜像名:标签 | grep -A 5 "platform"

# 使用 buildx 查看（更详细）
docker buildx imagetools inspect 镜像名:标签
```

**区别：**
- `docker image inspect` - 查询**本地**镜像（必须已拉取）
- `docker manifest inspect` - 查询**远程**镜像（不需要本地有）

---

## 第四部分：容器管理命令

### `docker update` - 动态修改容器配置

**作用：** 无需删除容器，动态修改运行中容器的配置

#### 修改重启策略

```bash
# 修改为 always（总是自启动）
docker update --restart=always 容器名

# 修改为 unless-stopped（推荐）
docker update --restart=unless-stopped 容器名

# 取消自启动
docker update --restart=no 容器名
```

#### 修改资源限制

```bash
# CPU 限制
docker update --cpus=2.5 容器名        # 限制使用 2.5 个 CPU 核心
docker update --cpus=1 容器名          # 限制使用 1 个 CPU 核心

# 内存限制
docker update --memory=512m 容器名     # 限制内存为 512MB
docker update --memory=2g 容器名       # 限制内存为 2GB

# CPU 权重（优先级）
docker update --cpu-shares=1024 容器名 # 提高 CPU 优先级（默认 1024）

# 进程数限制
docker update --pids-limit=100 容器名  # 限制最大进程数

# CPU 绑定
docker update --cpuset-cpus=0,1 容器名 # 只使用 CPU 0 和 1
```

#### 组合修改

```bash
docker update \
  --memory=1g \
  --cpus=2 \
  --restart=always \
  --cpu-shares=512 \
  容器名
```

**注意：** `docker update` **不能修改**端口映射、环境变量、卷挂载、网络配置，这些需要删除容器后重新创建。

---

### 查看容器重启策略

```bash
# 查看单个容器的重启策略
docker inspect 容器名 --format '{{.HostConfig.RestartPolicy.Name}}'

# 查看所有容器的重启策略
docker ps -a --format "{{.Names}}" | while read name; do
    restart=$(docker inspect "$name" --format '{{.HostConfig.RestartPolicy.Name}}' 2>/dev/null)
    printf "%-25s %s\n" "$name" "$restart"
done

# 只看自启动的容器（always）
docker ps -a --format "{{.Names}}" | while read name; do
    restart=$(docker inspect "$name" --format '{{.HostConfig.RestartPolicy.Name}}' 2>/dev/null)
    [ "$restart" = "always" ] && echo "$name"
done
```

---

## 第五部分：Volumes 数据卷管理

### Volumes 基础概念

#### 命名卷 vs 绑定挂载

```yaml
# 命名卷（推荐）
volumes:
  - my_data:/var/lib/mysql

volumes:
  my_data:

# 绑定挂载（目录映射）
volumes:
  - ./data:/var/lib/mysql
```

**区别：**

| 类型 | 特点 | 适用场景 |
|------|------|---------|
| **命名卷** | Docker 自动管理位置，跨平台兼容 | 数据库数据、应用数据 |
| **绑定挂载** | 数据位置直观，方便备份 | 配置文件、日志文件 |

### Volumes 命令

```bash
# 列出所有卷
docker volume ls

# 查看卷详情
docker volume inspect 卷名

# 删除卷（⚠️ 数据会丢失！）
docker volume rm 卷名

# 删除未使用的卷
docker volume prune

# 查看容器使用的卷
docker inspect 容器名 --format '{{range .Mounts}}{{.Type}} {{.Source}} -> {{.Destination}}{{"\n"}}{{end}}'
```

### Volumes 命名规则

**Docker Compose 自动命名：** `项目名_卷名`

```yaml
# docker/mysql/docker-compose.yml
volumes:
  - data:/var/lib/mysql

volumes:
  data:
```

**实际卷名：** `mysql_data`（项目名 `mysql` + 卷名 `data`）

**不同项目的同名卷不会冲突：**
- `mysql_data` - MySQL 项目的卷
- `redis_data` - Redis 项目的卷

### 复用已存在的卷

```yaml
volumes:
  mysql_data:
    external: true              # 声明这是外部已存在的卷
    name: mysql_mysql_data      # 指定实际的卷名
```

### Volumes 生命周期

**重要：命名卷是独立的，不绑定到容器！**

| 操作 | 容器状态 | 卷状态 | 数据状态 |
|------|---------|--------|---------|
| `docker rm 容器名` | ❌ 删除 | ✅ 保留 | ✅ 保留 |
| `docker compose down` | ❌ 删除 | ✅ 保留 | ✅ 保留 |
| `docker compose down -v` | ❌ 删除 | ❌ 删除 | ❌ 丢失 |
| `docker volume rm 卷名` | ✅ 保留 | ❌ 删除 | ❌ 丢失 |

**记忆口诀：**
> **卷是独立的，容器只是租客**  
> **删除租客（容器），房子（卷）还在**  
> **只有拆房子（删除卷），数据才丢失**

---

## 第六部分：高级主题

### 多架构镜像

#### 查看远程镜像支持的架构

```bash
# 查看 Manifest List（所有架构）
docker manifest inspect 镜像名:标签

# 查看支持的架构列表
docker manifest inspect 镜像名:标签 | grep -A 5 "platform"
```

#### 拉取指定架构的镜像

```bash
# 拉取 amd64 架构
docker pull --platform linux/amd64 镜像名:标签

# 拉取 arm64 架构
docker pull --platform linux/arm64 镜像名:标签

# 在 docker-compose.yml 中指定
services:
  app:
    image: 镜像名:标签
    platform: linux/amd64
```

#### 使用 Digest 拉取镜像

```bash
# 使用 Manifest Digest（指定架构）
docker pull 镜像名@sha256:manifest_digest...

# 使用 Index Digest（自动选择架构）
docker pull 镜像名@sha256:index_digest...

# 在 docker-compose.yml 中使用
services:
  app:
    image: 镜像名@sha256:digest...
```

**区别：**
- **Index Digest** - 标识整个多架构镜像（所有架构的集合）
- **Manifest Digest** - 标识单个架构镜像（特定架构的内容）

---

### 容器访问宿主机服务

#### Mac/Windows（自动支持）

```yaml
services:
  nginx:
    # Mac/Windows 自动支持 host.docker.internal
    # 不需要额外配置
```

#### Linux（需要手动配置）

```yaml
services:
  nginx:
    extra_hosts:
      - "host.docker.internal:host-gateway"
```

**使用：**

```nginx
# nginx 配置中
proxy_pass http://host.docker.internal:9000/;  # ✅ 正确！指向宿主机
proxy_pass http://127.0.0.1:9000/;              # ❌ 错误！指向容器自己
```

**验证：**

```bash
# 查看容器内的 hosts 文件
docker exec 容器名 cat /etc/hosts

# 测试是否能解析
docker exec 容器名 ping host.docker.internal

# 测试是否能访问宿主机服务
docker exec 容器名 curl http://host.docker.internal:9000/
```

---

### 开机自启动

#### Docker Compose 配置

```yaml
services:
  nginx:
    restart: unless-stopped  # 服务器重启后自动启动
```

#### 查看自启动的容器

```bash
# 查看所有容器的重启策略
docker ps -a --format "{{.Names}}" | while read name; do
    restart=$(docker inspect "$name" --format '{{.HostConfig.RestartPolicy.Name}}' 2>/dev/null)
    printf "%-25s %s\n" "$name" "$restart"
done

# 只看 always 自启动的容器
docker ps -a --format "{{.Names}}" | while read name; do
    restart=$(docker inspect "$name" --format '{{.HostConfig.RestartPolicy.Name}}' 2>/dev/null)
    [ "$restart" = "always" ] && echo "$name"
done
```

#### 修改重启策略（无需删除容器）

```bash
# 修改为 always
docker update --restart=always 容器名

# 修改为 unless-stopped
docker update --restart=unless-stopped 容器名

# 取消自启动
docker update --restart=no 容器名
```

---

### 本地 vs 远程命令对比

| 命令类型 | 操作对象 | 是否需要本地镜像 | 是否访问远程 |
|---------|---------|----------------|-------------|
| `docker image *` | 本地镜像 | ✅ 必须 | ❌ 不访问 |
| `docker manifest *` | 远程仓库 | ❌ 不需要 | ✅ 访问 |
| `docker pull` | 远程→本地 | ❌ 不需要 | ✅ 访问 |
| `docker push` | 本地→远程 | ✅ 必须 | ✅ 访问 |
| `docker images` | 本地镜像列表 | - | ❌ 不访问 |
| `docker search` | 远程仓库搜索 | ❌ 不需要 | ✅ 访问 |

**记忆规律：**
- `image` 开头 → 操作本地镜像
- `manifest` 开头 → 操作远程 Manifest
- `pull/push` → 远程操作
- 不带前缀的通用命令 → 通常操作本地

---

## 📚 快速参考

### 最小配置模板

```yaml
version: '3.8'

services:
  服务名:
    image: 镜像名:标签
    ports:
      - "宿主机端口:容器端口"
```

### 完整配置模板

```yaml
version: '3.8'

services:
  服务名:
    image: 镜像名:标签
    container_name: 容器名
    restart: unless-stopped
    ports:
      - "8080:80"
    environment:
      - 变量名=值
    volumes:
      - 数据卷名:/容器内路径
      - ./目录:/容器内路径
    depends_on:
      - 依赖的服务名
    healthcheck:
      test: ["CMD", "检查命令"]
      interval: 30s
      timeout: 10s
      retries: 3
    extra_hosts:  # Linux 需要
      - "host.docker.internal:host-gateway"

volumes:
  数据卷名:
```

### 常用命令速查

```bash
# Compose 命令
docker compose up -d              # 启动
docker compose down               # 停止（保留 volumes）
docker compose ps                 # 查看状态
docker compose logs -f            # 查看日志
docker compose restart            # 重启

# Docker 命令
docker ps -a                      # 查看所有容器
docker logs -f 容器名              # 查看日志
docker exec -it 容器名 sh         # 进入容器
docker inspect 容器名              # 查看详情

# Volumes 命令
docker volume ls                  # 列出卷
docker volume inspect 卷名        # 查看卷详情
docker volume rm 卷名             # 删除卷

# Update 命令
docker update --restart=always 容器名    # 修改重启策略
docker update --memory=1g 容器名        # 修改内存限制
docker update --cpus=2 容器名            # 修改 CPU 限制
```

---

## ❓ 常见问题

### Q: 端口被占用怎么办？

**A:** 换个端口号，或者停止占用端口的服务

```bash
# 查看端口占用
lsof -i :80

# 修改 compose 文件中的端口
ports:
  - "8081:80"  # 改成8081
```

### Q: 数据会丢失吗？

**A:** 
- ✅ 使用命名卷：删除容器**不会**丢失数据
- ✅ 使用绑定挂载：删除容器**不会**丢失数据
- ❌ 使用 `docker compose down -v`：**会**丢失数据
- ❌ 数据在容器内（没有 volume）：**会**丢失

### Q: 如何修改容器的配置？

**A:**
- **可以动态修改**：重启策略、资源限制 → 使用 `docker update`
- **需要重新创建**：端口映射、环境变量、卷挂载 → 修改 compose 文件后 `docker compose up -d`

### Q: 如何查看容器是否自启动？

**A:**
```bash
docker inspect 容器名 --format '{{.HostConfig.RestartPolicy.Name}}'
```

### Q: 容器如何访问宿主机服务？

**A:**
- Mac/Windows：直接使用 `host.docker.internal`
- Linux：需要在 compose 文件中添加 `extra_hosts`

---

## 📝 总结

### 核心要点

1. **Docker Compose 最基础**：`version` + `services` + `image` + `ports`
2. **数据持久化**：使用 `volumes`（命名卷或绑定挂载）
3. **开机自启动**：使用 `restart: unless-stopped` 或 `restart: always`
4. **动态修改配置**：使用 `docker update`（无需删除容器）
5. **Volumes 是独立的**：删除容器不会删除卷，数据会保留

### 推荐配置

```yaml
restart: unless-stopped  # 推荐的重启策略
volumes:                 # 使用命名卷持久化数据
healthcheck:            # 配置健康检查
depends_on:             # 控制启动顺序
```

---

**最后更新**: 2025-12-12
