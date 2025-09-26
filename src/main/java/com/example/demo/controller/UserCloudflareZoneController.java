package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.UserCloudflareZoneRegisterRequest;
import com.example.demo.dto.UserCloudflareZoneRegisterResponse;
import com.example.demo.dto.UserCloudflareZoneListResponse;
import com.example.demo.entity.UserCloudflareZone;
import com.example.demo.service.UserCloudflareZoneService;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件名：UserCloudflareZoneController.java
 * 功能：用户Cloudflare域名管理控制器
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/user/cloudflare/zones")
@RequiredArgsConstructor
public class UserCloudflareZoneController {
    
    private final UserCloudflareZoneService userCloudflareZoneService;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户注册Cloudflare域名
     * 用户注册一个可用的Cloudflare域名，系统自动分配十六进制前缀
     */
    @PostMapping("/register")
    public ApiResponse<UserCloudflareZoneRegisterResponse> registerZone(
            @Valid @RequestBody UserCloudflareZoneRegisterRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 请求注册Cloudflare域名: {}", userId, request.getZoneId());
            
            UserCloudflareZoneRegisterResponse response = userCloudflareZoneService.registerZone(userId, request);
            
            if (response.getResultCode() == 201) {
                return ApiResponse.success("域名注册成功", response);
            } else {
                return ApiResponse.error(response.getResultCode(), response.getResultMessage());
            }
            
        } catch (Exception e) {
            log.error("用户注册Cloudflare域名异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 获取用户的Cloudflare域名列表
     * 获取用户已注册的域名列表和可用域名列表
     */
    @GetMapping("/list")
    public ApiResponse<UserCloudflareZoneListResponse> getUserZoneList(HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 请求获取Cloudflare域名列表", userId);
            
            UserCloudflareZoneListResponse response = userCloudflareZoneService.getUserZoneList(userId);
            
            return ApiResponse.success("获取域名列表成功", response);
            
        } catch (Exception e) {
            log.error("获取用户Cloudflare域名列表异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 获取可用的Cloudflare域名列表
     * 获取所有可用的Cloudflare域名列表
     */
    @GetMapping("/available")
    public ApiResponse<List<UserCloudflareZoneListResponse.AvailableZoneInfo>> getAvailableZones() {
        
        try {
            log.info("请求获取可用Cloudflare域名列表");
            
            List<UserCloudflareZoneListResponse.AvailableZoneInfo> availableZones = 
                userCloudflareZoneService.getAvailableZones();
            
            return ApiResponse.success("获取可用域名列表成功", availableZones);
            
        } catch (Exception e) {
            log.error("获取可用Cloudflare域名列表异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 获取用户域名详情
     * 根据ID获取用户域名注册记录详情
     */
    @GetMapping("/{id}")
    public ApiResponse<UserCloudflareZone> getUserZoneById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 请求获取域名记录详情: {}", userId, id);
            
            UserCloudflareZone userZone = userCloudflareZoneService.getUserZoneById(userId, id);
            
            if (userZone != null) {
                return ApiResponse.success("获取域名详情成功", userZone);
            } else {
                return ApiResponse.error(404, "域名记录不存在");
            }
            
        } catch (Exception e) {
            log.error("获取用户域名详情异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 注销用户域名
     * 注销用户已注册的域名(需要先删除所有DNS记录)
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> unregisterZone(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 请求注销域名记录: {}", userId, id);
            
            boolean success = userCloudflareZoneService.unregisterZone(userId, id);
            
            if (success) {
                return ApiResponse.success("域名注销成功");
            } else {
                return ApiResponse.error(400, "域名注销失败，请确保已删除所有DNS记录");
            }
            
        } catch (Exception e) {
            log.error("注销用户域名异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 同步DNS记录数量
     * 同步用户在指定域名下的DNS记录数量
     */
    @PostMapping("/{zoneId}/sync-dns-count")
    public ApiResponse<String> syncDnsRecordCount(
            @PathVariable String zoneId,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 请求同步域名 {} 的DNS记录数量", userId, zoneId);
            
            boolean success = userCloudflareZoneService.syncDnsRecordCount(userId, zoneId);
            
            if (success) {
                return ApiResponse.success("DNS记录数量同步成功");
            } else {
                return ApiResponse.error(400, "DNS记录数量同步失败");
            }
            
        } catch (Exception e) {
            log.error("同步DNS记录数量异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 检查域名注册状态
     * 检查用户是否已注册指定域名
     */
    @GetMapping("/check/{zoneId}")
    public ApiResponse<Boolean> checkZoneRegistration(
            @PathVariable String zoneId,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 检查域名 {} 的注册状态", userId, zoneId);
            
            boolean isRegistered = userCloudflareZoneService.isUserRegisteredZone(userId, zoneId);
            
            String message = isRegistered ? "用户已注册该域名" : "用户未注册该域名";
            return ApiResponse.success(message, isRegistered);
            
        } catch (Exception e) {
            log.error("检查域名注册状态异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 获取用户域名注册统计信息
     * 获取用户的域名注册数量统计和限制信息
     */
    @GetMapping("/stats")
    public ApiResponse<UserCloudflareZoneListResponse.UserDomainStats> getUserDomainStats(
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 请求获取域名注册统计信息", userId);
            
            UserCloudflareZoneListResponse.UserDomainStats stats = 
                userCloudflareZoneService.getUserDomainStats(userId);
            
            return ApiResponse.success("获取域名统计信息成功", stats);
            
        } catch (Exception e) {
            log.error("获取用户域名统计信息异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
}