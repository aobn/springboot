package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.CloudflareZoneResponse;
import com.example.demo.dto.SimpleCloudflareZoneResponse;
import com.example.demo.entity.CloudflareZone;
import com.example.demo.service.CloudflareService;
import com.example.demo.service.CloudflareZoneService;
import com.example.demo.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Cloudflare DNS控制器
 * 提供Cloudflare DNS相关API接口
 * 
 * @author CodeBuddy
 * @since 2025-09-25
 */
@RestController
@RequestMapping("/api/cloudflare")
@Slf4j
public class CloudflareController {
    
    @Autowired
    private CloudflareService cloudflareService;
    
    @Autowired
    private CloudflareZoneService cloudflareZoneService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取所有Cloudflare域名列表（公开接口，无需认证）
     * 
     * @return 域名列表响应
     */
    @GetMapping("/zones")
    public ApiResponse<List<SimpleCloudflareZoneResponse>> getAllZones() {
        try {
            log.info("=== 开始获取Cloudflare所有域名列表（公开接口） ===");
            
            // 从数据库获取域名列表（包含新增字段）
            log.info("从数据库获取Cloudflare域名列表");
            List<SimpleCloudflareZoneResponse> zoneList;
            try {
                zoneList = cloudflareZoneService.getAllSimpleZones();
            } catch (Exception dbException) {
                log.error("从数据库获取域名列表失败", dbException);
                return ApiResponse.error(500, "从数据库获取域名列表失败: " + dbException.getMessage());
            }
            
            // 处理响应结果
            log.info("处理域名列表数据");
            if (zoneList == null) {
                log.error("数据库返回空结果");
                return ApiResponse.error(500, "数据库返回空结果");
            }
            
            if (zoneList.isEmpty()) {
                log.warn("未找到任何Cloudflare域名");
                return ApiResponse.success("未找到任何Cloudflare域名", zoneList);
            }
            
            log.info("成功获取Cloudflare域名列表，共 {} 个域名", zoneList.size());
            
            for (SimpleCloudflareZoneResponse zone : zoneList) {
                log.debug("域名详情: name={}, zone_id={}, status={}, user_count={}/{}, dns_record_count={}/{}", 
                        zone.getName(), zone.getZoneId(), zone.getStatus(), 
                        zone.getUserCount(), zone.getUserLimit(),
                        zone.getDnsRecordCount(), zone.getDnsRecordLimit());
            }
            
            log.info("=== Cloudflare域名列表获取完成 ===");
            return ApiResponse.success("成功获取Cloudflare域名列表", zoneList);
            
        } catch (Exception e) {
            log.error("获取Cloudflare域名列表失败", e);
            return ApiResponse.error(500, "获取域名列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据域名名称获取Zone信息
     * 
     * @param zoneName 域名名称
     * @param authHeader Authorization头信息
     * @return Zone信息
     */
    @GetMapping("/zones/{zoneName}")
    public ApiResponse<CloudflareZoneResponse.Zone> getZoneByName(
            @PathVariable String zoneName,
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("=== 开始根据域名名称获取Zone信息: {} ===", zoneName);
            
            // 步骤1：用户身份验证
            log.info("步骤1：执行用户身份验证");
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            log.info("用户身份验证成功：userId={}, email={}, role={}", userId, email, role);
            
            // 步骤2：权限验证（管理员权限）
            log.info("步骤2：执行权限验证");
            if (!"ADMIN".equals(role)) {
                log.warn("权限验证失败：用户{}不是管理员，角色为{}", userId, role);
                return ApiResponse.error(403, "需要管理员权限才能访问Cloudflare API");
            }
            log.info("权限验证通过：用户{}具有管理员权限", userId);
            
            // 步骤3：参数验证
            log.info("步骤3：执行参数验证");
            if (zoneName == null || zoneName.trim().isEmpty()) {
                log.warn("参数验证失败：域名名称为空");
                return ApiResponse.error(400, "域名名称不能为空");
            }
            log.info("参数验证通过：zoneName={}", zoneName);
            
            // 步骤4：调用Cloudflare API获取Zone信息
            log.info("步骤4：调用Cloudflare API获取Zone信息");
            CloudflareZoneResponse.Zone zone;
            try {
                zone = cloudflareService.getZoneByName(zoneName);
            } catch (Exception apiException) {
                log.error("调用Cloudflare API失败", apiException);
                return ApiResponse.error(500, "调用Cloudflare API失败: " + apiException.getMessage());
            }
            
            // 步骤5：处理响应结果
            log.info("步骤5：处理API响应结果");
            if (zone == null) {
                log.warn("未找到域名: {}", zoneName);
                return ApiResponse.error(404, "未找到指定域名: " + zoneName);
            }
            
            log.info("成功获取域名信息: name={}, id={}, status={}", 
                    zone.getName(), zone.getId(), zone.getStatus());
            
            log.info("=== Zone信息获取完成 ===");
            return ApiResponse.success("成功获取域名信息", zone);
            
        } catch (Exception e) {
            // 检查是否是JWT相关异常
            if (e instanceof io.jsonwebtoken.JwtException || 
                e instanceof io.jsonwebtoken.security.SignatureException ||
                e instanceof io.jsonwebtoken.ExpiredJwtException ||
                e instanceof io.jsonwebtoken.MalformedJwtException ||
                e instanceof io.jsonwebtoken.UnsupportedJwtException) {
                log.error("JWT认证失败", e);
                throw e; // 重新抛出让全局异常处理器处理
            }
            
            log.error("根据域名名称获取Zone信息失败", e);
            return ApiResponse.error(500, "获取域名信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试Cloudflare API连接
     * 
     * @param authHeader Authorization头信息
     * @return 连接测试结果
     */
    @GetMapping("/test")
    public ApiResponse<String> testConnection(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("=== 开始测试Cloudflare API连接 ===");
            
            // 用户身份验证
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            // 权限验证（管理员权限）
            if (!"ADMIN".equals(role)) {
                return ApiResponse.error(403, "需要管理员权限才能测试Cloudflare API");
            }
            
            // 调用API测试连接
            CloudflareZoneResponse response;
            try {
                response = cloudflareService.getAllZones();
            } catch (Exception apiException) {
                log.error("Cloudflare API连接测试失败", apiException);
                return ApiResponse.error(500, "API连接测试失败: " + apiException.getMessage());
            }
            
            if (response != null && response.isSuccess()) {
                int zoneCount = response.getResult() != null ? response.getResult().size() : 0;
                String message = String.format("Cloudflare API连接成功，共获取到 %d 个域名", zoneCount);
                log.info(message);
                return ApiResponse.success(message);
            } else {
                String errorMsg = "Cloudflare API连接失败";
                if (response != null && response.getErrors() != null && !response.getErrors().isEmpty()) {
                    errorMsg += ": " + response.getErrors().get(0).getMessage();
                }
                log.error(errorMsg);
                return ApiResponse.error(500, errorMsg);
            }
            
        } catch (Exception e) {
            log.error("测试Cloudflare API连接失败", e);
            return ApiResponse.error(500, "API连接测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 同步Cloudflare域名数据到本地数据库
     * 
     * @param authHeader Authorization头信息
     * @return 同步结果
     */
    @PostMapping("/sync-zones")
    public ApiResponse<String> syncZones(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("=== 开始同步Cloudflare域名数据到本地数据库 ===");
            
            // 用户身份验证
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            // 权限验证（管理员权限）
            if (!"ADMIN".equals(role)) {
                return ApiResponse.error(403, "需要管理员权限才能同步域名数据");
            }
            
            // 执行同步操作
            int syncedCount = cloudflareZoneService.syncZonesFromCloudflare();
            
            String message = String.format("成功同步 %d 个Cloudflare域名到本地数据库", syncedCount);
            log.info(message);
            return ApiResponse.success(message);
            
        } catch (Exception e) {
            log.error("同步Cloudflare域名数据失败", e);
            return ApiResponse.error(500, "同步域名数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取本地存储的Cloudflare域名列表
     * 
     * @param authHeader Authorization头信息
     * @return 本地域名列表
     */
    @GetMapping("/local-zones")
    public ApiResponse<List<CloudflareZone>> getLocalZones(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("=== 开始获取本地存储的Cloudflare域名列表 ===");
            
            // 用户身份验证
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            // 权限验证（管理员权限）
            if (!"ADMIN".equals(role)) {
                return ApiResponse.error(403, "需要管理员权限才能查看域名数据");
            }
            
            // 获取本地域名列表
            List<CloudflareZone> zones = cloudflareZoneService.getAllLocalZones();
            
            String message = String.format("成功获取 %d 个本地存储的域名", zones.size());
            log.info(message);
            return ApiResponse.success(message, zones);
            
        } catch (Exception e) {
            log.error("获取本地域名列表失败", e);
            return ApiResponse.error(500, "获取本地域名列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据域名名称获取本地存储的域名信息
     * 
     * @param zoneName 域名名称
     * @param authHeader Authorization头信息
     * @return 本地域名信息
     */
    @GetMapping("/local-zones/{zoneName}")
    public ApiResponse<CloudflareZone> getLocalZoneByName(
            @PathVariable String zoneName,
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("=== 开始获取本地存储的域名信息: {} ===", zoneName);
            
            // 用户身份验证
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            // 权限验证（管理员权限）
            if (!"ADMIN".equals(role)) {
                return ApiResponse.error(403, "需要管理员权限才能查看域名数据");
            }
            
            // 参数验证
            if (zoneName == null || zoneName.trim().isEmpty()) {
                return ApiResponse.error(400, "域名名称不能为空");
            }
            
            // 获取本地域名信息
            CloudflareZone zone = cloudflareZoneService.getLocalZoneByName(zoneName);
            
            if (zone == null) {
                return ApiResponse.error(404, "未找到指定域名: " + zoneName);
            }
            
            log.info("成功获取本地域名信息: name={}, id={}", zone.getName(), zone.getId());
            return ApiResponse.success("成功获取本地域名信息", zone);
            
        } catch (Exception e) {
            log.error("获取本地域名信息失败", e);
            return ApiResponse.error(500, "获取本地域名信息失败: " + e.getMessage());
        }
    }
}