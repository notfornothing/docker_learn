package org.example.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DockerService {

    private static final String DOCKER_DIR = "docker";

    /**
     * 获取所有 Docker 服务列表
     */
    public List<Map<String, Object>> getAllServices() {
        List<Map<String, Object>> services = new ArrayList<>();
        
        try {
            Path dockerPath = Paths.get(DOCKER_DIR);
            if (!Files.exists(dockerPath)) {
                return services;
            }

            try (Stream<Path> paths = Files.list(dockerPath)) {
                List<Path> serviceDirs = paths
                    .filter(Files::isDirectory)
                    .filter(path -> {
                        String dirName = path.getFileName().toString();
                        // 排除特殊目录
                        return !dirName.equals("data") && 
                               !dirName.startsWith(".") &&
                               Files.exists(path.resolve("docker-compose.yml"));
                    })
                    .sorted()
                    .collect(Collectors.toList());

                for (Path serviceDir : serviceDirs) {
                    Map<String, Object> serviceInfo = getServiceInfo(serviceDir);
                    if (serviceInfo != null) {
                        services.add(serviceInfo);
                    }
                }
            }
        } catch (IOException e) {
            // 忽略错误，返回空列表
        }

        return services;
    }

    /**
     * 获取指定服务的信息
     */
    public Map<String, Object> getServiceInfo(String serviceName) {
        Path servicePath = Paths.get(DOCKER_DIR, serviceName);
        if (!Files.exists(servicePath) || !Files.isDirectory(servicePath)) {
            return null;
        }
        return getServiceInfo(servicePath);
    }

    /**
     * 获取服务信息
     */
    private Map<String, Object> getServiceInfo(Path serviceDir) {
        Map<String, Object> info = new HashMap<>();
        String serviceName = serviceDir.getFileName().toString();
        
        info.put("name", serviceName);
        info.put("path", serviceDir.toString());
        info.put("composeFile", serviceDir.resolve("docker-compose.yml").toString());
        
        // 读取 README.md
        Path readmePath = serviceDir.resolve("README.md");
        if (Files.exists(readmePath)) {
            try {
                String readmeContent = Files.readString(readmePath);
                // 提取标题（第一行 # 标题）
                String title = extractTitle(readmeContent);
                if (title != null) {
                    info.put("title", title);
                }
                // 提取描述（第一个段落）
                String description = extractDescription(readmeContent);
                if (description != null) {
                    info.put("description", description);
                }
                info.put("readme", readmeContent);
            } catch (IOException e) {
                // 忽略读取错误
            }
        }
        
        // 检查是否有 docker-compose.yml
        Path composePath = serviceDir.resolve("docker-compose.yml");
        info.put("hasCompose", Files.exists(composePath));
        
        return info;
    }

    /**
     * 提取 README 标题
     */
    private String extractTitle(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("# ")) {
                return line.substring(2).trim();
            }
        }
        return null;
    }

    /**
     * 提取 README 描述（第一个段落）
     */
    private String extractDescription(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        String[] lines = content.split("\n");
        StringBuilder description = new StringBuilder();
        boolean inDescription = false;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                if (inDescription && description.length() > 0) {
                    break;
                }
                continue;
            }
            if (line.startsWith("#")) {
                if (inDescription) {
                    break;
                }
                continue;
            }
            if (!line.startsWith("```") && !line.startsWith("|") && !line.startsWith("-")) {
                inDescription = true;
                if (description.length() > 0) {
                    description.append(" ");
                }
                description.append(line);
                if (description.length() > 200) {
                    break;
                }
            }
        }
        
        String desc = description.toString().trim();
        return desc.isEmpty() ? null : desc;
    }

    /**
     * 获取服务的 README 内容
     */
    public String getServiceReadme(String serviceName) {
        Path readmePath = Paths.get(DOCKER_DIR, serviceName, "README.md");
        if (!Files.exists(readmePath)) {
            return null;
        }
        try {
            return Files.readString(readmePath);
        } catch (IOException e) {
            return null;
        }
    }
}





