# Neo4j - 图数据库 Docker Compose 部署指南

## 📋 目录

- [简介](#简介)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [常用操作](#常用操作)
- [Docker Compose 命令](#docker-compose-命令)
- [数据管理](#数据管理)
- [Cypher 查询示例](#cypher-查询示例)
- [故障排查](#故障排查)
- [参考链接](#参考链接)

---

## 📖 简介

**Neo4j** 是一个高性能的图数据库管理系统，使用图结构来存储和查询数据。它使用节点（Node）和关系（Relationship）来表示数据，非常适合处理复杂的关系型数据。

### 主要特性

- ✅ 原生图数据库，专为图数据设计
- ✅ Cypher 查询语言，直观易用
- ✅ 支持 ACID 事务
- ✅ 高性能图遍历和查询
- ✅ Web 管理界面（Neo4j Browser）
- ✅ 支持多种编程语言驱动（Java、Python、JavaScript 等）
- ✅ 丰富的插件生态（APOC、GDS 等）
- ✅ 支持集群部署

### 适用场景

- 社交网络分析
- 推荐系统
- 知识图谱
- 欺诈检测
- 实时推荐引擎
- 网络和 IT 基础设施管理
- 主数据管理

---

## 🚀 快速开始

### 前置要求

- Docker Engine 20.10+
- Docker Compose 2.0+
- 至少 2GB 可用内存（推荐 4GB+）

### 1. 启动服务

```bash
# 进入配置目录
cd docker/neo4j

# 启动服务（后台运行）
docker compose up -d

# 查看启动日志
docker compose logs -f neo4j
```

### 2. 访问 Neo4j Browser

启动成功后，访问以下地址：

- **Neo4j Browser**: http://localhost:7474
- **默认用户名**: `neo4j`
- **默认密码**: `password`

⚠️ **重要提示**: 首次登录后请立即修改默认密码！

### 3. 端口说明

| 端口 | 用途 | 说明 |
|------|------|------|
| 7474 | HTTP | Neo4j Browser Web 界面 |
| 7687 | Bolt | Neo4j 数据库连接端口（应用程序使用） |

### 4. 连接字符串

应用程序连接 Neo4j 时使用以下连接字符串：

```
bolt://localhost:7687
```

---

## ⚙️ 配置说明

### 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `NEO4J_AUTH` | `neo4j/password` | 认证配置（格式：用户名/密码） |
| `NEO4J_dbms_memory_heap_initial__size` | `512m` | 初始堆内存大小 |
| `NEO4J_dbms_memory_heap_max__size` | `2G` | 最大堆内存大小 |
| `NEO4J_dbms_default__listen__address` | `0.0.0.0` | 监听地址（0.0.0.0 允许所有主机） |
| `TZ` | `Asia/Shanghai` | 时区设置 |

### 数据卷说明

| 卷名 | 挂载路径 | 说明 |
|------|----------|------|
| `neo4j_data` | `/data` | 数据库数据文件 |
| `neo4j_logs` | `/logs` | 日志文件 |
| `neo4j_import` | `/var/lib/neo4j/import` | 数据导入目录（用于 LOAD CSV） |
| `neo4j_plugins` | `/plugins` | 插件目录（APOC、GDS 等） |

### 修改默认密码

在 `docker-compose.yml` 中修改 `NEO4J_AUTH` 环境变量：

```yaml
environment:
  - NEO4J_AUTH=neo4j/your_strong_password
```

然后重启服务：

```bash
docker compose down
docker compose up -d
```

### 内存配置优化

根据服务器内存情况调整堆内存大小：

```yaml
environment:
  # 小服务器（4GB RAM）
  - NEO4J_dbms_memory_heap_initial__size=512m
  - NEO4J_dbms_memory_heap_max__size=2G
  
  # 中等服务器（8GB RAM）
  - NEO4J_dbms_memory_heap_initial__size=1G
  - NEO4J_dbms_memory_heap_max__size=4G
  
  # 大服务器（16GB+ RAM）
  - NEO4J_dbms_memory_heap_initial__size=2G
  - NEO4J_dbms_memory_heap_max__size=8G
```

---

## 🔧 常用操作

### 使用 Neo4j Browser

1. 访问 http://localhost:7474
2. 使用默认账号登录（neo4j/password）
3. 首次登录会要求修改密码
4. 在查询框中输入 Cypher 查询语句
5. 点击运行按钮执行查询

### 导入数据

#### 方式一：使用 LOAD CSV（推荐）

1. 将 CSV 文件放到 `neo4j_import` 卷对应的目录
2. 在 Neo4j Browser 中执行：

```cypher
LOAD CSV WITH HEADERS FROM 'file:///your_file.csv' AS row
CREATE (n:Person {name: row.name, age: toInteger(row.age)})
```

#### 方式二：使用 Cypher Shell

```bash
# 进入容器
docker compose exec neo4j bash

# 使用 cypher-shell
cypher-shell -u neo4j -p your_password

# 执行 Cypher 语句
CREATE (n:Person {name: 'Alice', age: 30});
```

### 安装插件

#### 安装 APOC 插件

1. 下载 APOC 插件 JAR 文件
2. 将文件放到 `neo4j_plugins` 卷对应的目录
3. 在 `docker-compose.yml` 中添加配置：

```yaml
environment:
  - NEO4J_PLUGINS=["apoc"]
```

4. 重启服务

### 查看数据库信息

在 Neo4j Browser 中执行：

```cypher
// 查看所有节点标签
CALL db.labels()

// 查看所有关系类型
CALL db.relationshipTypes()

// 查看数据库统计信息
CALL db.stats.retrieve('GRAPH COUNTS')
```

---

## 🐳 Docker Compose 命令

### 基本操作

```bash
# 启动服务（后台运行）
docker compose up -d

# 启动服务（前台运行，查看日志）
docker compose up

# 停止服务
docker compose down

# 停止服务并删除数据卷（谨慎使用，会删除所有数据）
docker compose down -v

# 重启服务
docker compose restart neo4j

# 查看服务状态
docker compose ps

# 查看日志（实时）
docker compose logs -f neo4j

# 查看最近 100 行日志
docker compose logs --tail=100 neo4j
```

### 更新服务

```bash
# 拉取最新镜像
docker compose pull

# 停止旧容器
docker compose down

# 启动新容器
docker compose up -d

# 清理未使用的镜像
docker image prune -a
```

### 进入容器

```bash
# 进入容器 shell
docker compose exec neo4j bash

# 使用 Cypher Shell
docker compose exec neo4j cypher-shell -u neo4j -p your_password

# 查看容器信息
docker compose exec neo4j env
```

---

## 💾 数据管理

### 备份数据

#### 方式一：备份数据卷

```bash
# 停止服务
docker compose down

# 备份数据卷
docker run --rm \
  -v docker_learn_neo4j_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/neo4j-backup-$(date +%Y%m%d).tar.gz -C /data .

# 启动服务
docker compose up -d
```

#### 方式二：使用 Neo4j 备份命令

```bash
# 进入容器
docker compose exec neo4j bash

# 执行备份
neo4j-admin database dump neo4j --to-path=/backups
```

### 恢复数据

```bash
# 停止服务
docker compose down

# 恢复数据卷
docker run --rm \
  -v docker_learn_neo4j_data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/neo4j-backup-YYYYMMDD.tar.gz -C /data

# 启动服务
docker compose up -d
```

### 导出数据

```cypher
// 导出为 CSV
CALL apoc.export.csv.all('export.csv', {})

// 导出为 JSON
CALL apoc.export.json.all('export.json', {})
```

---

## 📝 Cypher 查询示例

### 创建节点和关系

```cypher
// 创建人物节点
CREATE (alice:Person {name: 'Alice', age: 30})
CREATE (bob:Person {name: 'Bob', age: 25})

// 创建关系
CREATE (alice)-[:KNOWS {since: 2020}]->(bob)
```

### 查询数据

```cypher
// 查找所有人物
MATCH (p:Person)
RETURN p

// 查找 Alice 认识的人
MATCH (alice:Person {name: 'Alice'})-[:KNOWS]->(friend)
RETURN friend

// 查找两度关系（朋友的朋友）
MATCH (alice:Person {name: 'Alice'})-[:KNOWS*2]->(friendOfFriend)
RETURN friendOfFriend
```

### 更新数据

```cypher
// 更新节点属性
MATCH (p:Person {name: 'Alice'})
SET p.age = 31
RETURN p

// 添加标签
MATCH (p:Person {name: 'Alice'})
SET p:Employee
RETURN p
```

### 删除数据

```cypher
// 删除节点和关系
MATCH (p:Person {name: 'Bob'})
DETACH DELETE p

// 删除所有数据（谨慎使用）
MATCH (n)
DETACH DELETE n
```

### 索引和约束

```cypher
// 创建索引
CREATE INDEX person_name_index FOR (p:Person) ON (p.name)

// 创建唯一约束
CREATE CONSTRAINT person_id_unique FOR (p:Person) REQUIRE p.id IS UNIQUE

// 查看所有索引和约束
CALL db.indexes()
CALL db.constraints()
```

---

## 🔍 故障排查

### 问题 1: 服务无法启动

1. **检查端口占用**
   ```bash
   lsof -i :7474
   lsof -i :7687
   ```

2. **查看日志**
   ```bash
   docker compose logs neo4j
   ```

3. **检查内存**
   ```bash
   # 查看容器资源使用
   docker stats neo4j
   ```

### 问题 2: 无法连接数据库

1. **检查认证信息**
   - 确认用户名和密码正确
   - 检查 `NEO4J_AUTH` 环境变量格式

2. **检查监听地址**
   - 确认 `NEO4J_dbms_default__listen__address=0.0.0.0`
   - 检查防火墙设置

3. **测试连接**
   ```bash
   docker compose exec neo4j cypher-shell -u neo4j -p password
   ```

### 问题 3: 内存不足

1. **调整内存配置**
   - 减少 `NEO4J_dbms_memory_heap_max__size`
   - 检查服务器可用内存

2. **查看内存使用**
   ```bash
   docker stats neo4j
   ```

### 问题 4: 数据导入失败

1. **检查文件路径**
   - CSV 文件必须在 `/var/lib/neo4j/import` 目录
   - 使用 `file:///` 前缀

2. **检查文件权限**
   ```bash
   docker compose exec neo4j ls -la /var/lib/neo4j/import
   ```

### 问题 5: 性能问题

1. **创建索引**
   ```cypher
   CREATE INDEX FOR (n:Label) ON (n.property)
   ```

2. **优化查询**
   - 使用 `EXPLAIN` 或 `PROFILE` 分析查询计划
   - 避免全图扫描

3. **调整内存配置**
   - 增加堆内存大小
   - 调整页面缓存大小

---

## 📚 参考链接

- [Neo4j 官方文档](https://neo4j.com/docs/)
- [Neo4j Browser 使用指南](https://neo4j.com/developer/neo4j-browser/)
- [Cypher 查询语言参考](https://neo4j.com/docs/cypher-manual/current/)
- [Neo4j Docker Hub](https://hub.docker.com/_/neo4j)
- [APOC 插件文档](https://neo4j.com/labs/apoc/)
- [Neo4j 图数据科学库 (GDS)](https://neo4j.com/docs/graph-data-science/current/)

---

## 📝 更新日志

### 2025-12-10
- 初始版本
- 配置 Neo4j 5.x 最新版本
- 配置数据持久化卷
- 配置健康检查
- 添加内存优化配置

---

## ⚠️ 注意事项

1. **安全建议**
   - 首次登录后立即修改默认密码
   - 生产环境使用强密码
   - 使用防火墙限制管理端口访问
   - 定期更新镜像版本
   - 定期备份数据
   - 考虑使用 SSL/TLS 加密连接

2. **性能优化**
   - 根据数据量调整内存配置
   - 为常用查询属性创建索引
   - 使用 `PROFILE` 分析慢查询
   - 定期清理不需要的数据

3. **生产环境**
   - 使用固定版本标签而非 `latest`
   - 配置日志轮转
   - 设置资源限制（CPU、内存）
   - 配置监控和告警
   - 考虑使用 Neo4j 集群
   - 定期进行数据备份

4. **数据导入**
   - 大文件导入时使用 `USING PERIODIC COMMIT`
   - 导入前创建必要的索引和约束
   - 使用 `LOAD CSV` 时注意文件编码

---

**最后更新**: 2025-12-10
