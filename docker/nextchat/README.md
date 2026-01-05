# NextChat 服务

NextChat（ChatGPT Next Web）是一个开源的 ChatGPT 界面应用，提供美观的聊天界面。

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
http://localhost:3000
```

首次访问需要输入 `CODE` 环境变量中设置的访问密码。

## 配置说明

### 必需环境变量

- **OPENAI_API_KEY**: 你的 OpenAI API 密钥（格式：`sk-...`）
- **CODE**: 访问密码，用于保护应用不被未授权访问

### 可选环境变量

- **BASE_URL**: OpenAI API 代理地址（可选）
  - 如果使用代理，取消注释并填写代理地址
  - 例如：`https://api.openai.com` 或你的代理地址
- **TZ**: 时区设置（默认：Asia/Shanghai）

### 端口配置

- **默认端口**: 3000
- 如需修改端口，更改 `ports` 配置，例如：`"8080:3000"` 则通过 `http://localhost:8080` 访问

## 配置示例

### 基本配置

```yaml
environment:
  - OPENAI_API_KEY=sk-your-actual-api-key
  - CODE=my-secure-password
```

### 使用代理

```yaml
environment:
  - OPENAI_API_KEY=sk-your-actual-api-key
  - CODE=my-secure-password
  - BASE_URL=https://your-proxy-url.com
```

## 安全建议

⚠️ **重要**: 

1. **修改默认配置**: 启动前必须修改 `OPENAI_API_KEY` 和 `CODE`
2. **保护 API 密钥**: 不要将包含真实 API 密钥的配置文件提交到公共仓库
3. **使用强密码**: `CODE` 应该使用强密码，防止未授权访问

## 更新镜像

```bash
docker-compose pull
docker-compose up -d
```

## 更多信息

- [NextChat GitHub 仓库](https://github.com/ChatGPTNextWeb/ChatGPT-Next-Web)
- [Docker Hub 镜像](https://hub.docker.com/r/yidadaa/chatgpt-next-web)

