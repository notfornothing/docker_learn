package org.example.controller;

import org.example.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docker")
public class DockerController {

    @Autowired
    private DockerService dockerService;

    /**
     * 获取所有 Docker 服务列表
     */
    @GetMapping("/services")
    public ResponseEntity<List<Map<String, Object>>> getAllServices() {
        return ResponseEntity.ok(dockerService.getAllServices());
    }

    /**
     * 获取指定服务的信息
     */
    @GetMapping("/services/{serviceName}")
    public ResponseEntity<Map<String, Object>> getServiceInfo(@PathVariable String serviceName) {
        Map<String, Object> info = dockerService.getServiceInfo(serviceName);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }

    /**
     * 获取指定服务的 README
     */
    @GetMapping("/services/{serviceName}/readme")
    public ResponseEntity<Map<String, String>> getServiceReadme(@PathVariable String serviceName) {
        String readme = dockerService.getServiceReadme(serviceName);
        if (readme == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, String> result = Map.of("readme", readme);
        return ResponseEntity.ok(result);
    }
}





