# Memos 日记记录服务

Memos 是一个开源的、自托管的备忘录和日记记录应用。

## 快速开始

### 启动服务

```bash
docker-compose up -d
```

### 停止服务

```bash
docker-compose down
```

### 查看日志

```bash
docker-compose logs -f
```

## 访问地址

启动后，在浏览器中访问：

```
http://localhost:5230
```

## 配置说明

- **端口**: 5230（默认端口）
- **数据存储**: 数据存储在 `~/.memos` 目录中
- **时区**: Asia/Shanghai

## 数据持久化

所有数据存储在宿主机的 `~/.memos` 目录中（即 `${HOME}/.memos`），即使删除容器，数据也不会丢失。

数据目录位置：
- macOS/Linux: `~/.memos` 或 `/Users/你的用户名/.memos`

## 首次使用

1. 访问 `http://localhost:5230`
2. 首次访问会提示创建管理员账户
3. 按照提示完成初始设置

## 更多信息

- [Memos 官方文档](https://usememos.com/)
- [GitHub 仓库](https://github.com/usememos/memos)

