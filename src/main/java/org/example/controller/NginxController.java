package org.example.controller;

import org.example.service.NginxProxyManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nginx")
public class NginxController {

    @Autowired
    private NginxProxyManagerService nginxService;

    /**
     * 获取 Nginx Proxy Manager 信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        return ResponseEntity.ok(nginxService.getNpmInfo());
    }

    /**
     * 获取 Docker Compose 命令
     */
    @GetMapping("/commands")
    public ResponseEntity<Map<String, String>> getCommands() {
        return ResponseEntity.ok(nginxService.getDockerComposeCommands());
    }
}
