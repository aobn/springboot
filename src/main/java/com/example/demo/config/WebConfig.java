package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类 - 通过WebMvcConfigurer配置CORS和视图控制器
 * 
 * @author CodeBuddy
 * @date 2025-08-31
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域映射
     * 
     * @param registry CORS注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的源
                .allowedOriginPatterns("*")
                // 允许的方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带凭证
                .allowCredentials(true)
                // 预检请求缓存时间
                .maxAge(3600);
    }

    /**
     * 配置视图控制器 - 处理静态路径映射
     * 
     * @param registry 视图控制器注册表
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 将根路径重定向到错误处理
        registry.addViewController("/").setStatusCode(org.springframework.http.HttpStatus.NOT_FOUND);
    }
}