# Docker 多架构镜像和 Digest 详解

## 📋 目录

- [Index Digest vs Manifest Digest](#index-digest-vs-manifest-digest)
- [为什么两个 Digest 不一样？](#为什么两个-digest-不一样)
- [如何指定拉取对应架构](#如何指定拉取对应架构)
- [使用 Digest 拉取镜像](#使用-digest-拉取镜像)
- [查看远程仓库的架构信息](#查看远程仓库的架构信息)
- [实际示例](#实际示例)

---

## 🎯 Index Digest vs Manifest Digest

### 什么是 Manifest Digest？

**Manifest Digest（清单摘要）** 是单个平台特定镜像的唯一标识符。

- 每个架构（amd64、arm64）都有自己的 Manifest
- Manifest 是一个 JSON 文件，描述了该架构镜像的所有层和配置
- Manifest Digest 是 Manifest 文件的 SHA-256 哈希值

**示例：**
```
linux/amd64 镜像 → Manifest Digest: e03a115433a2...
linux/arm64 镜像 → Manifest Digest: 7336edbb236f...
```

---

### 什么是 Index Digest？

**Index Digest（索引摘要）** 是多架构镜像的清单列表摘要。

- Index（也叫 Manifest List）是一个 JSON 文件，包含了所有架构的 Manifest 引用
- Index Digest 是整个 Index 文件的 SHA-256 哈希值
- 当你拉取多架构镜像时，Docker 先获取 Index，然后根据你的系统架构选择对应的 Manifest

**结构示意：**
```
Index (Manifest List)
├── linux/amd64 → Manifest Digest: e03a115433a2...
├── linux/arm64 → Manifest Digest: 7336edbb236f...
└── linux/arm/v7 → Manifest Digest: xxxxx...
```

---

## ❓ 为什么两个 Digest 不一样？

### 原因说明

**Index Digest ≠ Manifest Digest** 因为它们是完全不同的东西：

1. **Index Digest** - 是整个多架构镜像的"目录"的哈希值
2. **Manifest Digest** - 是单个架构镜像的"内容"的哈希值

**类比理解：**
- Index Digest = 一本书的目录页的哈希值
- Manifest Digest = 书中某一章节内容的哈希值

**为什么需要两个？**
- Index Digest 用于标识整个多架构镜像（所有架构的集合）
- Manifest Digest 用于标识特定架构的镜像（单个架构的内容）

---

## 🔧 如何指定拉取对应架构

### 方法一：使用 `--platform` 参数（推荐）

```bash
# 拉取 amd64 架构
docker pull --platform linux/amd64 nacos/nacos-server:v1.4.8-slim

# 拉取 arm64 架构
docker pull --platform linux/arm64 nacos/nacos-server:v1.4.8-slim

# 拉取 arm/v7 架构（如果支持）
docker pull --platform linux/arm/v7 nacos/nacos-server:v1.4.8-slim
```

**说明：**
- `--platform` 参数会强制拉取指定架构的镜像
- 即使你的系统是 arm64，也可以拉取 amd64 镜像（通过模拟）

---

### 方法二：在 docker-compose.yml 中指定

```yaml
version: '3.8'

services:
  nacos:
    image: nacos/nacos-server:v1.4.8-slim
    platform: linux/amd64  # 指定架构
    # 或者
    # platform: linux/arm64
```

---

### 方法三：使用 Dockerfile 的 FROM 指令

```dockerfile
# 指定架构
FROM --platform=linux/amd64 nacos/nacos-server:v1.4.8-slim
```

---

## 🔐 使用 Digest 拉取镜像

### 使用 Manifest Digest 拉取（指定架构）

```bash
# 拉取 amd64 架构（使用 Manifest Digest）
docker pull nacos/nacos-server@sha256:e03a115433a2...

# 拉取 arm64 架构（使用 Manifest Digest）
docker pull nacos/nacos-server@sha256:7336edbb236f...
```

**格式：** `镜像名@sha256:digest值`

**优点：**
- ✅ 精确指定架构
- ✅ 确保拉取的是特定版本的镜像
- ✅ 不受标签更新影响

---

### 使用 Index Digest 拉取（多架构）

```bash
# 使用 Index Digest（Docker 会自动选择适合的架构）
docker pull nacos/nacos-server@sha256:index_digest...
```

**说明：**
- 使用 Index Digest 时，Docker 会根据你的系统架构自动选择对应的 Manifest
- 如果你在 amd64 系统上，会拉取 amd64 的 Manifest
- 如果你在 arm64 系统上，会拉取 arm64 的 Manifest

---

### 在 docker-compose.yml 中使用 Digest

```yaml
version: '3.8'

services:
  nacos:
    # 使用 Manifest Digest（指定架构）
    image: nacos/nacos-server@sha256:e03a115433a2...
    
    # 或者使用 Index Digest（自动选择架构）
    # image: nacos/nacos-server@sha256:index_digest...
```

---

## 🔍 查看远程仓库的架构信息

### 方法一：使用 `docker manifest inspect`（推荐）

```bash
# 查看多架构镜像的所有架构
docker manifest inspect nacos/nacos-server:v1.4.8-slim

# 查看特定架构的 Manifest
docker manifest inspect --verbose nacos/nacos-server:v1.4.8-slim
```

**输出示例：**
```json
{
   "schemaVersion": 2,
   "mediaType": "application/vnd.docker.distribution.manifest.list.v2+json",
   "manifests": [
      {
         "mediaType": "application/vnd.docker.distribution.manifest.v2+json",
         "size": 1234,
         "digest": "sha256:e03a115433a2...",
         "platform": {
            "architecture": "amd64",
            "os": "linux"
         }
      },
      {
         "mediaType": "application/vnd.docker.distribution.manifest.v2+json",
         "size": 1234,
         "digest": "sha256:7336edbb236f...",
         "platform": {
            "architecture": "arm64",
            "os": "linux"
         }
      }
   ]
}
```

**关键信息：**
- `manifests` 数组列出了所有支持的架构
- 每个架构都有 `digest` 和 `platform` 信息

---

### 方法二：使用 `docker buildx imagetools inspect`

```bash
# 查看镜像的所有架构
docker buildx imagetools inspect nacos/nacos-server:v1.4.8-slim

# 只显示架构列表
docker buildx imagetools inspect nacos/nacos-server:v1.4.8-slim --raw | jq '.manifests[].platform'
```

**输出示例：**
```
Name:      nacos/nacos-server:v1.4.8-slim
MediaType: application/vnd.docker.distribution.manifest.list.v2+json
Digest:    sha256:index_digest...

Manifests:
  Name:      nacos/nacos-server:v1.4.8-slim
  MediaType: application/vnd.docker.distribution.manifest.v2+json
  Digest:    sha256:e03a115433a2...
  Platform:  linux/amd64

  Name:      nacos/nacos-server:v1.4.8-slim
  MediaType: application/vnd.docker.distribution.manifest.v2+json
  Digest:    sha256:7336edbb236f...
  Platform:  linux/arm64
```

---

### 方法三：使用 API 直接查询

```bash
# 查询镜像的 Manifest List
curl -H "Accept: application/vnd.docker.distribution.manifest.list.v2+json" \
  https://registry-1.docker.io/v2/nacos/nacos-server/manifests/v1.4.8-slim

# 需要先获取 token（复杂，不推荐）
```

---

### 方法四：在 Docker Hub 网页查看

1. 访问 https://hub.docker.com/r/nacos/nacos-server/tags
2. 点击标签 `v1.4.8-slim`
3. 查看 "Digest" 和 "OS/ARCH" 信息

---

## 💡 实际示例

### 示例 1：查看 Nacos 镜像支持的架构

```bash
# 查看所有架构
docker manifest inspect nacos/nacos-server:v1.4.8-slim | jq '.manifests[].platform'

# 输出：
# {
#   "architecture": "amd64",
#   "os": "linux"
# }
# {
#   "architecture": "arm64",
#   "os": "linux"
# }
```

---

### 示例 2：拉取特定架构的镜像

```bash
# 拉取 amd64 版本
docker pull --platform linux/amd64 nacos/nacos-server:v1.4.8-slim

# 拉取 arm64 版本
docker pull --platform linux/arm64 nacos/nacos-server:v1.4.8-slim

# 验证拉取的架构
docker image inspect nacos/nacos-server:v1.4.8-slim | jq '.[0].Architecture'
```

---

### 示例 3：使用 Digest 拉取

```bash
# 假设 Index Digest 是 sha256:abc123...
docker pull nacos/nacos-server@sha256:abc123...

# 假设 amd64 的 Manifest Digest 是 sha256:e03a115433a2...
docker pull nacos/nacos-server@sha256:e03a115433a2...

# 假设 arm64 的 Manifest Digest 是 sha256:7336edbb236f...
docker pull nacos/nacos-server@sha256:7336edbb236f...
```

---

### 示例 4：在 docker-compose.yml 中指定架构

```yaml
version: '3.8'

services:
  nacos:
    image: nacos/nacos-server:v1.4.8-slim
    platform: linux/amd64  # 强制使用 amd64 架构
    ports:
      - "8848:8848"
    environment:
      - MODE=standalone
```

---

## 📊 总结对比

| 项目 | Index Digest | Manifest Digest |
|------|--------------|-----------------|
| **作用** | 标识整个多架构镜像 | 标识单个架构镜像 |
| **范围** | 所有架构的集合 | 单个架构 |
| **使用场景** | 拉取多架构镜像（自动选择） | 拉取特定架构镜像 |
| **格式** | `镜像名@sha256:index_digest` | `镜像名@sha256:manifest_digest` |
| **数量** | 1个（每个标签） | 多个（每个架构1个） |

---

## ❓ 常见问题

### Q: 我应该用 Index Digest 还是 Manifest Digest？

**A:** 
- **用 Index Digest** - 如果你想让 Docker 自动选择适合的架构（推荐）
- **用 Manifest Digest** - 如果你需要精确控制架构（生产环境推荐）

---

### Q: 如何同时拉取两个架构的镜像？

**A:** 
```bash
# 方法1: 分别拉取并打不同标签
docker pull --platform linux/amd64 nacos/nacos-server:v1.4.8-slim
docker tag nacos/nacos-server:v1.4.8-slim nacos/nacos-server:v1.4.8-slim-amd64

docker pull --platform linux/arm64 nacos/nacos-server:v1.4.8-slim
docker tag nacos/nacos-server:v1.4.8-slim nacos/nacos-server:v1.4.8-slim-arm64

# 方法2: 使用不同的本地标签
docker pull --platform linux/amd64 nacos/nacos-server:v1.4.8-slim
docker pull --platform linux/arm64 nacos/nacos-server:v1.4.8-slim
# 注意：第二个会覆盖第一个，所以需要先打标签
```

---

### Q: 如何查看本地镜像的架构？

**A:**
```bash
# 查看镜像架构
docker image inspect nacos/nacos-server:v1.4.8-slim | jq '.[0].Architecture'

# 查看镜像的所有信息
docker image inspect nacos/nacos-server:v1.4.8-slim
```

---

### Q: 为什么 `docker manifest inspect` 命令失败？

**A:** 可能的原因：
1. 网络问题（无法访问 Docker Hub）
2. 需要启用实验性功能：`export DOCKER_CLI_EXPERIMENTAL=enabled`
3. 镜像不存在或标签错误

**解决方法：**
```bash
# 启用实验性功能（Docker 20.10+ 已默认启用）
export DOCKER_CLI_EXPERIMENTAL=enabled

# 或者使用 buildx
docker buildx imagetools inspect nacos/nacos-server:v1.4.8-slim
```

---

### Q: Digest 会变吗？

**A:**
- **Manifest Digest** - 不会变（除非重新构建相同架构的镜像）
- **Index Digest** - 会变（如果添加或删除架构）

**建议：** 生产环境使用 Manifest Digest 确保一致性。

---

**最后更新**: 2025-12-10
