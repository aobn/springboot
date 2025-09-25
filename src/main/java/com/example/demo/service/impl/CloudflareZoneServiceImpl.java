package com.example.demo.service.impl;

import com.example.demo.entity.CloudflareZone;
import com.example.demo.dto.CloudflareZoneResponse;
import com.example.demo.mapper.CloudflareZoneMapper;
import com.example.demo.service.CloudflareService;
import com.example.demo.service.CloudflareZoneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件名：CloudflareZoneServiceImpl.java
 * 功能：Cloudflare域名数据服务实现类
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloudflareZoneServiceImpl implements CloudflareZoneService {
    
    private final CloudflareZoneMapper cloudflareZoneMapper;
    private final CloudflareService cloudflareService;
    private final ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public int syncZonesFromCloudflare() {
        try {
            log.info("=== 开始同步Cloudflare域名数据 ===");
            
            // 步骤1：从Cloudflare API获取域名列表
            log.info("步骤1：从Cloudflare API获取域名列表");
            CloudflareZoneResponse response = cloudflareService.getAllZones();
            
            if (response == null || !response.isSuccess() || response.getResult() == null) {
                log.error("获取Cloudflare域名列表失败");
                return 0;
            }
            
            List<CloudflareZoneResponse.Zone> apiZones = response.getResult();
            log.info("从Cloudflare API获取到 {} 个域名", apiZones.size());
            
            // 步骤2：转换为本地实体
            log.info("步骤2：转换API数据为本地实体");
            List<CloudflareZone> localZones = new ArrayList<>();
            for (CloudflareZoneResponse.Zone apiZone : apiZones) {
                CloudflareZone localZone = convertToEntity(apiZone);
                localZones.add(localZone);
            }
            
            // 步骤3：批量保存或更新到数据库
            log.info("步骤3：批量保存域名数据到数据库");
            int savedCount = batchSaveOrUpdateZones(localZones);
            
            log.info("=== 域名数据同步完成，共同步 {} 个域名 ===", savedCount);
            return savedCount;
            
        } catch (Exception e) {
            log.error("同步Cloudflare域名数据失败", e);
            throw new RuntimeException("同步域名数据失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<CloudflareZone> getAllLocalZones() {
        try {
            log.info("获取所有本地存储的Cloudflare域名");
            List<CloudflareZone> zones = cloudflareZoneMapper.findAll();
            log.info("获取到 {} 个本地域名", zones.size());
            return zones;
        } catch (Exception e) {
            log.error("获取本地域名列表失败", e);
            throw new RuntimeException("获取本地域名列表失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CloudflareZone getLocalZoneByName(String zoneName) {
        try {
            log.info("根据域名名称获取本地域名信息: {}", zoneName);
            CloudflareZone zone = cloudflareZoneMapper.findByName(zoneName);
            if (zone != null) {
                log.info("找到域名信息: id={}, name={}", zone.getId(), zone.getName());
            } else {
                log.info("未找到域名: {}", zoneName);
            }
            return zone;
        } catch (Exception e) {
            log.error("根据域名名称获取本地域名信息失败: {}", zoneName, e);
            throw new RuntimeException("获取本地域名信息失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CloudflareZone getLocalZoneById(String zoneId) {
        try {
            log.info("根据Zone ID获取本地域名信息: {}", zoneId);
            CloudflareZone zone = cloudflareZoneMapper.findByZoneId(zoneId);
            if (zone != null) {
                log.info("找到域名信息: id={}, name={}", zone.getId(), zone.getName());
            } else {
                log.info("未找到Zone ID: {}", zoneId);
            }
            return zone;
        } catch (Exception e) {
            log.error("根据Zone ID获取本地域名信息失败: {}", zoneId, e);
            throw new RuntimeException("获取本地域名信息失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean saveOrUpdateZone(CloudflareZone zone) {
        try {
            log.info("保存或更新域名信息: name={}, zoneId={}", zone.getName(), zone.getZoneId());
            
            // 检查是否已存在
            CloudflareZone existingZone = cloudflareZoneMapper.findByZoneId(zone.getZoneId());
            
            int result;
            if (existingZone != null) {
                // 更新现有记录
                zone.setId(existingZone.getId());
                zone.setCreateTime(existingZone.getCreateTime());
                zone.setUpdateTime(LocalDateTime.now());
                result = cloudflareZoneMapper.updateByZoneId(zone);
                log.info("更新域名信息成功: id={}", zone.getId());
            } else {
                // 插入新记录
                zone.setCreateTime(LocalDateTime.now());
                zone.setUpdateTime(LocalDateTime.now());
                result = cloudflareZoneMapper.insert(zone);
                log.info("插入新域名信息成功: id={}", zone.getId());
            }
            
            return result > 0;
        } catch (Exception e) {
            log.error("保存或更新域名信息失败: name={}", zone.getName(), e);
            throw new RuntimeException("保存域名信息失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public int batchSaveOrUpdateZones(List<CloudflareZone> zones) {
        try {
            log.info("批量保存或更新 {} 个域名信息", zones.size());
            
            int savedCount = 0;
            for (CloudflareZone zone : zones) {
                if (saveOrUpdateZone(zone)) {
                    savedCount++;
                }
            }
            
            log.info("批量保存完成，成功保存 {} 个域名", savedCount);
            return savedCount;
        } catch (Exception e) {
            log.error("批量保存域名信息失败", e);
            throw new RuntimeException("批量保存域名信息失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteZone(String zoneId) {
        try {
            log.info("删除域名信息: zoneId={}", zoneId);
            int result = cloudflareZoneMapper.deleteByZoneId(zoneId);
            boolean success = result > 0;
            log.info("删除域名信息{}: zoneId={}", success ? "成功" : "失败", zoneId);
            return success;
        } catch (Exception e) {
            log.error("删除域名信息失败: zoneId={}", zoneId, e);
            throw new RuntimeException("删除域名信息失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CloudflareZone convertToEntity(CloudflareZoneResponse.Zone apiZone) {
        try {
            log.info("开始转换API域名数据: name={}, id={}", apiZone.getName(), apiZone.getId());
            log.debug("API Zone详细信息: createdOn={}, modifiedOn={}, activatedOn={}", 
                    apiZone.getCreatedOn(), apiZone.getModifiedOn(), apiZone.getActivatedOn());
            
            CloudflareZone entity = new CloudflareZone();
            LocalDateTime now = LocalDateTime.now();
            
            // 基本信息
            entity.setZoneId(apiZone.getId());
            entity.setName(apiZone.getName());
            entity.setStatus(apiZone.getStatus());
            entity.setType(apiZone.getType());
            entity.setPaused(apiZone.isPaused());
            entity.setDevelopmentMode(apiZone.getDevelopmentMode() != null ? apiZone.getDevelopmentMode() : 0);
            
            // 名称服务器
            if (apiZone.getNameServers() != null) {
                entity.setNameServers(apiZone.getNameServers());
            }
            
            // 原始名称服务器
            if (apiZone.getOriginalNameServers() != null) {
                entity.setOriginalNameServers(apiZone.getOriginalNameServers());
            }
            
            // 原始注册商和DNS主机
            entity.setOriginalRegistrar(apiZone.getOriginalRegistrar());
            entity.setOriginalDnshost(apiZone.getOriginalDnshost());
            
            // Cloudflare时间字段 - 必填字段，使用ISO格式解析
            // 如果API返回的时间为null，使用当前时间作为默认值
            entity.setCreatedOn(parseCloudflareDateTime(apiZone.getCreatedOn(), now));
            entity.setModifiedOn(parseCloudflareDateTime(apiZone.getModifiedOn(), now));
            entity.setActivatedOn(parseCloudflareDateTime(apiZone.getActivatedOn(), null));
            
            // 确保必填字段不为null
            if (entity.getCreatedOn() == null) {
                entity.setCreatedOn(now);
            }
            if (entity.getModifiedOn() == null) {
                entity.setModifiedOn(now);
            }
            
            // 虚荣名称服务器
            if (apiZone.getVanityNameServers() != null) {
                entity.setVanityNameServers(apiZone.getVanityNameServers());
            }
            
            // Meta信息
            if (apiZone.getMeta() != null) {
                entity.setMetaStep(apiZone.getMeta().getStep());
                entity.setMetaCustomCertificateQuota(apiZone.getMeta().getCustomCertificateQuota());
                entity.setMetaPageRuleQuota(apiZone.getMeta().getPageRuleQuota());
                entity.setMetaPhishingDetected(apiZone.getMeta().getPhishingDetected() != null ? apiZone.getMeta().getPhishingDetected() : false);
            }
            
            // 所有者信息
            if (apiZone.getOwner() != null) {
                entity.setOwnerId(apiZone.getOwner().getId());
                entity.setOwnerType(apiZone.getOwner().getType());
                entity.setOwnerEmail(apiZone.getOwner().getEmail());
            }
            
            // 账户信息 - 必填字段
            if (apiZone.getAccount() != null) {
                entity.setAccountId(apiZone.getAccount().getId() != null ? apiZone.getAccount().getId() : "unknown");
                entity.setAccountName(apiZone.getAccount().getName() != null ? apiZone.getAccount().getName() : "Unknown Account");
            } else {
                entity.setAccountId("unknown");
                entity.setAccountName("Unknown Account");
            }
            
            // 租户信息
            if (apiZone.getTenant() != null) {
                entity.setTenantId(apiZone.getTenant().getId());
                entity.setTenantName(apiZone.getTenant().getName());
            }
            if (apiZone.getTenantUnit() != null) {
                entity.setTenantUnitId(apiZone.getTenantUnit().getId());
            }
            
            // 计划信息 - 必填字段
            if (apiZone.getPlan() != null) {
                entity.setPlanId(apiZone.getPlan().getId() != null ? apiZone.getPlan().getId() : "free");
                entity.setPlanName(apiZone.getPlan().getName() != null ? apiZone.getPlan().getName() : "Free Plan");
                entity.setPlanPrice(apiZone.getPlan().getPrice() != null ? BigDecimal.valueOf(apiZone.getPlan().getPrice()) : BigDecimal.ZERO);
                entity.setPlanCurrency(apiZone.getPlan().getCurrency() != null ? apiZone.getPlan().getCurrency() : "USD");
                entity.setPlanFrequency(apiZone.getPlan().getFrequency() != null ? apiZone.getPlan().getFrequency() : "");
                entity.setPlanIsSubscribed(apiZone.getPlan().getIsSubscribed() != null ? apiZone.getPlan().getIsSubscribed() : false);
                entity.setPlanCanSubscribe(apiZone.getPlan().getCanSubscribe() != null ? apiZone.getPlan().getCanSubscribe() : false);
                entity.setPlanLegacyId(apiZone.getPlan().getLegacyId());
                entity.setPlanLegacyDiscount(apiZone.getPlan().getLegacyDiscount() != null ? apiZone.getPlan().getLegacyDiscount() : false);
                entity.setPlanExternallyManaged(apiZone.getPlan().getExternallyManaged() != null ? apiZone.getPlan().getExternallyManaged() : false);
            } else {
                entity.setPlanId("free");
                entity.setPlanName("Free Plan");
                entity.setPlanPrice(BigDecimal.ZERO);
                entity.setPlanCurrency("USD");
                entity.setPlanFrequency("");
                entity.setPlanIsSubscribed(false);
                entity.setPlanCanSubscribe(false);
                entity.setPlanLegacyDiscount(false);
                entity.setPlanExternallyManaged(false);
            }
            
            // 权限信息
            if (apiZone.getPermissions() != null) {
                entity.setPermissions(apiZone.getPermissions());
            }
            
            // 同步状态
            entity.setSyncStatus("SUCCESS");
            entity.setLastSyncTime(now);
            entity.setIsActive(true);
            
            // 本地时间戳
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            
            log.info("转换完成，域名实体信息: name={}, createdOn={}, modifiedOn={}, accountId={}, planId={}", 
                    entity.getName(), entity.getCreatedOn(), entity.getModifiedOn(), entity.getAccountId(), entity.getPlanId());
            
            return entity;
        } catch (Exception e) {
            log.error("转换API域名数据为本地实体失败: zoneName={}", apiZone.getName(), e);
            throw new RuntimeException("转换域名数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析Cloudflare时间字符串为LocalDateTime
     * 
     * @param dateTimeStr Cloudflare时间字符串
     * @param defaultValue 默认值
     * @return LocalDateTime
     */
    private LocalDateTime parseCloudflareDateTime(String dateTimeStr, LocalDateTime defaultValue) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            log.debug("时间字符串为空，使用默认值: {}", defaultValue);
            return defaultValue;
        }
        
        try {
            // 移除时区信息，Cloudflare通常返回UTC时间
            String cleanDateStr = dateTimeStr.replace("Z", "").replace("+00:00", "");
            
            // 尝试多种格式解析
            DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            };
            
            for (DateTimeFormatter formatter : formatters) {
                try {
                    LocalDateTime result = LocalDateTime.parse(cleanDateStr, formatter);
                    log.debug("成功解析时间字符串: {} -> {}", dateTimeStr, result);
                    return result;
                } catch (Exception ignored) {
                    // 继续尝试下一个格式
                }
            }
            
            log.warn("无法解析时间字符串: {}, 使用默认值: {}", dateTimeStr, defaultValue);
            return defaultValue;
            
        } catch (Exception e) {
            log.warn("解析时间字符串异常: {}, 使用默认值: {}, 异常: {}", dateTimeStr, defaultValue, e.getMessage());
            return defaultValue;
        }
    }
}