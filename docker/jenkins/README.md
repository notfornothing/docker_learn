# Jenkins - Docker Compose 部署指南

## 📋 目录

- [简介](#简介)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [常用操作](#常用操作)
- [Docker Compose 命令](#docker-compose-命令)

---

## 📖 简介

**Jenkins** 是一个开源的持续集成和持续交付（CI/CD）工具。

### 配置说明

- **镜像**: `jenkins/jenkins:lts`（长期支持版本）
- **端口**: `8080`（Web UI）、`50000`（Agent 通信）
- **数据持久化**: 使用命名卷 `jenkins_data`

---

## 🚀 快速开始

### 1. 启动服务

```bash
# 进入目录
cd docker/jenkins

# 启动 Jenkins（后台运行）
docker compose up -d

# 查看启动日志
docker compose logs -f jenkins
```

### 2. 访问 Jenkins

启动成功后，访问以下地址：

- **Jenkins 控制台**: http://localhost:8080

### 3. 初始设置

首次访问需要：

1. **获取初始管理员密码**
   ```bash
   # 查看初始密码
   docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```

2. **安装推荐插件**（或选择自定义插件）

3. **创建管理员账号**

4. **完成设置**

---

## ⚙️ 配置说明

### 端口说明

| 端口 | 用途 | 说明 |
|------|------|------|
| 8080 | Web UI | Jenkins 控制台访问端口 |
| 50000 | Agent 通信 | Jenkins Agent 连接端口 |

### 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `TZ` | `Asia/Shanghai` | 时区设置 |

### 数据卷说明

| 卷名/路径 | 挂载路径 | 说明 |
|---------|----------|------|
| `jenkins_data` | `/var/jenkins_home` | Jenkins 数据目录（配置、插件、工作空间等） |
| `/usr/local/maven` | `/usr/local/maven` | Maven 目录（可选，根据实际情况修改） |

### ⚠️ 重要：Jenkins 数据存储位置

**Jenkins 的所有数据都保存在 `/var/jenkins_home` 目录：**

```
/var/jenkins_home/
├── plugins/          # ✅ 插件目录（所有插件都在这里）
├── jobs/             # ✅ 任务配置和工作空间
├── users/            # ✅ 用户配置
├── secrets/          # ✅ 密钥和密码
├── config.xml        # ✅ Jenkins 主配置
└── ...               # ✅ 其他所有配置和数据
```

**关键点：**
- ✅ **插件保存在 `/var/jenkins_home/plugins/`**
- ✅ **这个目录已经挂载到 `jenkins_data` 卷**
- ✅ **重启后插件不会丢失！**

**验证方法：**

```bash
# 查看插件目录
docker compose exec jenkins ls -la /var/jenkins_home/plugins

# 查看卷的实际位置
docker volume inspect jenkins_jenkins_data

# 查看卷中的插件（需要 root 权限）
sudo ls -la /var/lib/docker/volumes/jenkins_jenkins_data/_data/plugins
```

---

## 💾 挂载宿主机目录

### 重要说明：数据不会丢失！

**✅ 添加新的 volumes 挂载不会影响现有数据！**

原因：
- `jenkins_data` 卷是独立的，不依赖容器
- 添加新的挂载只是增加新的目录映射
- 不会删除或覆盖现有的 `jenkins_data` 卷

### 如何添加 Maven 目录挂载

#### 步骤 1：找到宿主机 Maven 路径

```bash
# 查看 Maven 安装路径
which mvn
# 输出：/usr/local/maven/bin/mvn

# 查看 Maven 主目录
mvn -version
# 或
echo $MAVEN_HOME
```

**常见路径：**
- `/usr/local/maven` - Linux
- `/opt/maven` - Linux
- `/usr/share/maven` - Linux
- `C:\Program Files\Apache\maven` - Windows（需要转换路径）

#### 步骤 2：修改 docker-compose.yml

```yaml
volumes:
  - jenkins_data:/var/jenkins_home
  # 添加 Maven 目录挂载
  - /usr/local/maven:/usr/local/maven:ro  # 只读挂载（推荐）
```

**说明：**
- `:ro` 表示只读（read-only），Jenkins 只能读取，不能修改 Maven
- 不写 `:ro` 表示可读写（Jenkins 可以修改 Maven）

#### 步骤 3：重启 Jenkins（数据不会丢失）

```bash
# 停止容器
docker compose down

# 启动容器（会使用新的配置）
docker compose up -d

# 验证数据还在
docker compose exec jenkins ls -la /var/jenkins_home
# 应该能看到之前的配置和插件
```

**✅ 数据还在！** Jenkins 的配置、插件、工作空间都还在。

#### 步骤 4：验证 Maven 挂载成功

```bash
# 进入容器
docker compose exec jenkins bash

# 查看 Maven 是否挂载成功
ls -la /usr/local/maven

# 查看 Maven 版本
/usr/local/maven/bin/mvn -version
```

### 完整配置示例

```yaml
version: '3.8'

services:
  jenkins:
    image: jenkins/jenkins:lts
    container_name: jenkins
    restart: unless-stopped
    ports:
      - "58080:8080"
      - "50000:50000"
    volumes:
      - jenkins_data:/var/jenkins_home
      # Maven 目录（根据实际情况修改路径）
      - /usr/local/maven:/usr/local/maven:ro
      # 其他工具目录（可选）
      # - /usr/local/java:/usr/local/java:ro
      # - /usr/local/git:/usr/local/git:ro
    environment:
      - TZ=Asia/Shanghai

volumes:
  jenkins_data:
```

### 在 Jenkins 中配置 Maven

挂载后，需要在 Jenkins 中配置 Maven：

1. 访问 http://localhost:58080
2. 登录后点击 **"Manage Jenkins"** → **"Global Tool Configuration"**
3. 找到 **"Maven"** 部分
4. 点击 **"Add Maven"**
5. 配置：
   - **Name**: `Maven`（随便起名）
   - **MAVEN_HOME**: `/usr/local/maven`（容器内的路径）
6. 保存

### 其他常用目录挂载

```yaml
volumes:
  - jenkins_data:/var/jenkins_home
  # Maven
  - /usr/local/maven:/usr/local/maven:ro
  # Java
  - /usr/local/java:/usr/local/java:ro
  # Git
  - /usr/bin/git:/usr/bin/git:ro
  # Docker（让 Jenkins 使用宿主机的 Docker）
  - /var/run/docker.sock:/var/run/docker.sock
  # 自定义工具目录
  - /opt/tools:/opt/tools:ro
```

**注意：**
- 使用 `:ro` 只读挂载更安全（Jenkins 不能修改工具）
- Docker socket 挂载需要谨慎（Jenkins 可以控制宿主机 Docker）

---

## 🔧 常用操作

### 查看初始密码

```bash
# 方法1: 使用 docker compose
docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# 方法2: 使用 docker
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### 进入 Jenkins 容器

```bash
# 进入容器
docker compose exec jenkins bash

# 查看 Jenkins 配置
ls -la /var/jenkins_home
```

### 安装插件

1. 访问 http://localhost:8080
2. 登录后点击 **"Manage Jenkins"** → **"Manage Plugins"**
3. 在 **"Available"** 标签页搜索并安装插件

### 常用插件推荐

- **Git Plugin** - Git 支持
- **Docker Pipeline** - Docker 集成
- **Blue Ocean** - 现代化 UI
- **Pipeline** - Pipeline 支持
- **Credentials Binding** - 凭证管理

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
docker compose restart jenkins

# 查看服务状态
docker compose ps

# 查看日志（实时）
docker compose logs -f jenkins
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

### 完整配置（自定义内存）

```yaml
version: '3.8'

services:
  jenkins:
    image: jenkins/jenkins:lts
    container_name: jenkins
    restart: unless-stopped
    ports:
      - "8080:8080"
      - "50000:50000"
    volumes:
      - jenkins_data:/var/jenkins_home
    environment:
      - TZ=Asia/Shanghai
      - JAVA_OPTS=-Xmx2048m -Xms1024m  # 自定义 JVM 内存
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/login"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

volumes:
  jenkins_data:
```

---

## ⚠️ 注意事项

1. **初始密码**
   - 首次启动后需要从日志或容器中获取初始密码
   - 设置完成后可以修改密码

2. **数据备份**
   - 定期备份 `jenkins_data` 卷
   - 包含所有配置、插件和工作空间

3. **性能优化**
   - 根据服务器内存调整 JVM 参数
   - 使用 `JAVA_OPTS` 环境变量设置内存

4. **安全建议**
   - 生产环境使用 HTTPS（配置反向代理）
   - 定期更新 Jenkins 版本
   - 限制访问 IP（使用防火墙或反向代理）

---

## 📚 参考链接

- [Jenkins 官方文档](https://www.jenkins.io/doc/)
- [Jenkins Docker Hub](https://hub.docker.com/r/jenkins/jenkins)
- [Jenkins 插件中心](https://plugins.jenkins.io/)

---

**最后更新**: 2025-12-10


