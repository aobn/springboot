package com.example.demo.service.impl;

import com.example.demo.dto.CloudflareZoneResponse;
import com.example.demo.service.CloudflareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Cloudflare DNS服务实现类
 * 
 * @author CodeBuddy
 * @since 2025-09-25
 */
@Service
@Slf4j
public class CloudflareServiceImpl implements CloudflareService {
    
    /**
     * Cloudflare API基础URL
     */
    private static final String CLOUDFLARE_API_BASE_URL = "https://api.cloudflare.com/client/v4";
    
    /**
     * Cloudflare API Key
     */
    @Value("${cloudflare.api.key:2ece4a2665a0219c10dd7174638d4133a4f77}")
    private String apiKey;
    
    /**
     * Cloudflare API Email
     */
    @Value("${cloudflare.api.email:xianhuawork@163.com}")
    private String apiEmail;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public CloudflareServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public CloudflareZoneResponse getAllZones() throws Exception {
        log.info("开始调用Cloudflare API获取所有域名列表");
        
        try {
            // 构建请求URL
            String url = CLOUDFLARE_API_BASE_URL + "/zones";
            
            // 构建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-Auth-Email", apiEmail)
                    .header("X-Auth-Key", apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            log.info("发送Cloudflare API请求: {}", url);
            log.debug("请求头: X-Auth-Email={}, X-Auth-Key={}***", apiEmail, 
                    apiKey.length() > 10 ? apiKey.substring(0, 10) : "***");
            
            // 发送请求
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            log.info("Cloudflare API响应状态码: {}", response.statusCode());
            log.debug("Cloudflare API响应内容: {}", response.body());
            
            // 检查HTTP状态码
            if (response.statusCode() != 200) {
                String errorMsg = String.format("Cloudflare API调用失败，状态码: %d, 响应: %s", 
                        response.statusCode(), response.body());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            // 解析响应JSON
            CloudflareZoneResponse zoneResponse = objectMapper.readValue(
                    response.body(), CloudflareZoneResponse.class);
            
            // 检查API调用是否成功
            if (!zoneResponse.isSuccess()) {
                String errorMsg = "Cloudflare API返回失败状态";
                if (zoneResponse.getErrors() != null && !zoneResponse.getErrors().isEmpty()) {
                    errorMsg += ": " + zoneResponse.getErrors().get(0).getMessage();
                }
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            // 记录成功信息
            int zoneCount = zoneResponse.getResult() != null ? zoneResponse.getResult().size() : 0;
            log.info("成功获取Cloudflare域名列表，共 {} 个域名", zoneCount);
            
            if (zoneResponse.getResult() != null) {
                for (CloudflareZoneResponse.Zone zone : zoneResponse.getResult()) {
                    log.debug("域名: {} (ID: {}, 状态: {})", zone.getName(), zone.getId(), zone.getStatus());
                }
            }
            
            return zoneResponse;
            
        } catch (IOException e) {
            String errorMsg = "Cloudflare API网络请求失败: " + e.getMessage();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (InterruptedException e) {
            String errorMsg = "Cloudflare API请求被中断: " + e.getMessage();
            log.error(errorMsg, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Cloudflare API调用异常: " + e.getMessage();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    @Override
    public CloudflareZoneResponse.Zone getZoneByName(String zoneName) throws Exception {
        log.info("开始根据域名名称获取Zone信息: {}", zoneName);
        
        if (zoneName == null || zoneName.trim().isEmpty()) {
            throw new IllegalArgumentException("域名名称不能为空");
        }
        
        try {
            // 获取所有域名
            CloudflareZoneResponse response = getAllZones();
            
            if (response.getResult() == null) {
                log.warn("未找到任何域名");
                return null;
            }
            
            // 查找指定域名
            for (CloudflareZoneResponse.Zone zone : response.getResult()) {
                if (zoneName.equalsIgnoreCase(zone.getName())) {
                    log.info("找到匹配的域名: {} (ID: {})", zone.getName(), zone.getId());
                    return zone;
                }
            }
            
            log.warn("未找到域名: {}", zoneName);
            return null;
            
        } catch (Exception e) {
            String errorMsg = "根据域名名称获取Zone信息失败: " + e.getMessage();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
}