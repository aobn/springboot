package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.CloudflareZoneResponse;
import com.example.demo.entity.CloudflareZone;
import com.example.demo.service.CloudflareService;
import com.example.demo.service.CloudflareZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 测试控制器
 * 用于调试Cloudflare域名同步问题
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    
    private final CloudflareService cloudflareService;
    private final CloudflareZoneService cloudflareZoneService;
    
    /**
     * 测试Cloudflare API数据获取和转换
     */
    @GetMapping("/cloudflare-data")
    public ApiResponse<String> testCloudflareData() {
        try {
            log.info("=== 开始测试Cloudflare数据获取和转换 ===");
            
            // 1. 获取API数据
            CloudflareZoneResponse response = cloudflareService.getAllZones();
            if (response == null || !response.isSuccess() || response.getResult() == null) {
                return ApiResponse.error(500, "获取Cloudflare数据失败");
            }
            
            log.info("获取到 {} 个域名", response.getResult().size());
            
            // 2. 测试第一个域名的转换
            if (!response.getResult().isEmpty()) {
                CloudflareZoneResponse.Zone firstZone = response.getResult().get(0);
                log.info("第一个域名原始数据:");
                log.info("  - ID: {}", firstZone.getId());
                log.info("  - Name: {}", firstZone.getName());
                log.info("  - Status: {}", firstZone.getStatus());
                log.info("  - Type: {}", firstZone.getType());
                log.info("  - CreatedOn: {}", firstZone.getCreatedOn());
                log.info("  - ModifiedOn: {}", firstZone.getModifiedOn());
                log.info("  - ActivatedOn: {}", firstZone.getActivatedOn());
                log.info("  - Account: {}", firstZone.getAccount());
                log.info("  - Plan: {}", firstZone.getPlan());
                
                // 3. 转换为实体
                CloudflareZone entity = cloudflareZoneService.convertToEntity(firstZone);
                log.info("转换后的实体数据:");
                log.info("  - ZoneId: {}", entity.getZoneId());
                log.info("  - Name: {}", entity.getName());
                log.info("  - Status: {}", entity.getStatus());
                log.info("  - CreatedOn: {}", entity.getCreatedOn());
                log.info("  - ModifiedOn: {}", entity.getModifiedOn());
                log.info("  - AccountId: {}", entity.getAccountId());
                log.info("  - PlanId: {}", entity.getPlanId());
                
                return ApiResponse.success("测试完成，请查看日志");
            } else {
                return ApiResponse.error(404, "没有找到域名数据");
            }
            
        } catch (Exception e) {
            log.error("测试失败", e);
            return ApiResponse.error(500, "测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试单个域名保存
     */
    @PostMapping("/save-single-zone")
    public ApiResponse<String> testSaveSingleZone() {
        try {
            log.info("=== 开始测试单个域名保存 ===");
            
            // 创建一个测试域名实体
            CloudflareZone testZone = new CloudflareZone();
            LocalDateTime now = LocalDateTime.now();
            
            testZone.setZoneId("test-zone-id-" + System.currentTimeMillis());
            testZone.setName("test-domain.com");
            testZone.setStatus("active");
            testZone.setType("full");
            testZone.setPaused(false);
            testZone.setDevelopmentMode(0);
            
            // 必填的时间字段
            testZone.setCreatedOn(now);
            testZone.setModifiedOn(now);
            
            // 必填的账户和计划字段
            testZone.setAccountId("test-account-id");
            testZone.setAccountName("Test Account");
            testZone.setPlanId("free");
            testZone.setPlanName("Free Plan");
            testZone.setPlanPrice(java.math.BigDecimal.ZERO);
            testZone.setPlanCurrency("USD");
            testZone.setPlanFrequency("");
            testZone.setPlanIsSubscribed(false);
            testZone.setPlanCanSubscribe(false);
            testZone.setPlanLegacyDiscount(false);
            testZone.setPlanExternallyManaged(false);
            
            // 同步状态
            testZone.setSyncStatus("SUCCESS");
            testZone.setLastSyncTime(now);
            testZone.setIsActive(true);
            
            // 本地时间
            testZone.setCreateTime(now);
            testZone.setUpdateTime(now);
            
            log.info("测试域名实体创建完成，开始保存...");
            
            boolean result = cloudflareZoneService.saveOrUpdateZone(testZone);
            
            if (result) {
                return ApiResponse.success("测试域名保存成功，ID: " + testZone.getId());
            } else {
                return ApiResponse.error(500, "测试域名保存失败");
            }
            
        } catch (Exception e) {
            log.error("测试保存失败", e);
            return ApiResponse.error(500, "测试保存失败: " + e.getMessage());
        }
    }
}