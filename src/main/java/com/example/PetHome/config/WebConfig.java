package com.example.PetHome.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 【关键修复】确保路径格式为 file:///C:/...
        // 你的 uploadDir 是 "C:/Users/hyb/Desktop/毕设/image/"
        // 拼接后应该是 "file:///C:/Users/hyb/Desktop/毕设/image/"

        String path = "file:///" + uploadDir;

        // 防止 uploadDir 没带斜杠的容错处理（可选）
        // if (!uploadDir.endsWith("/")) { path += "/"; }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(path);
    }

    // 全局 CORS 配置 (防止跨域问题)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }
}