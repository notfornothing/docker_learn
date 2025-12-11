# 使用指南

## 🚀 快速开始

### 1. 启动项目

```bash
# 编译并运行
mvn spring-boot:run
```

启动后，控制台会显示访问地址。

### 2. 查看所有 Docker 服务

访问 API：
```bash
curl http://localhost:10086/api/docker/services
```

这会自动扫描 `docker/` 目录下的所有服务，返回：
- 服务名称
- 服务标题（从 README.md 提取）
- 服务描述
- compose 文件路径
- README 内容

### 3. 启动 Docker 服务

#### 方式一：进入服务目录启动（推荐）

```bash
# 进入服务目录
cd docker/nginx-proxy-manager

# 启动服务
docker compose up -d

# 查看日志
docker compose logs -f

# 停止服务
docker compose down
```

#### 方式二：从项目根目录启动

```bash
# 启动所有服务
docker compose up -d

# 启动指定服务
docker compose up -d nginx-proxy-manager
```

## 📋 Docker 服务目录结构

每个 Docker 服务目录应包含：

```
docker/[service-name]/
├── docker-compose.yml    # Compose 配置文件（必需）
├── README.md            # 服务文档（推荐）
├── data/                # 数据目录（自动创建）
└── ...
```

## 🔍 服务识别规则

项目会自动识别 `docker/` 目录下满足以下条件的服务：

1. ✅ 是一个目录
2. ✅ 目录下有 `docker-compose.yml` 文件
3. ✅ 目录名不以 `.` 开头
4. ✅ 目录名不是 `data`

## 📝 添加新服务

1. **创建服务目录**
   ```bash
   mkdir -p docker/my-service
   cd docker/my-service
   ```

2. **创建 docker-compose.yml**
   ```yaml
   version: '3.8'
   services:
     my-service:
       image: 'my-image:latest'
       ports:
         - '8080:8080'
   ```

3. **创建 README.md**（可选但推荐）
   ```bash
   cp ../TEMPLATE.md README.md
   # 编辑 README.md
   ```

4. **启动服务**
   ```bash
   docker compose up -d
   ```

5. **验证服务被识别**
   ```bash
   curl http://localhost:10086/api/docker/services
   ```

## 💡 使用示例

### 查看所有服务

```bash
curl http://localhost:10086/api/docker/services | jq
```

### 查看特定服务信息

```bash
curl http://localhost:10086/api/docker/services/nginx-proxy-manager | jq
```

### 查看服务的 README

```bash
curl http://localhost:10086/api/docker/services/nginx-proxy-manager/readme | jq -r '.readme'
```

### 启动服务

```bash
# 方式一：进入目录
cd docker/nginx-proxy-manager
docker compose up -d

# 方式二：指定文件路径
docker compose -f docker/nginx-proxy-manager/docker-compose.yml up -d
```

## 🎯 最佳实践

1. **每个服务独立目录**
   - 每个服务有自己的目录和 compose 文件
   - 便于管理和维护

2. **编写 README**
   - 每个服务都应该有 README.md
   - 参考 `docker/TEMPLATE.md` 模板
   - 包含快速开始、配置说明等

3. **数据目录管理**
   - 使用相对路径 `./data`
   - 数据目录已加入 `.gitignore`
   - 定期备份重要数据

4. **版本控制**
   - compose 文件和 README 提交到 Git
   - 数据目录不提交

---

**最后更新**: 2025-12-10


