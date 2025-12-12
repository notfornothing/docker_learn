# Docker Compose 开机自启动详解

## 核心问题：Docker Compose 不会自动启动！

**重要**：`docker compose up -d` 启动的容器，**不会**在系统重启后自动启动，除非你配置了自动启动。

## 三个层面的自启动

### 1. Docker 服务本身的自启动 ✅（通常已配置）

**Mac（Docker Desktop）**：
- Docker Desktop 默认开机自启动（在设置中可以配置）
- 位置：Docker Desktop → Settings → General → Start Docker Desktop when you log in

**Linux（systemd）**：
```bash
# 检查 Docker 服务是否开机自启动
systemctl is-enabled docker

# 如果未启用，启用开机自启动
sudo systemctl enable docker

# 启动 Docker 服务
sudo systemctl start docker
```

### 2. 容器的重启策略（Restart Policy）✅（已配置）

在你的 `docker-compose.yml` 中：
```yaml
restart: unless-stopped
```

**重启策略说明**：

| 策略 | 说明 | 开机自启动？ |
|-----|------|------------|
| `no` | 不自动重启 | ❌ 否 |
| `always` | 总是重启（即使手动停止） | ✅ 是 |
| `on-failure` | 失败时重启 | ⚠️ 部分（如果之前是运行状态） |
| `unless-stopped` | 除非手动停止，否则重启 | ✅ **是**（推荐） |

**你的配置**：`restart: unless-stopped`
- ✅ 系统重启后，容器会自动启动
- ✅ 容器异常退出后，会自动重启
- ✅ 手动停止后（`docker stop`），不会自动启动

### 3. Docker Compose 项目的自动启动 ❌（需要配置）

**问题**：即使容器有 `restart: unless-stopped`，如果 Docker Compose **项目没有启动**，容器也不会自动启动。

## 如何让 Docker Compose 项目开机自启动？

### 方法 1：使用 systemd（Linux 推荐）

创建 systemd 服务文件：

```bash
# 创建服务文件
sudo nano /etc/systemd/system/docker-compose-nginx.service
```

内容：
```ini
[Unit]
Description=Docker Compose Nginx Service
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/Users/mac/Project_IDEA/docker_learn/docker/nginx-official
ExecStart=/usr/local/bin/docker compose up -d
ExecStop=/usr/local/bin/docker compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

启用服务：
```bash
sudo systemctl daemon-reload
sudo systemctl enable docker-compose-nginx.service
sudo systemctl start docker-compose-nginx.service
```

### 方法 2：使用 launchd（Mac）

创建 plist 文件：

```bash
# 创建目录
mkdir -p ~/Library/LaunchAgents

# 创建 plist 文件
nano ~/Library/LaunchAgents/com.nginx.docker-compose.plist
```

内容：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>com.nginx.docker-compose</string>
    <key>ProgramArguments</key>
    <array>
        <string>/usr/local/bin/docker</string>
        <string>compose</string>
        <string>-f</string>
        <string>/Users/mac/Project_IDEA/docker_learn/docker/nginx-official/docker-compose.yml</string>
        <string>up</string>
        <string>-d</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
    <key>KeepAlive</key>
    <false/>
    <key>WorkingDirectory</key>
    <string>/Users/mac/Project_IDEA/docker_learn/docker/nginx-official</string>
</dict>
</plist>
```

加载服务：
```bash
launchctl load ~/Library/LaunchAgents/com.nginx.docker-compose.plist
```

### 方法 3：使用 Docker Compose 的 `restart: always`（简单但不完美）

修改 `docker-compose.yml`：
```yaml
restart: always  # 改为 always
```

然后使用 `docker update`：
```bash
docker update --restart=always nginx-official
```

**注意**：这只能让容器在 Docker 启动后自动启动，但如果 Docker Compose 项目没有运行，容器可能无法正常启动。

### 方法 4：使用 Docker Swarm（生产环境推荐）

```bash
# 初始化 Swarm
docker swarm init

# 部署服务
docker stack deploy -c docker-compose.yml nginx-stack
```

Swarm 会自动管理服务的重启和恢复。

## 当前你的配置状态

### ✅ 已配置的：
1. **容器重启策略**：`restart: unless-stopped`
   - 容器异常退出会自动重启
   - 系统重启后，如果 Docker 已启动，容器会自动启动

### ❌ 未配置的：
1. **Docker Compose 项目自启动**
   - 系统重启后，需要手动运行 `docker compose up -d`
   - 或者配置 systemd/launchd 服务

## 检查当前状态

```bash
# 1. 检查容器的重启策略
docker inspect nginx-official --format '{{.HostConfig.RestartPolicy.Name}}'

# 2. 检查 Docker 服务状态（Linux）
systemctl status docker

# 3. 检查 Docker Desktop 是否开机自启动（Mac）
# 打开 Docker Desktop → Settings → General
```

## 推荐方案

### Mac（开发环境）：
- ✅ Docker Desktop 已配置开机自启动
- ✅ 容器使用 `restart: unless-stopped`
- ⚠️ 如果需要，可以配置 launchd 服务

### Linux（生产环境）：
- ✅ 确保 Docker 服务开机自启动：`systemctl enable docker`
- ✅ 容器使用 `restart: unless-stopped`
- ✅ **推荐**：配置 systemd 服务让 Compose 项目自动启动

## 总结

| 项目 | 是否自启动 | 说明 |
|-----|----------|------|
| Docker 服务 | ✅ 通常已配置 | Mac/Windows 默认开启，Linux 需要配置 |
| 容器（restart: unless-stopped） | ✅ 是 | Docker 启动后，容器会自动启动 |
| Docker Compose 项目 | ❌ 否 | 需要手动运行或配置 systemd/launchd |

**你的情况**：
- ✅ 容器会在 Docker 启动后自动启动（因为 `restart: unless-stopped`）
- ❌ 但需要确保 Docker 服务本身已配置开机自启动

