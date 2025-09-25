package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.CreateDnsRecordRequest;
import com.example.demo.entity.CloudflareDnsRecord;
import com.example.demo.service.CloudflareDnsRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 文件名：TestController.java
 * 功能：测试控制器，用于快速测试API功能
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {
    
    private final CloudflareDnsRecordService cloudflareDnsRecordService;
    
    /**
     * 测试创建DNS记录（无需认证）
     * 
     * @param zoneId 域名Zone ID
     * @param request 创建请求参数
     * @return 创建结果
     */
    @PostMapping("/dns/create/{zoneId}")
    public ApiResponse<CloudflareDnsRecord> testCreateDnsRecord(
            @PathVariable String zoneId,
            @Valid @RequestBody CreateDnsRecordRequest request) {
        
        log.info("测试创建DNS记录: zoneId={}, type={}, name={}", 
                zoneId, request.getType(), request.getNamePrefix());
        
        try {
            CloudflareDnsRecord record = cloudflareDnsRecordService.createDnsRecord(zoneId, request);
            return ApiResponse.success("DNS记录创建成功", record);
        } catch (Exception e) {
            log.error("创建DNS记录失败", e);
            return ApiResponse.error(500, "创建DNS记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试获取域名列表（无需认证）
     * 
     * @return 域名列表
     */
    @GetMapping("/zones")
    public ApiResponse<String> testGetZones() {
        log.info("测试获取域名列表");
        return ApiResponse.success("测试接口正常工作");
    }
}