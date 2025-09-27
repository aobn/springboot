package com.example.demo.service.impl;

import com.example.demo.dto.CloudflareZoneResponse;
import com.example.demo.dto.CloudflareDnsRecordResponse;
import com.example.demo.dto.CreateDnsRecordRequest;
import com.example.demo.dto.CreateDnsRecordResponse;
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
import java.util.HashMap;
import java.util.Map;

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
    
    @Override
    public CloudflareDnsRecordResponse getDnsRecords(String zoneId) throws Exception {
        log.info("开始调用Cloudflare API获取Zone {} 的DNS记录", zoneId);
        
        if (zoneId == null || zoneId.trim().isEmpty()) {
            throw new IllegalArgumentException("Zone ID不能为空");
        }
        
        try {
            // 构建请求URL
            String url = CLOUDFLARE_API_BASE_URL + "/zones/" + zoneId + "/dns_records";
            
            // 构建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-Auth-Email", apiEmail)
                    .header("X-Auth-Key", apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            log.info("发送Cloudflare DNS记录API请求: {}", url);
            
            // 发送请求
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            log.info("Cloudflare DNS记录API响应状态码: {}", response.statusCode());
            log.debug("Cloudflare DNS记录API响应内容: {}", response.body());
            
            // 检查HTTP状态码
            if (response.statusCode() != 200) {
                String errorMsg = String.format("Cloudflare DNS记录API调用失败，状态码: %d, 响应: %s", 
                        response.statusCode(), response.body());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            // 解析响应JSON
            CloudflareDnsRecordResponse recordResponse = objectMapper.readValue(
                    response.body(), CloudflareDnsRecordResponse.class);
            
            // 检查API调用是否成功
            if (!recordResponse.isSuccess()) {
                String errorMsg = "Cloudflare DNS记录API返回失败状态";
                if (recordResponse.getErrors() != null && !recordResponse.getErrors().isEmpty()) {
                    errorMsg += ": " + recordResponse.getErrors().toString();
                }
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            // 记录成功信息
            int recordCount = recordResponse.getResult() != null ? recordResponse.getResult().size() : 0;
            log.info("成功获取Zone {} 的DNS记录，共 {} 条记录", zoneId, recordCount);
            
            if (recordResponse.getResult() != null) {
                for (CloudflareDnsRecordResponse.DnsRecord record : recordResponse.getResult()) {
                    log.debug("DNS记录: {} {} {} (ID: {})", record.getName(), record.getType(), 
                            record.getContent(), record.getId());
                }
            }
            
            return recordResponse;
            
        } catch (IOException e) {
            String errorMsg = "Cloudflare DNS记录API网络请求失败: " + e.getMessage();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (InterruptedException e) {
            String errorMsg = "Cloudflare DNS记录API请求被中断: " + e.getMessage();
            log.error(errorMsg, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Cloudflare DNS记录API调用异常: " + e.getMessage();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    @Override
    public CreateDnsRecordResponse createDnsRecord(String zoneId, CreateDnsRecordRequest request) throws Exception {
        log.info("开始调用Cloudflare API创建DNS记录: zoneId={}, name={}, type={}", 
                zoneId, request.getNamePrefix(), request.getType());
        
        if (zoneId == null || zoneId.trim().isEmpty()) {
            throw new IllegalArgumentException("Zone ID不能为空");
        }
        
        if (request == null) {
            throw new IllegalArgumentException("创建DNS记录请求不能为空");
        }
        
        try {
            // 首先获取Zone信息以获取域名
            String zoneName = getZoneNameById(zoneId);
            if (zoneName == null) {
                throw new RuntimeException("未找到Zone ID: " + zoneId);
            }
            
            // 构建请求体
            Map<String, Object> requestBody = buildCreateDnsRecordRequestBody(request, zoneName);
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            
            log.info("DNS记录创建请求体: {}", requestBodyJson);
            
            // 构建请求URL
            String url = CLOUDFLARE_API_BASE_URL + "/zones/" + zoneId + "/dns_records";
            
            // 构建HTTP请求
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-Auth-Email", apiEmail)
                    .header("X-Auth-Key", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            log.info("发送Cloudflare创建DNS记录API请求: {}", url);
            
            // 发送请求
            HttpResponse<String> response = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofString());
            
            log.info("Cloudflare创建DNS记录API响应状态码: {}", response.statusCode());
            log.debug("Cloudflare创建DNS记录API响应内容: {}", response.body());
            
            // 解析响应JSON
            CreateDnsRecordResponse createResponse = objectMapper.readValue(
                    response.body(), CreateDnsRecordResponse.class);
            
            // 检查API调用是否成功
            if (!createResponse.isSuccess()) {
                String errorMsg = "Cloudflare创建DNS记录API返回失败状态";
                if (createResponse.getErrors() != null && !createResponse.getErrors().isEmpty()) {
                    errorMsg += ": " + createResponse.getErrors().get(0).getMessage();
                }
                log.error(errorMsg);
                log.error("完整错误信息: {}", createResponse.getErrors());
                throw new RuntimeException(errorMsg);
            }
            
            // 记录成功信息
            if (createResponse.getResult() != null) {
                CreateDnsRecordResponse.DnsRecordResult result = createResponse.getResult();
                log.info("成功创建DNS记录: id={}, name={}, type={}, content={}", 
                        result.getId(), result.getName(), result.getType(), result.getContent());
            }
            
            return createResponse;
            
        } catch (IOException e) {
            String errorMsg = "Cloudflare创建DNS记录API网络请求失败: " + e.getMessage();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (InterruptedException e) {
            String errorMsg = "Cloudflare创建DNS记录API请求被中断: " + e.getMessage();
            log.error(errorMsg, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Cloudflare创建DNS记录API调用异常: " + e.getMessage();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    /**
     * 根据Zone ID获取域名名称
     */
    private String getZoneNameById(String zoneId) throws Exception {
        CloudflareZoneResponse response = getAllZones();
        if (response.getResult() != null) {
            for (CloudflareZoneResponse.Zone zone : response.getResult()) {
                if (zoneId.equals(zone.getId())) {
                    return zone.getName();
                }
            }
        }
        return null;
    }
    
    /**
     * 构建创建DNS记录的请求体
     */
    private Map<String, Object> buildCreateDnsRecordRequestBody(CreateDnsRecordRequest request, String zoneName) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // 基本字段
        requestBody.put("name", request.getFullName(zoneName));
        requestBody.put("type", request.getType().toUpperCase());
        requestBody.put("content", request.getContent());
        requestBody.put("ttl", request.getTtl());
        
        // 代理设置（仅对A、AAAA、CNAME记录有效）
        String type = request.getType().toUpperCase();
        if ("A".equals(type) || "AAAA".equals(type) || "CNAME".equals(type)) {
            requestBody.put("proxied", request.getProxied() != null ? request.getProxied() : false);
        }
        
        // MX记录优先级
        if ("MX".equals(type) && request.getPriority() != null) {
            requestBody.put("priority", request.getPriority());
        }
        
        // SRV记录特殊处理
        if ("SRV".equals(type)) {
            Map<String, Object> data = new HashMap<>();
            if (request.getService() != null) data.put("service", request.getService());
            if (request.getProto() != null) data.put("proto", request.getProto());
            if (request.getTarget() != null) data.put("target", request.getTarget());
            if (request.getPriority() != null) data.put("priority", request.getPriority());
            if (request.getWeight() != null) data.put("weight", request.getWeight());
            if (request.getPort() != null) data.put("port", request.getPort());
            
            if (!data.isEmpty()) {
                requestBody.put("data", data);
            }
        }
        
        // 注释
        if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
            requestBody.put("comment", request.getComment());
        }
        
        return requestBody;
    }
    
    @Override
    public boolean testConnection() {
        try {
            log.info("=== 测试Cloudflare API连接 ===");
            CloudflareZoneResponse response = getAllZones();
            boolean success = response != null && response.isSuccess();
            log.info("Cloudflare API连接测试结果: {}", success ? "成功" : "失败");
            return success;
        } catch (Exception e) {
            log.error("Cloudflare API连接测试失败", e);
            return false;
        }
    }
}