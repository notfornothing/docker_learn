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

### .env 文件示例

`.env` 文件示例（实际配置请编辑 `.env` 文件）：

```bash
# 时区设置
TZ=Asia/Shanghai

# 访问密码
CODE=my-secure-password

# 火山引擎配置
BYTEDANCE_API_KEY=bd2d5cad-4f4c-40c3-9473-ea97709316fe
BYTEDANCE_URL=https://ark.cn-beijing.volces.com/api/v3/chat/completions

# 自定义模型
CUSTOM_MODELS=-all,+DeepSeekV3-2@ByteDance=deepseek-v3-2-251201
CUSTOM_MODELS=-all,+DeepSeekV3-2@ByteDance=deepseek-v3-2-251201, +字节的_在线推理_好像是自动给你选@ByteDance=ep-20260108164623-gkxw5,doubao-seedream-4-5-251128@ByteDance@doubao-seedream-4-5-251128
```

### 使用 OpenAI

```bash
# .env 文件
OPENAI_API_KEY=sk-your-actual-api-key
CODE=my-secure-password
BASE_URL=https://api.openai.com
```

### 使用 DeepSeek 官方 API

```bash
# .env 文件
DEEPSEEK_API_KEY=sk-your-deepseek-api-key
DEEPSEEK_URL=https://api.deepseek.com
CODE=my-secure-password
```

## 安全建议

⚠️ **重要**: 

1. **`.env` 文件已加入 `.gitignore`**：不会提交到 Git 仓库，可以安全地存储敏感信息
2. **使用 `.env.example` 作为模板**：提交 `.env.example` 作为配置参考，不包含真实密钥
3. **修改默认配置**: 启动前必须编辑 `.env` 文件，填写真实的 API 密钥
4. **使用强密码**: `CODE` 应该使用强密码，防止未授权访问
5. **不要提交 `.env` 文件**：确保 `.env` 文件在 `.gitignore` 中

## 更新镜像

```bash
docker-compose pull
docker-compose up -d
```

## 更多信息

- [NextChat GitHub 仓库](https://github.com/ChatGPTNextWeb/ChatGPT-Next-Web)
- [Docker Hub 镜像](https://hub.docker.com/r/yidadaa/chatgpt-next-web)

