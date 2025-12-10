package org.example.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String hostName = InetAddress.getLocalHost().getHostName();
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🚀 应用启动成功！");
            System.out.println("=".repeat(60));
            System.out.println("📍 访问地址:");
            System.out.println("   本地:   http://localhost:" + port + contextPath);
            System.out.println("   网络:   http://" + hostAddress + ":" + port + contextPath);
            System.out.println("   主机名: http://" + hostName + ":" + port + contextPath);
            System.out.println("\n📚 API 文档:");
            System.out.println("   http://localhost:" + port + contextPath + "/");
            System.out.println("\n🔗 Nginx Proxy Manager:");
            System.out.println("   http://localhost:81");
            System.out.println("=".repeat(60) + "\n");
            
        } catch (UnknownHostException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🚀 应用启动成功！");
            System.out.println("=".repeat(60));
            System.out.println("📍 访问地址: http://localhost:" + port + contextPath);
            System.out.println("📚 API 文档: http://localhost:" + port + contextPath + "/");
            System.out.println("🔗 Nginx Proxy Manager: http://localhost:81");
            System.out.println("=".repeat(60) + "\n");
        }
    }
}

