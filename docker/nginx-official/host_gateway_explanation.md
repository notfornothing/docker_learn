# Docker `extra_hosts` 和 `host-gateway` 详解

## 简单理解

### 问题：容器如何访问宿主机上的服务？

**场景**：你的后端服务运行在**宿主机**（Mac/Linux 服务器）的 9000 端口，nginx 容器需要代理到这个服务。

**问题**：在容器内，`127.0.0.1` 指向的是**容器自己**，不是宿主机！

### 解决方案：`host.docker.internal`

`host.docker.internal` 是一个特殊的主机名，专门用来表示"宿主机"。

```
容器内访问：http://host.docker.internal:9000/
实际访问：   http://宿主机IP:9000/
```

## `extra_hosts` 的作用

`extra_hosts` 就是在容器的 `/etc/hosts` 文件中添加一行映射：

```yaml
extra_hosts:
  - "host.docker.internal:host-gateway"
```

**实际效果**：在容器的 `/etc/hosts` 文件中添加：
```
192.168.65.254  host.docker.internal
```

这样容器就能通过 `host.docker.internal` 这个域名访问宿主机了。

## `host-gateway` 是什么？

`host-gateway` 是 Docker 的一个**特殊值**，Docker 会自动把它解析为**宿主机（Docker 网关）的 IP 地址**。

### 不同操作系统的行为：

| 操作系统 | `host.docker.internal` 支持 | 需要 `extra_hosts` 吗？ |
|---------|---------------------------|----------------------|
| **Mac** | ✅ 自动支持 | ❌ 不需要 |
| **Windows** | ✅ 自动支持 | ❌ 不需要 |
| **Linux** | ❌ 不支持 | ✅ **需要手动添加** |

### 为什么 Linux 需要手动添加？

- Mac/Windows：Docker Desktop 会自动配置 `host.docker.internal`
- Linux：Docker 引擎默认不提供这个功能，需要手动配置

## 实际例子

### 不使用 `host.docker.internal`（错误）：

```nginx
# 在 nginx 配置中
proxy_pass http://127.0.0.1:9000/;  # ❌ 错误！指向容器自己，不是宿主机
```

### 使用 `host.docker.internal`（正确）：

```nginx
# 在 nginx 配置中
proxy_pass http://host.docker.internal:9000/;  # ✅ 正确！指向宿主机
```

### Docker Compose 配置：

```yaml
services:
  nginx:
    # Linux 需要这行，Mac/Windows 可以省略（但加上也没问题）
    extra_hosts:
      - "host.docker.internal:host-gateway"
```

## 验证方法

```bash
# 1. 查看容器内的 hosts 文件
docker exec nginx-official cat /etc/hosts

# 2. 测试是否能解析
docker exec nginx-official ping host.docker.internal

# 3. 测试是否能访问宿主机服务
docker exec nginx-official curl http://host.docker.internal:9000/
```

## 总结

- **`extra_hosts`**：在容器中添加主机名映射（相当于修改 `/etc/hosts`）
- **`host-gateway`**：Docker 的特殊值，自动解析为宿主机 IP
- **`host.docker.internal`**：一个约定俗成的域名，用来表示"宿主机"
- **为什么需要**：容器内的 `127.0.0.1` 指向容器自己，无法访问宿主机服务
- **Mac/Windows**：可以不加（Docker Desktop 自动支持），但加上也没问题
- **Linux**：**必须加**，否则 `host.docker.internal` 无法解析

## 如果不想用 `host.docker.internal`？

你也可以直接用宿主机 IP：

```yaml
extra_hosts:
  - "myhost:192.168.1.100"  # 替换为你的实际宿主机 IP
```

然后在 nginx 配置中使用：
```nginx
proxy_pass http://myhost:9000/;
```

但用 `host-gateway` 更灵活，因为 IP 地址可能会变化。

