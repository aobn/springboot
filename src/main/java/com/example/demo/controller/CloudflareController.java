package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.CloudflareZoneResponse;
import com.example.demo.service.CloudflareService;
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
    private JwtUtil jwtUtil;
    
    /**
     * 获取所有Cloudflare域名列表
     * 
     * @param authHeader Authorization头信息
     * @return 域名列表响应
     */
    @GetMapping("/zones")
    public ApiResponse<CloudflareZoneResponse> getAllZones(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("=== 开始获取Cloudflare所有域名列表 ===");
            
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
            
            // 步骤3：调用Cloudflare API获取域名列表
            log.info("步骤3：调用Cloudflare API获取域名列表");
            CloudflareZoneResponse response;
            try {
                response = cloudflareService.getAllZones();
            } catch (Exception apiException) {
                log.error("调用Cloudflare API失败", apiException);
                return ApiResponse.error(500, "调用Cloudflare API失败: " + apiException.getMessage());
            }
            
            // 步骤4：处理响应结果
            log.info("步骤4：处理API响应结果");
            if (response == null) {
                log.error("Cloudflare API返回空响应");
                return ApiResponse.error(500, "获取域名列表失败：API返回空响应");
            }
            
            if (!response.isSuccess()) {
                String errorMsg = "Cloudflare API调用失败";
                if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                    errorMsg += ": " + response.getErrors().get(0).getMessage();
                }
                log.error(errorMsg);
                return ApiResponse.error(500, errorMsg);
            }
            
            // 步骤5：记录成功信息并返回结果
            int zoneCount = response.getResult() != null ? response.getResult().size() : 0;
            log.info("成功获取Cloudflare域名列表，共 {} 个域名", zoneCount);
            
            if (response.getResult() != null) {
                for (CloudflareZoneResponse.Zone zone : response.getResult()) {
                    log.debug("域名详情: name={}, id={}, status={}, type={}", 
                            zone.getName(), zone.getId(), zone.getStatus(), zone.getType());
                }
            }
            
            log.info("=== Cloudflare域名列表获取完成 ===");
            return ApiResponse.success("成功获取Cloudflare域名列表", response);
            
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
}