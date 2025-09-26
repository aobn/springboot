package com.example.demo.service.impl;

import com.example.demo.entity.CloudflareZone;
import com.example.demo.mapper.AdminCloudflareZoneMapper;
import com.example.demo.service.AdminCloudflareZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员Cloudflare域名管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCloudflareZoneServiceImpl implements AdminCloudflareZoneService {

    private final AdminCloudflareZoneMapper adminCloudflareZoneMapper;

    @Override
    @Transactional
    public Map<String, Object> initPrefixPool(String zoneId) {
        log.info("开始初始化域名前缀池: zoneId={}", zoneId);
        
        // 检查域名是否存在
        CloudflareZone zone = adminCloudflareZoneMapper.findByZoneId(zoneId);
        if (zone == null) {
            throw new RuntimeException("域名不存在: " + zoneId);
        }

        // 检查是否已有前缀池
        int existingCount = adminCloudflareZoneMapper.countPrefixPool(zoneId);
        if (existingCount > 0) {
            Map<String, Object> result = new HashMap<>();
            result.put("zoneId", zoneId);
            result.put("zoneName", zone.getName());
            result.put("status", "ALREADY_EXISTS");
            result.put("message", "前缀池已存在，无需初始化");
            result.put("existingPrefixes", existingCount);
            return result;
        }

        // 调用存储过程初始化前缀池
        adminCloudflareZoneMapper.callInitPrefixPool(zoneId);
        
        // 获取初始化后的统计信息
        Map<String, Object> stats = adminCloudflareZoneMapper.getPrefixPoolStats(zoneId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("zoneId", zoneId);
        result.put("zoneName", zone.getName());
        result.put("status", "INITIALIZED");
        result.put("message", "前缀池初始化完成");
        result.put("totalPrefixes", stats.get("total_prefixes"));
        result.put("availablePrefixes", stats.get("available_prefixes"));
        result.put("assignedPrefixes", stats.get("assigned_prefixes"));
        
        log.info("域名前缀池初始化完成: zoneId={}, totalPrefixes={}", zoneId, stats.get("total_prefixes"));
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> resetPrefixPool(String zoneId) {
        log.info("开始重置域名前缀池: zoneId={}", zoneId);
        
        // 检查域名是否存在
        CloudflareZone zone = adminCloudflareZoneMapper.findByZoneId(zoneId);
        if (zone == null) {
            throw new RuntimeException("域名不存在: " + zoneId);
        }

        // 检查是否有已分配的前缀
        int assignedCount = adminCloudflareZoneMapper.countAssignedPrefixes(zoneId);
        if (assignedCount > 0) {
            throw new RuntimeException("该域名有 " + assignedCount + " 个已分配的前缀，无法重置。请先处理用户注册记录。");
        }

        // 清空前缀池
        adminCloudflareZoneMapper.clearPrefixPool(zoneId);
        
        // 重新初始化
        adminCloudflareZoneMapper.callInitPrefixPool(zoneId);
        
        // 获取重置后的统计信息
        Map<String, Object> stats = adminCloudflareZoneMapper.getPrefixPoolStats(zoneId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("zoneId", zoneId);
        result.put("zoneName", zone.getName());
        result.put("status", "RESET");
        result.put("message", "前缀池重置完成");
        result.put("totalPrefixes", stats.get("total_prefixes"));
        result.put("availablePrefixes", stats.get("available_prefixes"));
        result.put("assignedPrefixes", stats.get("assigned_prefixes"));
        
        log.info("域名前缀池重置完成: zoneId={}, totalPrefixes={}", zoneId, stats.get("total_prefixes"));
        return result;
    }

    @Override
    public Map<String, Object> getPrefixPoolStatus(String zoneId) {
        log.info("获取域名前缀池状态: zoneId={}", zoneId);
        
        // 检查域名是否存在
        CloudflareZone zone = adminCloudflareZoneMapper.findByZoneId(zoneId);
        if (zone == null) {
            throw new RuntimeException("域名不存在: " + zoneId);
        }

        // 获取前缀池统计信息
        Map<String, Object> stats = adminCloudflareZoneMapper.getPrefixPoolStats(zoneId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("zoneId", zoneId);
        result.put("zoneName", zone.getName());
        result.put("zoneStatus", zone.getStatus());
        result.put("autoAssignEnabled", zone.getAutoAssignEnabled());
        result.put("totalPrefixes", stats.get("total_prefixes"));
        result.put("availablePrefixes", stats.get("available_prefixes"));
        result.put("assignedPrefixes", stats.get("assigned_prefixes"));
        result.put("usagePercentage", stats.get("usage_percentage"));
        result.put("userCount", zone.getUserCount());
        result.put("userLimit", zone.getUserLimit());
        result.put("dnsRecordCount", zone.getDnsRecordCount());
        result.put("dnsRecordLimit", zone.getDnsRecordLimit());
        result.put("isFull", zone.getIsFull());
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> batchInitPrefixPools() {
        log.info("开始批量初始化所有域名前缀池");
        
        // 获取所有启用自动分配的域名
        List<CloudflareZone> zones = adminCloudflareZoneMapper.findAutoAssignEnabledZones();
        
        int totalZones = zones.size();
        int initializedCount = 0;
        int skippedCount = 0;
        
        for (CloudflareZone zone : zones) {
            try {
                // 检查是否已有前缀池
                int existingCount = adminCloudflareZoneMapper.countPrefixPool(zone.getZoneId());
                if (existingCount == 0) {
                    // 初始化前缀池
                    adminCloudflareZoneMapper.callInitPrefixPool(zone.getZoneId());
                    initializedCount++;
                    log.info("域名前缀池初始化完成: zoneId={}, zoneName={}", zone.getZoneId(), zone.getName());
                } else {
                    skippedCount++;
                    log.info("域名前缀池已存在，跳过: zoneId={}, zoneName={}", zone.getZoneId(), zone.getName());
                }
            } catch (Exception e) {
                log.error("域名前缀池初始化失败: zoneId={}, error={}", zone.getZoneId(), e.getMessage());
                skippedCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalZones", totalZones);
        result.put("initializedCount", initializedCount);
        result.put("skippedCount", skippedCount);
        result.put("message", String.format("批量初始化完成：共%d个域名，成功初始化%d个，跳过%d个", 
                totalZones, initializedCount, skippedCount));
        
        log.info("批量初始化前缀池完成: totalZones={}, initializedCount={}, skippedCount={}", 
                totalZones, initializedCount, skippedCount);
        return result;
    }

    @Override
    public Map<String, Object> getAllPrefixPoolStatus() {
        log.info("获取所有域名前缀池状态");
        
        // 获取所有域名的前缀池状态
        List<Map<String, Object>> zoneStats = adminCloudflareZoneMapper.getAllZonePrefixStats();
        
        // 计算总体统计
        int totalZones = zoneStats.size();
        int zonesWithPool = 0;
        int zonesWithoutPool = 0;
        int totalPrefixes = 0;
        int totalAssigned = 0;
        
        for (Map<String, Object> stats : zoneStats) {
            Integer prefixCount = (Integer) stats.get("total_prefixes");
            if (prefixCount != null && prefixCount > 0) {
                zonesWithPool++;
                totalPrefixes += prefixCount;
                Integer assigned = (Integer) stats.get("assigned_prefixes");
                if (assigned != null) {
                    totalAssigned += assigned;
                }
            } else {
                zonesWithoutPool++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalZones", totalZones);
        result.put("zonesWithPool", zonesWithPool);
        result.put("zonesWithoutPool", zonesWithoutPool);
        result.put("totalPrefixes", totalPrefixes);
        result.put("totalAssigned", totalAssigned);
        result.put("totalAvailable", totalPrefixes - totalAssigned);
        result.put("overallUsagePercentage", totalPrefixes > 0 ? 
                Math.round((double) totalAssigned / totalPrefixes * 100 * 100) / 100.0 : 0);
        result.put("zoneDetails", zoneStats);
        
        return result;
    }

    @Override
    public Map<String, Object> getZoneDetails(String zoneId) {
        log.info("获取域名详细信息: zoneId={}", zoneId);
        
        // 获取域名基本信息
        CloudflareZone zone = adminCloudflareZoneMapper.findByZoneId(zoneId);
        if (zone == null) {
            throw new RuntimeException("域名不存在: " + zoneId);
        }

        // 获取前缀池统计
        Map<String, Object> prefixStats = adminCloudflareZoneMapper.getPrefixPoolStats(zoneId);
        
        // 获取用户注册统计
        List<Map<String, Object>> userRegistrations = adminCloudflareZoneMapper.getUserRegistrations(zoneId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("zoneInfo", zone);
        result.put("prefixPoolStats", prefixStats);
        result.put("userRegistrations", userRegistrations);
        result.put("userRegistrationCount", userRegistrations.size());
        
        return result;
    }
}