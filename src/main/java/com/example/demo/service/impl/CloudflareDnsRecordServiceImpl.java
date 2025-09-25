package com.example.demo.service.impl;

import com.example.demo.dto.CloudflareDnsRecordResponse;
import com.example.demo.dto.CreateDnsRecordRequest;
import com.example.demo.dto.CreateDnsRecordResponse;
import com.example.demo.entity.CloudflareDnsRecord;
import com.example.demo.entity.CloudflareZone;
import com.example.demo.mapper.CloudflareDnsRecordMapper;
import com.example.demo.service.CloudflareService;
import com.example.demo.service.CloudflareDnsRecordService;
import com.example.demo.service.CloudflareZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件名：CloudflareDnsRecordServiceImpl.java
 * 功能：Cloudflare DNS记录服务实现类
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloudflareDnsRecordServiceImpl implements CloudflareDnsRecordService {
    
    private final CloudflareDnsRecordMapper cloudflareRecordMapper;
    private final CloudflareService cloudflareService;
    private final CloudflareZoneService cloudflareZoneService;
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
    private static final DateTimeFormatter FALLBACK_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    @Override
    @Transactional
    public int syncDnsRecordsFromCloudflare(String zoneId) {
        try {
            log.info("=== 开始同步Zone {} 的DNS记录 ===", zoneId);
            
            // 步骤1：从Cloudflare API获取DNS记录
            log.info("步骤1：从Cloudflare API获取DNS记录");
            CloudflareDnsRecordResponse response = cloudflareService.getDnsRecords(zoneId);
            
            if (response == null || !response.isSuccess() || response.getResult() == null) {
                log.error("获取Zone {} 的DNS记录失败", zoneId);
                return 0;
            }
            
            List<CloudflareDnsRecordResponse.DnsRecord> apiRecords = response.getResult();
            log.info("从Cloudflare API获取到 {} 条DNS记录", apiRecords.size());
            
            // 步骤2：转换为本地实体
            log.info("步骤2：转换API数据为本地实体");
            List<CloudflareDnsRecord> localRecords = new ArrayList<>();
            for (CloudflareDnsRecordResponse.DnsRecord apiRecord : apiRecords) {
                CloudflareDnsRecord localRecord = convertToEntity(apiRecord, zoneId);
                localRecords.add(localRecord);
            }
            
            // 步骤3：清理旧记录并保存新记录
            log.info("步骤3：清理旧记录并保存新记录");
            cloudflareRecordMapper.deleteByZoneId(zoneId);
            int savedCount = batchSaveOrUpdateDnsRecords(localRecords);
            
            log.info("=== Zone {} DNS记录同步完成，共同步 {} 条记录 ===", zoneId, savedCount);
            return savedCount;
            
        } catch (Exception e) {
            log.error("同步Zone {} DNS记录失败", zoneId, e);
            throw new RuntimeException("同步DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public int syncAllDnsRecordsFromCloudflare() {
        try {
            log.info("=== 开始同步所有Zone的DNS记录 ===");
            
            // 获取所有本地Zone
            List<CloudflareZone> zones = cloudflareZoneService.getAllLocalZones();
            log.info("找到 {} 个Zone，开始逐个同步DNS记录", zones.size());
            
            int totalSyncCount = 0;
            for (CloudflareZone zone : zones) {
                try {
                    int syncCount = syncDnsRecordsFromCloudflare(zone.getZoneId());
                    totalSyncCount += syncCount;
                    log.info("Zone {} ({}) 同步完成，同步了 {} 条记录", zone.getName(), zone.getZoneId(), syncCount);
                } catch (Exception e) {
                    log.error("同步Zone {} ({}) 的DNS记录失败", zone.getName(), zone.getZoneId(), e);
                }
            }
            
            log.info("=== 所有Zone DNS记录同步完成，总共同步 {} 条记录 ===", totalSyncCount);
            return totalSyncCount;
            
        } catch (Exception e) {
            log.error("同步所有DNS记录失败", e);
            throw new RuntimeException("同步所有DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<CloudflareDnsRecord> getLocalDnsRecordsByZoneId(String zoneId) {
        try {
            log.info("获取Zone {} 的本地DNS记录", zoneId);
            List<CloudflareDnsRecord> records = cloudflareRecordMapper.findByZoneId(zoneId);
            log.info("获取到 {} 条DNS记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("获取Zone {} 的本地DNS记录失败", zoneId, e);
            throw new RuntimeException("获取本地DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<CloudflareDnsRecord> getLocalDnsRecordsByZoneIdAndType(String zoneId, String type) {
        try {
            log.info("获取Zone {} 类型为 {} 的本地DNS记录", zoneId, type);
            List<CloudflareDnsRecord> records = cloudflareRecordMapper.findByZoneIdAndType(zoneId, type);
            log.info("获取到 {} 条DNS记录", records.size());
            return records;
        } catch (Exception e) {
            log.error("获取Zone {} 类型为 {} 的本地DNS记录失败", zoneId, type, e);
            throw new RuntimeException("获取本地DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CloudflareDnsRecord getLocalDnsRecordByRecordId(String recordId) {
        try {
            log.info("根据记录ID获取本地DNS记录: {}", recordId);
            CloudflareDnsRecord record = cloudflareRecordMapper.findByRecordId(recordId);
            if (record != null) {
                log.info("找到DNS记录: id={}, name={}, type={}", record.getId(), record.getName(), record.getType());
            } else {
                log.info("未找到记录ID: {}", recordId);
            }
            return record;
        } catch (Exception e) {
            log.error("根据记录ID获取本地DNS记录失败: {}", recordId, e);
            throw new RuntimeException("获取本地DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean saveOrUpdateDnsRecord(CloudflareDnsRecord record) {
        try {
            log.info("保存或更新DNS记录: name={}, type={}, recordId={}", record.getName(), record.getType(), record.getRecordId());
            
            // 检查是否已存在
            CloudflareDnsRecord existingRecord = cloudflareRecordMapper.findByRecordId(record.getRecordId());
            
            int result;
            if (existingRecord != null) {
                // 更新现有记录
                record.setId(existingRecord.getId());
                record.setCreateTime(existingRecord.getCreateTime());
                record.setUpdateTime(LocalDateTime.now());
                result = cloudflareRecordMapper.updateByRecordId(record);
                log.info("更新DNS记录成功: id={}", record.getId());
            } else {
                // 插入新记录
                record.setCreateTime(LocalDateTime.now());
                record.setUpdateTime(LocalDateTime.now());
                result = cloudflareRecordMapper.insert(record);
                log.info("插入新DNS记录成功: id={}", record.getId());
            }
            
            return result > 0;
        } catch (Exception e) {
            log.error("保存或更新DNS记录失败: name={}", record.getName(), e);
            throw new RuntimeException("保存DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public int batchSaveOrUpdateDnsRecords(List<CloudflareDnsRecord> records) {
        try {
            log.info("批量保存或更新 {} 条DNS记录", records.size());
            
            int savedCount = 0;
            for (CloudflareDnsRecord record : records) {
                if (saveOrUpdateDnsRecord(record)) {
                    savedCount++;
                }
            }
            
            log.info("批量保存完成，成功保存 {} 条DNS记录", savedCount);
            return savedCount;
        } catch (Exception e) {
            log.error("批量保存DNS记录失败", e);
            throw new RuntimeException("批量保存DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteDnsRecord(String recordId) {
        try {
            log.info("删除DNS记录: recordId={}", recordId);
            int result = cloudflareRecordMapper.deleteByRecordId(recordId);
            boolean success = result > 0;
            log.info("删除DNS记录{}: recordId={}", success ? "成功" : "失败", recordId);
            return success;
        } catch (Exception e) {
            log.error("删除DNS记录失败: recordId={}", recordId, e);
            throw new RuntimeException("删除DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public int deleteDnsRecordsByZoneId(String zoneId) {
        try {
            log.info("删除Zone {} 的所有DNS记录", zoneId);
            int result = cloudflareRecordMapper.deleteByZoneId(zoneId);
            log.info("删除Zone {} 的DNS记录完成，删除了 {} 条记录", zoneId, result);
            return result;
        } catch (Exception e) {
            log.error("删除Zone {} 的DNS记录失败", zoneId, e);
            throw new RuntimeException("删除DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int countDnsRecordsByZoneId(String zoneId) {
        try {
            log.info("统计Zone {} 的DNS记录数量", zoneId);
            int count = cloudflareRecordMapper.countByZoneId(zoneId);
            log.info("Zone {} 共有 {} 条DNS记录", zoneId, count);
            return count;
        } catch (Exception e) {
            log.error("统计Zone {} 的DNS记录数量失败", zoneId, e);
            throw new RuntimeException("统计DNS记录数量失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public CloudflareDnsRecord createDnsRecord(String zoneId, CreateDnsRecordRequest request) {
        try {
            log.info("=== 开始创建DNS记录到Cloudflare ===");
            log.info("Zone ID: {}, 记录名称前缀: {}, 类型: {}, 内容: {}", 
                    zoneId, request.getNamePrefix(), request.getType(), request.getContent());
            
            // 步骤1：验证请求参数
            validateCreateDnsRecordRequest(request);
            
            // 步骤2：调用Cloudflare API创建DNS记录
            log.info("步骤1：调用Cloudflare API创建DNS记录");
            CreateDnsRecordResponse createResponse = cloudflareService.createDnsRecord(zoneId, request);
            
            if (createResponse == null || !createResponse.isSuccess()) {
                String errorMsg = "Cloudflare API创建DNS记录失败";
                if (createResponse != null && createResponse.getErrors() != null && !createResponse.getErrors().isEmpty()) {
                    errorMsg += ": " + createResponse.getErrors().get(0).getMessage();
                }
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            // 步骤3：转换为本地实体并保存到数据库
            log.info("步骤2：转换API响应为本地实体并保存到数据库");
            CloudflareDnsRecord localRecord = convertCreateResponseToEntity(createResponse, zoneId);
            
            // 步骤4：保存到本地数据库
            boolean saveSuccess = saveOrUpdateDnsRecord(localRecord);
            if (!saveSuccess) {
                log.error("保存DNS记录到本地数据库失败");
                throw new RuntimeException("保存DNS记录到本地数据库失败");
            }
            
            log.info("=== DNS记录创建完成 ===");
            log.info("Cloudflare记录ID: {}, 本地记录ID: {}, 记录名称: {}", 
                    localRecord.getRecordId(), localRecord.getId(), localRecord.getName());
            
            return localRecord;
            
        } catch (Exception e) {
            log.error("创建DNS记录失败: zoneId={}, namePrefix={}", zoneId, request.getNamePrefix(), e);
            throw new RuntimeException("创建DNS记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CloudflareDnsRecord convertCreateResponseToEntity(CreateDnsRecordResponse createResponse, String zoneId) {
        try {
            if (createResponse == null || createResponse.getResult() == null) {
                throw new IllegalArgumentException("创建DNS记录响应数据为空");
            }
            
            CreateDnsRecordResponse.DnsRecordResult result = createResponse.getResult();
            CloudflareDnsRecord entity = new CloudflareDnsRecord();
            
            // 基本信息
            entity.setZoneId(zoneId);
            entity.setRecordId(result.getId());
            entity.setName(result.getName());
            entity.setType(result.getType());
            entity.setContent(result.getContent());
            
            // 代理设置
            entity.setProxiable(result.isProxiable());
            entity.setProxied(result.isProxied());
            entity.setTtl(result.getTtl());
            
            // 优先级和权重
            entity.setPriority(result.getPriority());
            entity.setWeight(result.getWeight());
            entity.setPort(result.getPort());
            
            // JSON字段
            entity.setSettings(result.getSettings());
            entity.setMeta(result.getMeta());
            entity.setTags(result.getTags());
            
            // 备注
            entity.setComment(result.getComment());
            
            // Cloudflare时间字段
            entity.setCreatedOn(parseCloudflareTime(result.getCreatedOn()));
            entity.setModifiedOn(parseCloudflareTime(result.getModifiedOn()));
            entity.setCommentModifiedOn(parseCloudflareTime(result.getCommentModifiedOn()));
            
            // 同步状态
            entity.setSyncStatus("SUCCESS");
            entity.setLastSyncTime(LocalDateTime.now());
            entity.setIsActive(true);
            
            // 本地时间戳
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            
            log.debug("转换创建DNS记录响应成功: name={}, type={}, content={}", 
                    entity.getName(), entity.getType(), entity.getContent());
            return entity;
        } catch (Exception e) {
            log.error("转换创建DNS记录响应数据为本地实体失败", e);
            throw new RuntimeException("转换DNS记录数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证创建DNS记录请求参数
     */
    private void validateCreateDnsRecordRequest(CreateDnsRecordRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("创建DNS记录请求不能为空");
        }
        
        if (request.getNamePrefix() == null || request.getNamePrefix().trim().isEmpty()) {
            throw new IllegalArgumentException("记录名称前缀不能为空");
        }
        
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("记录类型不能为空");
        }
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("记录内容不能为空");
        }
        
        if (request.getTtl() == null || request.getTtl() < 1) {
            throw new IllegalArgumentException("TTL值必须大于等于1");
        }
        
        // 验证MX记录
        if (!request.validateMxRecord()) {
            throw new IllegalArgumentException("MX记录必须设置优先级");
        }
        
        // 验证SRV记录
        if (!request.validateSrvRecord()) {
            throw new IllegalArgumentException("SRV记录必须设置优先级、权重、端口、服务名、协议和目标");
        }
        
        log.debug("DNS记录请求参数验证通过: namePrefix={}, type={}", request.getNamePrefix(), request.getType());
    }
    
    @Override
    public CloudflareDnsRecord convertToEntity(CloudflareDnsRecordResponse.DnsRecord apiRecord, String zoneId) {
        try {
            CloudflareDnsRecord entity = new CloudflareDnsRecord();
            
            // 基本信息
            entity.setZoneId(zoneId);
            entity.setRecordId(apiRecord.getId());
            entity.setName(apiRecord.getName());
            entity.setType(apiRecord.getType());
            entity.setContent(apiRecord.getContent());
            
            // 代理设置
            entity.setProxiable(apiRecord.isProxiable());
            entity.setProxied(apiRecord.isProxied());
            entity.setTtl(apiRecord.getTtl());
            
            // 优先级和权重
            entity.setPriority(apiRecord.getPriority());
            entity.setWeight(apiRecord.getWeight());
            entity.setPort(apiRecord.getPort());
            
            // SRV记录特殊处理
            if (apiRecord.getData() != null) {
                CloudflareDnsRecordResponse.SrvData srvData = apiRecord.getData();
                entity.setService(srvData.getService());
                entity.setProto(srvData.getProto());
                entity.setTarget(srvData.getTarget());
                if (srvData.getPriority() != null) {
                    entity.setPriority(srvData.getPriority());
                }
                if (srvData.getWeight() != null) {
                    entity.setWeight(srvData.getWeight());
                }
                if (srvData.getPort() != null) {
                    entity.setPort(srvData.getPort());
                }
            }
            
            // JSON字段 - 确保正确的类型转换
            entity.setSettings(apiRecord.getSettings());
            entity.setMeta(apiRecord.getMeta());
            entity.setTags(apiRecord.getTags());
            
            // 备注
            entity.setComment(apiRecord.getComment());
            
            // Cloudflare时间字段
            entity.setCreatedOn(parseCloudflareTime(apiRecord.getCreatedOn()));
            entity.setModifiedOn(parseCloudflareTime(apiRecord.getModifiedOn()));
            entity.setCommentModifiedOn(parseCloudflareTime(apiRecord.getCommentModifiedOn()));
            
            // 同步状态
            entity.setSyncStatus("SUCCESS");
            entity.setLastSyncTime(LocalDateTime.now());
            entity.setIsActive(true);
            
            // 本地时间戳
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            
            log.debug("转换DNS记录成功: name={}, type={}, content={}", entity.getName(), entity.getType(), entity.getContent());
            return entity;
        } catch (Exception e) {
            log.error("转换API DNS记录数据为本地实体失败: recordName={}", apiRecord.getName(), e);
            throw new RuntimeException("转换DNS记录数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析Cloudflare时间字符串为LocalDateTime
     * @param timeStr 时间字符串
     * @return LocalDateTime对象，解析失败时返回当前时间
     */
    private LocalDateTime parseCloudflareTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return LocalDateTime.now();
        }
        
        try {
            // 尝试完整格式解析
            return LocalDateTime.parse(timeStr, ISO_FORMATTER);
        } catch (Exception e1) {
            try {
                // 尝试简化格式解析
                return LocalDateTime.parse(timeStr, FALLBACK_FORMATTER);
            } catch (Exception e2) {
                log.warn("无法解析时间字符串: {}, 使用当前时间", timeStr);
                return LocalDateTime.now();
            }
        }
    }
}