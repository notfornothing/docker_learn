package org.example.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NginxProxyManagerService {

    /**
     * 获取 Nginx Proxy Manager 的基本信息
     */
    public Map<String, Object> getNpmInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Nginx Proxy Manager");
        info.put("adminUrl", "http://localhost:81");
        info.put("httpPort", 80);
        info.put("httpsPort", 443);
        info.put("adminPort", 81);
        info.put("description", "Nginx Proxy Manager 是一个基于 Docker 的反向代理管理工具");
        info.put("dockerComposeFile", "docker-compose.yml");
        return info;
    }

    /**
     * 获取 Docker Compose 命令帮助
     */
    public Map<String, String> getDockerComposeCommands() {
        Map<String, String> commands = new HashMap<>();
        commands.put("启动服务", "docker compose up -d");
        commands.put("停止服务", "docker compose down");
        commands.put("查看日志", "docker compose logs -f nginx-proxy-manager");
        commands.put("重启服务", "docker compose restart nginx-proxy-manager");
        commands.put("查看状态", "docker compose ps");
        return commands;
    }
}

