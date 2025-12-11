# Docker Compose 最简单教程

> 只讲最基础的参数，能启动起来就行

## 📋 目录

- [最简单的 compose 文件](#最简单的-compose-文件)
- [必须的参数](#必须的参数)
- [常用参数](#常用参数)
- [最简单的示例](#最简单的示例)
- [常用命令](#常用命令)

---

## 🎯 最简单的 compose 文件

一个最简单的 `docker-compose.yml` 只需要 3 个参数就能启动：

```yaml
version: '3.8'

services:
  web:
    image: nginx:latest
    ports:
      - "8080:80"
```

**解释：**
- `version`: compose 文件格式版本（必须写）
- `services`: 定义服务（必须写）
- `web`: 服务名称（随便起名）
- `image`: 使用哪个镜像（必须写）
- `ports`: 端口映射（必须写，格式：`"宿主机端口:容器端口"`）

运行：`docker compose up -d`

访问：`http://localhost:8080` 就能看到 nginx 页面了！

---

## ✅ 必须的参数

### 1. `version` - 版本号

```yaml
version: '3.8'  # 必须写，推荐用 3.8
```

**作用：** 告诉 Docker Compose 使用哪个版本的配置格式

**可选值：** `'3.6'`, `'3.7'`, `'3.8'`（推荐用 3.8）

---

### 2. `services` - 服务定义

```yaml
services:
  服务名:
    image: 镜像名
```

**作用：** 定义要启动的容器服务

**说明：** 
- `services:` 是固定关键字
- `服务名` 随便起，比如 `web`、`db`、`redis` 等

---

### 3. `image` - 镜像名称

```yaml
image: nginx:latest
```

**作用：** 指定使用哪个 Docker 镜像

**格式：** `镜像名:标签`
- `nginx:latest` - 最新版本
- `nginx:1.25` - 指定版本
- `mysql:8.0` - MySQL 8.0

**必须写，否则不知道启动什么！**

---

## 🔧 常用参数

### `ports` - 端口映射

```yaml
ports:
  - "8080:80"        # 宿主机8080端口 -> 容器80端口
  - "3306:3306"      # 宿主机3306端口 -> 容器3306端口
```

**作用：** 把容器内的端口映射到宿主机，这样就能从外面访问了

**格式：** `"宿主机端口:容器端口"`

**示例：**
- `"8080:80"` - 访问 `localhost:8080` 就是访问容器的 80 端口
- `"7474:7474"` - 访问 `localhost:7474` 就是访问容器的 7474 端口

---

### `volumes` - 数据卷（持久化）

```yaml
volumes:
  - ./data:/data              # 当前目录的data文件夹 -> 容器的/data
  - my_volume:/var/lib/mysql  # 命名卷 -> 容器的/var/lib/mysql
```

**作用：** 把数据保存到宿主机，容器删了数据还在

**两种写法：**

1. **目录映射**（常用）
   ```yaml
   volumes:
     - ./data:/data  # 当前目录下的data文件夹映射到容器的/data
   ```

2. **命名卷**（推荐）
   ```yaml
   volumes:
     - my_data:/data
   
   volumes:  # 在文件最下面定义
     my_data:
   ```

---

### `environment` - 环境变量

```yaml
environment:
  - MYSQL_ROOT_PASSWORD=123456
  - TZ=Asia/Shanghai
```

**作用：** 设置容器的环境变量，很多服务需要这个来配置

**格式：** `变量名=值`

**示例：**
- `MYSQL_ROOT_PASSWORD=123456` - MySQL 的 root 密码
- `NEO4J_AUTH=neo4j/password` - Neo4j 的用户名/密码
- `TZ=Asia/Shanghai` - 时区设置

---

### `container_name` - 容器名称

```yaml
container_name: my-nginx
```

**作用：** 给容器起个名字，方便管理

**不写的话：** Docker 会自动生成名字（通常是 `目录名_服务名_1`）

---

### `restart` - 重启策略

```yaml
restart: always
```

**作用：** 容器挂了自动重启，服务器重启后自动启动容器

**可选值：**
- `always` - **总是重启**（包括手动停止后，服务器重启后也会自动启动）
- `unless-stopped` - **除非手动停止，否则重启**（推荐，服务器重启后会自动启动，但手动停止后不会自动启动）
- `on-failure` - **只有失败才重启**（退出码非0时重启）
- `no` - **不自动重启**（默认值，容器挂了不重启，服务器重启后也不会自动启动）

**详细说明：**

| 策略 | 容器崩溃 | 手动停止 | 服务器重启 | 适用场景 |
|------|---------|---------|-----------|---------|
| `always` | ✅ 重启 | ✅ 重启 | ✅ 自动启动 | 需要一直运行的服务 |
| `unless-stopped` | ✅ 重启 | ❌ 不重启 | ✅ 自动启动 | **推荐，最常用** |
| `on-failure` | ✅ 重启 | ❌ 不重启 | ❌ 不启动 | 测试环境 |
| `no` | ❌ 不重启 | ❌ 不重启 | ❌ 不启动 | 临时服务 |

**示例：**
```yaml
services:
  web:
    image: nginx:latest
    restart: unless-stopped  # 推荐用这个
```

**开机自启动：**
- `always` 和 `unless-stopped` 都会在服务器重启后自动启动容器
- 只要 Docker 服务开机自启，这些容器就会自动启动

---

## 🔄 启动和重启相关参数（完整列表）

### 1. `restart` - 重启策略（已介绍）

见上面的详细说明。

---

### 2. `depends_on` - 服务依赖（启动顺序）

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

**注意：** 只控制启动顺序，不等待服务就绪（服务启动了但不一定可用）

**示例：**
```yaml
services:
  app:
    image: myapp:latest
    depends_on:
      - mysql      # 等 mysql 启动后再启动 app
      - redis      # 等 redis 启动后再启动 app
  
  mysql:
    image: mysql:8.0
  
  redis:
    image: redis:latest
```

---

### 3. `healthcheck` - 健康检查

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:80"]
  interval: 30s      # 每30秒检查一次
  timeout: 10s       # 10秒没响应就超时
  retries: 3         # 失败3次才认为不健康
  start_period: 40s  # 启动后40秒内不检查（给服务启动时间）
```

**作用：** 定期检查容器是否健康，不健康时会重启（如果配置了 restart）

**常用检查方式：**

1. **HTTP 检查**（Web 服务）
   ```yaml
   healthcheck:
     test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:80"]
     interval: 30s
     timeout: 10s
     retries: 3
   ```

2. **TCP 检查**（数据库等）
   ```yaml
   healthcheck:
     test: ["CMD", "nc", "-z", "localhost", "3306"]
     interval: 30s
     timeout: 10s
     retries: 3
   ```

3. **命令检查**（自定义）
   ```yaml
   healthcheck:
     test: ["CMD", "pg_isready", "-U", "postgres"]
     interval: 30s
     timeout: 10s
     retries: 3
   ```

**参数说明：**
- `test` - 检查命令（必须）
- `interval` - 检查间隔（默认 30s）
- `timeout` - 超时时间（默认 10s）
- `retries` - 失败重试次数（默认 3）
- `start_period` - 启动宽限期（默认 0s）

**示例：**
```yaml
services:
  nginx:
    image: nginx:latest
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:80"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

---

### 4. `depends_on` + `condition` - 等待服务就绪

```yaml
services:
  app:
    image: myapp:latest
    depends_on:
      mysql:
        condition: service_healthy  # 等 mysql 健康后才启动
      redis:
        condition: service_started  # redis 启动后就启动（不等待健康）
  
  mysql:
    image: mysql:8.0
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
```

**作用：** 更精确地控制启动顺序

**condition 可选值：**
- `service_started` - 服务启动后（默认）
- `service_healthy` - 服务健康后（需要配置 healthcheck）
- `service_completed_successfully` - 服务成功完成后（一次性任务）

**示例：**
```yaml
services:
  web:
    image: nginx:latest
    depends_on:
      db:
        condition: service_healthy  # 等数据库健康后才启动
      cache:
        condition: service_started  # 缓存启动后就启动
  
  db:
    image: mysql:8.0
    healthcheck:
      test: ["CMD", "mysqladmin", "ping"]
      interval: 10s
      retries: 5
```

---

### 5. `deploy.restart_policy` - 部署重启策略（Swarm 模式）

```yaml
services:
  web:
    image: nginx:latest
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
```

**作用：** 在 Docker Swarm 模式下使用（单机模式用 `restart` 就行）

**参数说明：**
- `condition` - 重启条件（`none`、`on-failure`、`any`）
- `delay` - 重启延迟时间
- `max_attempts` - 最大重启次数
- `window` - 时间窗口

**注意：** 单机 Docker Compose 不需要这个，用 `restart` 就够了。

---

### 6. `init` - 使用 init 进程

```yaml
services:
  web:
    image: nginx:latest
    init: true
```

**作用：** 使用 init 进程处理僵尸进程，容器退出时清理子进程

**适用场景：** 容器内会启动多个进程的服务

**默认值：** `false`

---

### 7. `stop_grace_period` - 停止宽限期

```yaml
services:
  web:
    image: nginx:latest
    stop_grace_period: 30s
```

**作用：** 停止容器时等待的时间，让容器优雅关闭

**默认值：** `10s`

**示例：**
```yaml
services:
  mysql:
    image: mysql:8.0
    stop_grace_period: 30s  # 停止时等30秒，让 MySQL 优雅关闭
```

---

## 📋 启动相关参数总结

| 参数 | 作用 | 常用值 |
|------|------|--------|
| `restart` | 重启策略和开机自启 | `unless-stopped`（推荐） |
| `depends_on` | 控制启动顺序 | `- 服务名` |
| `healthcheck` | 健康检查 | 根据服务类型配置 |
| `init` | 使用 init 进程 | `true`（多进程服务） |
| `stop_grace_period` | 停止宽限期 | `30s`（数据库等） |

---

## 🎯 开机自启动完整配置示例

```yaml
version: '3.8'

services:
  # Web 服务
  nginx:
    image: nginx:latest
    container_name: nginx
    restart: unless-stopped  # 服务器重启后自动启动
    ports:
      - "80:80"
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:80"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
  
  # 数据库服务
  mysql:
    image: mysql:8.0
    container_name: mysql
    restart: unless-stopped  # 服务器重启后自动启动
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=123456
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
    stop_grace_period: 30s  # 停止时优雅关闭
  
  # 应用服务（依赖数据库）
  app:
    image: myapp:latest
    container_name: app
    restart: unless-stopped  # 服务器重启后自动启动
    depends_on:
      mysql:
        condition: service_healthy  # 等数据库健康后才启动
    ports:
      - "8080:8080"
```

**说明：**
1. `restart: unless-stopped` - 服务器重启后自动启动
2. `depends_on` - 控制启动顺序
3. `healthcheck` - 检查服务是否健康
4. `stop_grace_period` - 优雅关闭

---

## 📝 最简单的示例

### 示例 1：启动 Nginx（最简单）

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

### 示例 2：启动 MySQL（加环境变量）

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=123456
```

**运行：** `docker compose up -d`

**连接：** `mysql -h localhost -P 3306 -u root -p123456`

---

### 示例 3：启动 Redis（加数据持久化）

```yaml
version: '3.8'

services:
  redis:
    image: redis:latest
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  redis_data:
```

**运行：** `docker compose up -d`

**连接：** `redis-cli -h localhost -p 6379`

---

### 示例 4：启动 Neo4j（完整示例）

```yaml
version: '3.8'

services:
  neo4j:
    image: neo4j:latest
    container_name: neo4j
    restart: always
    ports:
      - "7474:7474"
      - "7687:7687"
    environment:
      - NEO4J_AUTH=neo4j/password
    volumes:
      - neo4j_data:/data

volumes:
  neo4j_data:
```

**运行：** `docker compose up -d`

**访问：** http://localhost:7474（用户名：neo4j，密码：password）

---

## 🚀 常用命令

### 启动和停止

```bash
# 启动服务（后台运行）
docker compose up -d

# 启动服务（前台运行，看日志）
docker compose up

# 停止服务
docker compose down

# 停止并删除数据卷（数据会丢失！）
docker compose down -v
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
```

### 其他操作

```bash
# 重启服务
docker compose restart

# 重启某个服务
docker compose restart 服务名

# 进入容器
docker compose exec 服务名 sh

# 更新镜像
docker compose pull
docker compose up -d
```

---

## 💡 快速参考

### 最小配置模板

```yaml
version: '3.8'

services:
  服务名:
    image: 镜像名:标签
    ports:
      - "宿主机端口:容器端口"
```

### 完整配置模板（包含启动相关参数）

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
    depends_on:
      - 依赖的服务名
    healthcheck:
      test: ["CMD", "检查命令"]
      interval: 30s
      timeout: 10s
      retries: 3
    stop_grace_period: 30s

volumes:
  数据卷名:
```

### 启动相关参数快速参考表

| 参数 | 作用 | 推荐值 | 示例 |
|------|------|--------|------|
| `restart` | 重启策略和开机自启 | `unless-stopped` | `restart: unless-stopped` |
| `depends_on` | 控制启动顺序 | 列表 | `depends_on: - db` |
| `healthcheck.test` | 健康检查命令 | 根据服务 | `test: ["CMD", "curl", "-f", "http://localhost"]` |
| `healthcheck.interval` | 检查间隔 | `30s` | `interval: 30s` |
| `healthcheck.timeout` | 超时时间 | `10s` | `timeout: 10s` |
| `healthcheck.retries` | 失败重试次数 | `3` | `retries: 3` |
| `healthcheck.start_period` | 启动宽限期 | `40s` | `start_period: 40s` |
| `stop_grace_period` | 停止宽限期 | `30s` | `stop_grace_period: 30s` |
| `init` | 使用 init 进程 | `true`（多进程） | `init: true` |

---

## ❓ 常见问题

### Q: 端口被占用怎么办？

**A:** 换个端口号，比如把 `8080` 改成 `8081`

```yaml
ports:
  - "8081:80"  # 改成8081
```

### Q: 怎么知道服务需要什么环境变量？

**A:** 看镜像的文档，或者去 Docker Hub 搜镜像名，看说明

### Q: 数据卷是干什么的？

**A:** 保存数据用的。不写数据卷，容器删了数据就没了；写了数据卷，数据会保存到宿主机

### Q: `restart: always` 和 `unless-stopped` 有什么区别？

**A:** 
- `always` - 任何时候都重启（包括手动停止后）
- `unless-stopped` - 手动停止后不重启（推荐用这个）

---

## 📚 总结

**最基础的 3 个参数：**
1. `version` - 版本号
2. `services` - 服务定义
3. `image` - 镜像名称

**最常用的 5 个参数：**
1. `ports` - 端口映射
2. `volumes` - 数据卷
3. `environment` - 环境变量
4. `container_name` - 容器名
5. `restart` - 重启策略

**启动和重启相关参数（7个）：**
1. `restart` - 重启策略（`always`、`unless-stopped`、`on-failure`、`no`）
2. `depends_on` - 服务依赖和启动顺序
3. `healthcheck` - 健康检查（定期检查服务是否正常）
4. `depends_on.condition` - 等待服务就绪（`service_started`、`service_healthy`）
5. `init` - 使用 init 进程（处理僵尸进程）
6. `stop_grace_period` - 停止宽限期（优雅关闭）
7. `deploy.restart_policy` - Swarm 模式重启策略（单机不用）

**开机自启动：**
- 使用 `restart: always` 或 `restart: unless-stopped` 即可实现开机自启动
- 只要 Docker 服务开机自启，这些容器就会自动启动

**记住：** 只要有了 `version`、`services`、`image` 和 `ports`，就能启动一个服务了！

---

**最后更新**: 2025-12-10
