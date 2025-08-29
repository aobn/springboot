package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.UserDnsRecordRequest;
import com.example.demo.dto.UserDnsRecordUpdateRequest;
import com.example.demo.dto.UserDnsRecordQueryRequest;
import com.example.demo.entity.UserDnsRecord;
import com.example.demo.entity.UserSubdomain;
import com.example.demo.service.DnspodService;
import com.example.demo.service.UserDnsRecordService;
import com.example.demo.service.UserSubdomainService;
import com.example.demo.util.JwtUtil;
import com.tencentcloudapi.dnspod.v20210323.models.CreateRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyRecordResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户DNS解析记录控制器
 * 提供用户DNS解析记录的管理功能
 * 
 * @author CodeBuddy
 * @since 2025-08-25
 */
@RestController
@RequestMapping("/api/user/dns-records")
@Slf4j
@Validated
public class UserDnsRecordController {
    
    @Autowired
    private UserDnsRecordService userDnsRecordService;
    
    @Autowired
    private UserSubdomainService userSubdomainService;
    
    @Autowired
    private DnspodService dnspodService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 添加用户DNS解析记录
     * 
     * @param request DNS解析记录请求
     * @param authHeader Authorization头信息
     * @return 添加结果
     */
    @PostMapping
    @Transactional
    public ApiResponse<UserDnsRecord> addDnsRecord(
            @Valid @RequestBody UserDnsRecordRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 从Authorization头中提取token
            String token = authHeader.replace("Bearer ", "");
            
            // 从token中获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            
            log.info("用户 {} ({}) 请求添加DNS解析记录: {}", userId, email, request);
            
            // 1. 验证用户是否拥有该子域名
            UserSubdomain userSubdomain = userSubdomainService.getById(request.getSubdomainId());
            if (userSubdomain == null) {
                return ApiResponse.error(404, "子域名不存在");
            }
            
            if (!userSubdomain.getUserId().equals(userId)) {
                return ApiResponse.error(403, "无权限操作该子域名");
            }
            
            if (!"ACTIVE".equals(userSubdomain.getStatus())) {
                return ApiResponse.error(400, "子域名状态异常，无法添加解析记录");
            }
            
            // 2. 检查记录是否已存在
            if (userDnsRecordService.existsRecord(userId, request.getSubdomainId(), 
                    request.getName(), request.getType())) {
                return ApiResponse.error(409, "DNS解析记录已存在");
            }
            
            // 3. 验证记录类型和值的格式
            if (!isValidRecordValue(request.getType(), request.getValue())) {
                return ApiResponse.error(400, "记录值格式不正确");
            }
            
            // 4. 创建本地DNS记录（状态为PENDING）
            UserDnsRecord dnsRecord = new UserDnsRecord();
            dnsRecord.setUserId(userId);
            dnsRecord.setSubdomainId(request.getSubdomainId());
            dnsRecord.setName(request.getName());
            dnsRecord.setType(request.getType());
            dnsRecord.setValue(request.getValue());
            dnsRecord.setLine(request.getLine() != null ? request.getLine() : "默认");
            dnsRecord.setLineId("0");
            dnsRecord.setTtl(request.getTtl() != null ? request.getTtl() : 600);
            dnsRecord.setMx(request.getMx());
            dnsRecord.setWeight(request.getWeight());
            dnsRecord.setStatus("ENABLE");
            dnsRecord.setRemark(request.getRemark());
            dnsRecord.setSyncStatus("PENDING");
            
            // 保存到数据库
            UserDnsRecord savedRecord = userDnsRecordService.createRecord(dnsRecord);
            
            // 5. 同步到DNSPod
            try {
                // 构建完整域名（子域名.主域名）
                String fullDomain = userSubdomain.getFullDomain();
                String[] domainParts = fullDomain.split("\\.", 2);
                if (domainParts.length != 2) {
                    throw new RuntimeException("域名格式错误: " + fullDomain);
                }
                
                String subDomainPrefix = domainParts[0];
                String mainDomain = domainParts[1];
                
                // 构建DNSPod记录的主机记录
                String dnspodSubDomain;
                if ("@".equals(request.getName())) {
                    dnspodSubDomain = subDomainPrefix;
                } else {
                    dnspodSubDomain = request.getName() + "." + subDomainPrefix;
                }
                
                log.info("调用DNSPod API添加记录: domain={}, subDomain={}, type={}, value={}", 
                        mainDomain, dnspodSubDomain, request.getType(), request.getValue());
                
                CreateRecordResponse response = dnspodService.createRecord(
                        mainDomain,
                        request.getType(),
                        dnsRecord.getLine(),
                        request.getValue(),
                        dnspodSubDomain,
                        dnsRecord.getTtl().longValue(),
                        dnsRecord.getMx() != null ? dnsRecord.getMx().longValue() : null,
                        dnsRecord.getWeight() != null ? dnsRecord.getWeight().longValue() : null,
                        dnsRecord.getStatus(),
                        dnsRecord.getRemark()
                );
                
                // 6. 更新本地记录状态
                if (response != null && response.getRecordId() != null) {
                    userDnsRecordService.updateRecordId(savedRecord.getId(), response.getRecordId());
                    userDnsRecordService.updateSyncStatus(savedRecord.getId(), "SUCCESS", null);
                    
                    // 更新返回的记录对象
                    savedRecord.setRecordId(response.getRecordId());
                    savedRecord.setSyncStatus("SUCCESS");
                    
                    log.info("DNS解析记录添加成功: recordId={}, dnspodRecordId={}", 
                            savedRecord.getId(), response.getRecordId());
                } else {
                    throw new RuntimeException("DNSPod API返回异常");
                }
                
            } catch (Exception e) {
                log.error("同步DNS记录到DNSPod失败，删除本地记录", e);
                
                // DNSPod添加记录失败，删除本地数据库对应的DNS记录
                try {
                    userDnsRecordService.deleteRecord(savedRecord.getId());
                    log.info("已删除本地DNS记录: recordId={}", savedRecord.getId());
                } catch (Exception deleteException) {
                    log.error("删除本地DNS记录失败: recordId={}", savedRecord.getId(), deleteException);
                }
                
                return ApiResponse.error(500, "DNS解析记录创建失败: " + e.getMessage());
            }
            
            return ApiResponse.success(savedRecord);
            
        } catch (Exception e) {
            log.error("添加DNS解析记录失败", e);
            return ApiResponse.error(500, "添加DNS解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改用户DNS解析记录
     * 
     * @param id 记录ID
     * @param request DNS解析记录更新请求
     * @param authHeader Authorization头信息
     * @return 修改结果
     */
    @PutMapping("/{id}")
    @Transactional
    public ApiResponse<UserDnsRecord> updateDnsRecord(
            @PathVariable Long id,
            @Valid @RequestBody UserDnsRecordUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 从Authorization头中提取token
            String token = authHeader.replace("Bearer ", "");
            
            // 从token中获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            
            log.info("用户 {} ({}) 请求修改DNS解析记录 {}: {}", userId, email, id, request);
            
            // 1. 验证记录是否存在且属于当前用户
            UserDnsRecord record = userDnsRecordService.getRecordById(id);
            if (record == null) {
                return ApiResponse.error(404, "DNS解析记录不存在");
            }
            
            if (!record.getUserId().equals(userId)) {
                return ApiResponse.error(403, "无权限修改该DNS解析记录");
            }
            
            // 2. 获取子域名信息
            UserSubdomain userSubdomain = userSubdomainService.getById(record.getSubdomainId());
            if (userSubdomain == null) {
                return ApiResponse.error(404, "子域名不存在");
            }
            
            if (!"ACTIVE".equals(userSubdomain.getStatus())) {
                return ApiResponse.error(400, "子域名状态异常，无法修改解析记录");
            }
            
            // 3. 如果修改了记录类型，检查是否与其他记录冲突
            if (request.getType() != null && !request.getType().equals(record.getType())) {
                // 检查是否存在同名不同类型的记录
                if (userDnsRecordService.existsRecord(userId, record.getSubdomainId(), record.getName(), request.getType())) {
                    return ApiResponse.error(409, "记录类型冲突：相同主机记录下已存在该类型的记录");
                }
                
                // 特殊检查：CNAME记录不能与其他记录共存
                if ("CNAME".equals(request.getType()) || "CNAME".equals(record.getType())) {
                    List<UserDnsRecord> existingRecords = userDnsRecordService.getRecordsByUserIdAndSubdomainId(userId, record.getSubdomainId());
                    for (UserDnsRecord existingRecord : existingRecords) {
                        if (existingRecord.getId().equals(id)) {
                            continue; // 跳过当前记录
                        }
                        if (existingRecord.getName().equals(record.getName())) {
                            return ApiResponse.error(409, "记录类型冲突：CNAME记录不能与其他记录类型共存于同一主机记录下");
                        }
                    }
                }
            }
            
            // 4. 验证记录值格式
            String recordType = request.getType() != null ? request.getType() : record.getType();
            String recordValue = request.getValue() != null ? request.getValue() : record.getValue();
            if (!isValidRecordValue(recordType, recordValue)) {
                return ApiResponse.error(400, "记录值格式不正确");
            }
            
            // 5. 更新本地记录（保存原始值用于回滚）
            String originalType = record.getType();
            String originalValue = record.getValue();
            Integer originalTtl = record.getTtl();
            Integer originalMx = record.getMx();
            Integer originalWeight = record.getWeight();
            String originalStatus = record.getStatus();
            String originalRemark = record.getRemark();
            
            // 更新记录字段
            if (request.getType() != null) record.setType(request.getType());
            if (request.getValue() != null) record.setValue(request.getValue());
            if (request.getLine() != null) record.setLine(request.getLine());
            if (request.getTtl() != null) record.setTtl(request.getTtl());
            if (request.getMx() != null) record.setMx(request.getMx());
            if (request.getWeight() != null) record.setWeight(request.getWeight());
            if (request.getStatus() != null) record.setStatus(request.getStatus());
            if (request.getRemark() != null) record.setRemark(request.getRemark());
            
            // 更新同步状态为待同步
            record.setSyncStatus("PENDING");
            record.setSyncError(null);
            
            // 保存到数据库
            UserDnsRecord updatedRecord = userDnsRecordService.updateRecord(record);
            
            // 6. 同步到DNSPod
            try {
                // 构建完整域名（子域名.主域名）
                String fullDomain = userSubdomain.getFullDomain();
                String[] domainParts = fullDomain.split("\\.", 2);
                if (domainParts.length != 2) {
                    throw new RuntimeException("域名格式错误: " + fullDomain);
                }
                
                String subDomainPrefix = domainParts[0];
                String mainDomain = domainParts[1];
                
                // 构建DNSPod记录的主机记录
                String dnspodSubDomain;
                if ("@".equals(record.getName())) {
                    dnspodSubDomain = subDomainPrefix;
                } else {
                    dnspodSubDomain = record.getName() + "." + subDomainPrefix;
                }
                
                log.info("调用DNSPod API修改记录: domain={}, recordId={}, subDomain={}, type={}, value={}", 
                        mainDomain, record.getRecordId(), dnspodSubDomain, record.getType(), record.getValue());
                
                // 确保recordId不为空
                if (record.getRecordId() == null) {
                    throw new RuntimeException("DNSPod记录ID为空，无法修改记录");
                }
                
                ModifyRecordResponse response = dnspodService.modifyRecord(
                        mainDomain,
                        record.getRecordId(),
                        record.getType(),
                        record.getLine(),
                        record.getValue(),
                        dnspodSubDomain,
                        null, // domainId
                        record.getTtl().longValue(),
                        record.getMx() != null ? record.getMx().longValue() : null,
                        record.getWeight() != null ? record.getWeight().longValue() : null,
                        record.getStatus(),
                        record.getRemark()
                );
                
                // 7. 更新本地记录状态
                if (response != null && response.getRecordId() != null) {
                    userDnsRecordService.updateSyncStatus(record.getId(), "SUCCESS", null);
                    
                    // 更新返回的记录对象
                    updatedRecord.setSyncStatus("SUCCESS");
                    
                    log.info("DNS解析记录修改成功: recordId={}, dnspodRecordId={}", 
                            record.getId(), response.getRecordId());
                } else {
                    throw new RuntimeException("DNSPod API返回异常");
                }
                
            } catch (Exception e) {
                log.error("同步DNS记录修改到DNSPod失败", e);
                
                // 更新同步状态为失败
                userDnsRecordService.updateSyncStatus(record.getId(), "FAILED", e.getMessage());
                updatedRecord.setSyncStatus("FAILED");
                updatedRecord.setSyncError(e.getMessage());
                
                // 可选：回滚到原始值
                // record.setType(originalType);
                // record.setValue(originalValue);
                // record.setTtl(originalTtl);
                // record.setMx(originalMx);
                // record.setWeight(originalWeight);
                // record.setStatus(originalStatus);
                // record.setRemark(originalRemark);
                // userDnsRecordService.updateRecord(record);
                
                return ApiResponse.error(500, "DNS解析记录修改失败: " + e.getMessage());
            }
            
            return ApiResponse.success(updatedRecord);
            
        } catch (Exception e) {
            log.error("修改DNS解析记录失败", e);
            return ApiResponse.error(500, "修改DNS解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的DNS解析记录列表（URL参数方式）
     * 
     * @param authHeader Authorization头信息
     * @param subdomainId 可选的子域名ID过滤
     * @param type 可选的记录类型过滤
     * @param status 可选的状态过滤
     * @return DNS解析记录列表
     */
    @GetMapping
    public ApiResponse<List<UserDnsRecord>> getDnsRecords(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Long subdomainId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        try {
            // 从Authorization头中提取token
            String token = authHeader.replace("Bearer ", "");
            
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            List<UserDnsRecord> records;
            
            if (subdomainId != null) {
                // 验证用户是否拥有该子域名
                UserSubdomain userSubdomain = userSubdomainService.getById(subdomainId);
                if (userSubdomain == null || !userSubdomain.getUserId().equals(userId)) {
                    return ApiResponse.error(403, "无权限访问该子域名的解析记录");
                }
                
                records = userDnsRecordService.getRecordsByUserIdAndSubdomainId(userId, subdomainId);
            } else {
                records = userDnsRecordService.getRecordsByUserId(userId);
            }
            
            // 根据类型和状态过滤
            if (type != null || status != null) {
                records = records.stream()
                        .filter(record -> type == null || type.equals(record.getType()))
                        .filter(record -> status == null || status.equals(record.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
            }
            
            log.info("用户 {} 查询DNS解析记录，共 {} 条", userId, records.size());
            return ApiResponse.success(records);
            
        } catch (Exception e) {
            log.error("获取DNS解析记录列表失败", e);
            return ApiResponse.error(500, "获取DNS解析记录列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的DNS解析记录列表（JSON格式）
     * 
     * @param request 查询请求参数（JSON格式）
     * @param authHeader Authorization头信息
     * @return DNS解析记录列表
     */
    @PostMapping("/query")
    public ApiResponse<List<UserDnsRecord>> getDnsRecordsJson(
            @Valid @RequestBody UserDnsRecordQueryRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 从Authorization头中提取token
            String token = authHeader.replace("Bearer ", "");
            
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            
            log.info("用户 {} ({}) 请求查询DNS解析记录: {}", userId, email, request);
            
            List<UserDnsRecord> records;
            
            if (request.getSubdomainId() != null) {
                // 验证用户是否拥有该子域名
                UserSubdomain userSubdomain = userSubdomainService.getById(request.getSubdomainId());
                if (userSubdomain == null || !userSubdomain.getUserId().equals(userId)) {
                    return ApiResponse.error(403, "无权限访问该子域名的解析记录");
                }
                
                records = userDnsRecordService.getRecordsByUserIdAndSubdomainId(userId, request.getSubdomainId());
            } else {
                records = userDnsRecordService.getRecordsByUserId(userId);
            }
            
            // 根据条件过滤
            records = records.stream()
                    .filter(record -> request.getType() == null || request.getType().equals(record.getType()))
                    .filter(record -> request.getStatus() == null || request.getStatus().equals(record.getStatus()))
                    .filter(record -> request.getSyncStatus() == null || request.getSyncStatus().equals(record.getSyncStatus()))
                    .collect(java.util.stream.Collectors.toList());
            
            // 分页处理
            int total = records.size();
            int offset = request.getOffset();
            int limit = request.getLimit();
            
            if (offset < total) {
                int endIndex = Math.min(offset + limit, total);
                records = records.subList(offset, endIndex);
            } else {
                records = java.util.Collections.emptyList();
            }
            
            log.info("用户 {} 查询DNS解析记录成功，共 {} 条（总计 {} 条）", userId, records.size(), total);
            return ApiResponse.success(records);
            
        } catch (Exception e) {
            log.error("获取DNS解析记录列表失败", e);
            return ApiResponse.error(500, "获取DNS解析记录列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取DNS解析记录详情
     * 
     * @param id 记录ID
     * @param authHeader Authorization头信息
     * @return DNS解析记录详情
     */
    @GetMapping("/{id}")
    public ApiResponse<UserDnsRecord> getDnsRecordById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 从Authorization头中提取token
            String token = authHeader.replace("Bearer ", "");
            
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            UserDnsRecord record = userDnsRecordService.getRecordById(id);
            if (record == null) {
                return ApiResponse.error(404, "DNS解析记录不存在");
            }
            
            if (!record.getUserId().equals(userId)) {
                return ApiResponse.error(403, "无权限访问该DNS解析记录");
            }
            
            return ApiResponse.success(record);
            
        } catch (Exception e) {
            log.error("获取DNS解析记录详情失败", e);
            return ApiResponse.error(500, "获取DNS解析记录详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除DNS解析记录
     * 
     * @param id 记录ID
     * @param authHeader Authorization头信息
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<Boolean> deleteDnsRecord(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 步骤1：用户身份验证(JWT)
            // 从Authorization头中提取token
            String token = authHeader.replace("Bearer ", "");
            
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            
            log.info("用户 {} ({}) 请求删除DNS解析记录: {}", userId, email, id);
            
            // 步骤2和3：检查记录是否存在且属于当前用户
            UserDnsRecord record = userDnsRecordService.getRecordById(id);
            if (record == null) {
                return ApiResponse.error(404, "DNS解析记录不存在");
            }
            
            if (!record.getUserId().equals(userId)) {
                return ApiResponse.error(403, "无权限删除该DNS解析记录");
            }
            
            // 步骤4：如果记录已同步到DNSPod，需要先从DNSPod删除
            if ("SUCCESS".equals(record.getSyncStatus()) && record.getRecordId() != null) {
                try {
                    UserSubdomain userSubdomain = userSubdomainService.getById(record.getSubdomainId());
                    if (userSubdomain != null) {
                        String[] domainParts = userSubdomain.getFullDomain().split("\\.", 2);
                        if (domainParts.length == 2) {
                            String mainDomain = domainParts[1];
                            dnspodService.deleteRecord(mainDomain, record.getRecordId(), null);
                            log.info("从DNSPod删除记录成功: recordId={}", record.getRecordId());
                        }
                    }
                } catch (Exception e) {
                    // 异常处理：记录警告日志，但继续执行本地删除
                    log.warn("从DNSPod删除记录失败，但继续删除本地记录: {}", e.getMessage());
                }
            }
            
            // 步骤5：删除本地记录
            boolean deleted = userDnsRecordService.deleteRecord(id);
            if (deleted) {
                log.info("用户 {} 删除DNS解析记录成功: recordId={}", userId, id);
                // 步骤6：返回操作结果
                return ApiResponse.success(true);
            } else {
                return ApiResponse.error(500, "删除DNS解析记录失败");
            }
            
        } catch (Exception e) {
            log.error("删除DNS解析记录失败", e);
            return ApiResponse.error(500, "删除DNS解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户DNS解析记录统计信息
     * 
     * @param authHeader Authorization头信息
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<DnsRecordStats> getDnsRecordStats(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 从Authorization头中提取token
            String token = authHeader.replace("Bearer ", "");
            
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            int totalRecords = userDnsRecordService.countRecordsByUserId(userId);
            List<UserDnsRecord> allRecords = userDnsRecordService.getRecordsByUserId(userId);
            
            long successRecords = allRecords.stream()
                    .filter(record -> "SUCCESS".equals(record.getSyncStatus()))
                    .count();
            
            long pendingRecords = allRecords.stream()
                    .filter(record -> "PENDING".equals(record.getSyncStatus()))
                    .count();
            
            long failedRecords = allRecords.stream()
                    .filter(record -> "FAILED".equals(record.getSyncStatus()))
                    .count();
            
            DnsRecordStats stats = new DnsRecordStats();
            stats.setTotalRecords(totalRecords);
            stats.setSuccessRecords((int) successRecords);
            stats.setPendingRecords((int) pendingRecords);
            stats.setFailedRecords((int) failedRecords);
            
            return ApiResponse.success(stats);
            
        } catch (Exception e) {
            log.error("获取DNS解析记录统计信息失败", e);
            return ApiResponse.error(500, "获取统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证记录值格式是否正确
     * 
     * @param type 记录类型
     * @param value 记录值
     * @return 是否有效
     */
    private boolean isValidRecordValue(String type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        switch (type.toUpperCase()) {
            case "A":
                return isValidIPv4(value);
            case "AAAA":
                return isValidIPv6(value);
            case "CNAME":
            case "MX":
            case "NS":
                return isValidDomain(value);
            case "TXT":
                return value.length() <= 255;
            default:
                return true; // 其他类型暂不验证
        }
    }
    
    /**
     * 验证IPv4地址格式
     */
    private boolean isValidIPv4(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证IPv6地址格式（简单验证）
     */
    private boolean isValidIPv6(String ip) {
        return ip.matches("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$") ||
               ip.matches("^::1$") || ip.matches("^::$");
    }
    
    /**
     * 验证域名格式
     */
    private boolean isValidDomain(String domain) {
        return domain.matches("^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)*$");
    }
    
    /**
     * DNS解析记录统计信息DTO
     */
    public static class DnsRecordStats {
        private int totalRecords;
        private int successRecords;
        private int pendingRecords;
        private int failedRecords;
        
        // Getter和Setter方法
        public int getTotalRecords() {
            return totalRecords;
        }
        
        public void setTotalRecords(int totalRecords) {
            this.totalRecords = totalRecords;
        }
        
        public int getSuccessRecords() {
            return successRecords;
        }
        
        public void setSuccessRecords(int successRecords) {
            this.successRecords = successRecords;
        }
        
        public int getPendingRecords() {
            return pendingRecords;
        }
        
        public void setPendingRecords(int pendingRecords) {
            this.pendingRecords = pendingRecords;
        }
        
        public int getFailedRecords() {
            return failedRecords;
        }
        
        public void setFailedRecords(int failedRecords) {
            this.failedRecords = failedRecords;
        }
    }
}