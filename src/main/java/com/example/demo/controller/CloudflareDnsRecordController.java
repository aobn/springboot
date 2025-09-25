package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.CreateDnsRecordRequest;
import com.example.demo.entity.CloudflareDnsRecord;
import com.example.demo.service.CloudflareDnsRecordService;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 文件名：CloudflareDnsRecordController.java
 * 功能：Cloudflare DNS记录管理控制器
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/cloudflare/dns")
@RequiredArgsConstructor
@Validated
public class CloudflareDnsRecordController {
    
    private final CloudflareDnsRecordService cloudflareRecordService;
    private final JwtUtil jwtUtil;
    
    /**
     * 创建DNS记录到Cloudflare
     */
    @PostMapping("/create/{zoneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CloudflareDnsRecord> createDnsRecord(
            @PathVariable String zoneId,
            @Valid @RequestBody CreateDnsRecordRequest request,
            HttpServletRequest httpRequest) {
        try {
            log.info("=== 开始创建DNS记录 ===");
            log.info("Zone ID: {}, 记录名称前缀: {}, 类型: {}, 内容: {}", 
                    zoneId, request.getNamePrefix(), request.getType(), request.getContent());
            
            // 获取管理员信息
            String token = httpRequest.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long adminId = jwtUtil.getUserIdFromToken(token);
                log.info("管理员 {} 请求创建DNS记录", adminId);
            }
            
            // 创建DNS记录
            CloudflareDnsRecord createdRecord = cloudflareRecordService.createDnsRecord(zoneId, request);
            
            log.info("DNS记录创建成功: recordId={}, name={}, type={}", 
                    createdRecord.getRecordId(), createdRecord.getName(), createdRecord.getType());
            return ApiResponse.success(createdRecord);
            
        } catch (IllegalArgumentException e) {
            log.error("DNS记录创建参数错误: {}", e.getMessage());
            return ApiResponse.error(400, "参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("创建DNS记录失败: zoneId={}, namePrefix={}", zoneId, request.getNamePrefix(), e);
            return ApiResponse.error(500, "创建DNS记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 同步指定Zone的DNS记录
     */
    @PostMapping("/sync/{zoneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> syncDnsRecords(@PathVariable String zoneId, HttpServletRequest request) {
        try {
            log.info("=== 开始同步Zone {} 的DNS记录到本地数据库 ===", zoneId);
            
            // 获取管理员信息
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long adminId = jwtUtil.getUserIdFromToken(token);
                log.info("管理员 {} 请求同步Zone {} 的DNS记录", adminId, zoneId);
            }
            
            int syncCount = cloudflareRecordService.syncDnsRecordsFromCloudflare(zoneId);
            
            log.info("成功同步 {} 条DNS记录到本地数据库", syncCount);
            return ApiResponse.success("成功同步 " + syncCount + " 条DNS记录");
            
        } catch (Exception e) {
            log.error("同步Zone {} DNS记录失败", zoneId, e);
            return ApiResponse.error(500, "同步DNS记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 同步所有Zone的DNS记录
     */
    @PostMapping("/sync-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> syncAllDnsRecords(HttpServletRequest request) {
        try {
            log.info("=== 开始同步所有Zone的DNS记录到本地数据库 ===");
            
            // 获取管理员信息
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long adminId = jwtUtil.getUserIdFromToken(token);
                log.info("管理员 {} 请求同步所有Zone的DNS记录", adminId);
            }
            
            int syncCount = cloudflareRecordService.syncAllDnsRecordsFromCloudflare();
            
            log.info("成功同步 {} 条DNS记录到本地数据库", syncCount);
            return ApiResponse.success("成功同步 " + syncCount + " 条DNS记录");
            
        } catch (Exception e) {
            log.error("同步所有DNS记录失败", e);
            return ApiResponse.error(500, "同步所有DNS记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定Zone的本地DNS记录
     */
    @GetMapping("/zone/{zoneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CloudflareDnsRecord>> getDnsRecordsByZoneId(@PathVariable String zoneId) {
        try {
            log.info("获取Zone {} 的本地DNS记录", zoneId);
            
            List<CloudflareDnsRecord> records = cloudflareRecordService.getLocalDnsRecordsByZoneId(zoneId);
            
            log.info("获取到 {} 条DNS记录", records.size());
            return ApiResponse.success(records);
            
        } catch (Exception e) {
            log.error("获取Zone {} DNS记录失败", zoneId, e);
            return ApiResponse.error(500, "获取DNS记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据Zone ID和记录类型获取DNS记录
     */
    @GetMapping("/zone/{zoneId}/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CloudflareDnsRecord>> getDnsRecordsByZoneIdAndType(
            @PathVariable String zoneId, @PathVariable String type) {
        try {
            log.info("获取Zone {} 类型为 {} 的本地DNS记录", zoneId, type);
            
            List<CloudflareDnsRecord> records = cloudflareRecordService.getLocalDnsRecordsByZoneIdAndType(zoneId, type);
            
            log.info("获取到 {} 条DNS记录", records.size());
            return ApiResponse.success(records);
            
        } catch (Exception e) {
            log.error("获取Zone {} 类型为 {} 的DNS记录失败", zoneId, type, e);
            return ApiResponse.error(500, "获取DNS记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据记录ID获取DNS记录详情
     */
    @GetMapping("/record/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CloudflareDnsRecord> getDnsRecordByRecordId(@PathVariable String recordId) {
        try {
            log.info("根据记录ID获取DNS记录详情: {}", recordId);
            
            CloudflareDnsRecord record = cloudflareRecordService.getLocalDnsRecordByRecordId(recordId);
            
            if (record != null) {
                log.info("找到DNS记录: name={}, type={}", record.getName(), record.getType());
                return ApiResponse.success(record);
            } else {
                log.info("未找到记录ID: {}", recordId);
                return ApiResponse.error(404, "未找到指定的DNS记录");
            }
            
        } catch (Exception e) {
            log.error("获取DNS记录详情失败: recordId={}", recordId, e);
            return ApiResponse.error(500, "获取DNS记录详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除指定的DNS记录
     */
    @DeleteMapping("/record/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteDnsRecord(@PathVariable String recordId, HttpServletRequest request) {
        try {
            log.info("删除DNS记录: recordId={}", recordId);
            
            // 获取管理员信息
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long adminId = jwtUtil.getUserIdFromToken(token);
                log.info("管理员 {} 请求删除DNS记录: {}", adminId, recordId);
            }
            
            boolean success = cloudflareRecordService.deleteDnsRecord(recordId);
            
            if (success) {
                log.info("DNS记录删除成功: recordId={}", recordId);
                return ApiResponse.success("DNS记录删除成功");
            } else {
                log.warn("DNS记录删除失败: recordId={}", recordId);
                return ApiResponse.error(404, "未找到指定的DNS记录");
            }
            
        } catch (Exception e) {
            log.error("删除DNS记录失败: recordId={}", recordId, e);
            return ApiResponse.error(500, "删除DNS记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计指定Zone的DNS记录数量
     */
    @GetMapping("/zone/{zoneId}/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Integer> countDnsRecordsByZoneId(@PathVariable String zoneId) {
        try {
            log.info("统计Zone {} 的DNS记录数量", zoneId);
            
            int count = cloudflareRecordService.countDnsRecordsByZoneId(zoneId);
            
            log.info("Zone {} 共有 {} 条DNS记录", zoneId, count);
            return ApiResponse.success(count);
            
        } catch (Exception e) {
            log.error("统计Zone {} DNS记录数量失败", zoneId, e);
            return ApiResponse.error(500, "统计DNS记录数量失败: " + e.getMessage());
        }
    }
}