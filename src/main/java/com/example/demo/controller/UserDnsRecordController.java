package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.UserDnsRecordRequest;
import com.example.demo.dto.UserDnsRecordUpdateRequest;
import com.example.demo.dto.UserDnsRecordQueryRequest;
import com.example.demo.dto.DeleteDnsRecordRequest;
import com.example.demo.entity.UserDnsRecord;
import com.example.demo.entity.UserSubdomain;
import com.example.demo.service.DnspodService;
import com.example.demo.service.UserDnsRecordService;
import com.example.demo.service.UserSubdomainService;
import com.example.demo.util.DnsRecordValidator;
import com.example.demo.util.JwtUtil;
import com.tencentcloudapi.dnspod.v20210323.models.CreateRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyRecordResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
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
                return ApiResponse.error(400, getRecordValueErrorMessage(request.getType(), request.getValue()));
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
     * @param request DNS解析记录更新请求
     * @param authHeader Authorization头信息
     * @return 修改结果
     */
    @PostMapping("/modify")
    @Transactional
    public ApiResponse<UserDnsRecord> updateDnsRecord(
            @Valid @RequestBody UserDnsRecordUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 从Authorization头中提取token
            String token = authHeader.replace("Bearer ", "");
            
            // 从token中获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            
            log.info("用户 {} ({}) 请求修改DNS解析记录 {}: {}", userId, email, request.getId(), request);
            
            // 1. 验证记录是否存在且属于当前用户
            UserDnsRecord record = userDnsRecordService.getRecordById(request.getId());
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
                        if (existingRecord.getId().equals(request.getId())) {
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
            
            // 使用新的校验工具类进行更严格的参数校验
            DnsRecordValidator.ValidationResult validationResult = DnsRecordValidator.validateRecordValue(recordType, recordValue);
            if (!validationResult.isValid()) {
                return ApiResponse.error(400, "参数验证失败：" + validationResult.getErrorMessage());
            }
            
            // 5. 调用DNSPod API修改记录（只有DNSPod修改成功后才更新本地数据库）
            // 构建完整域名（子域名.主域名）
            String fullDomain = userSubdomain.getFullDomain();
            String[] domainParts = fullDomain.split("\\.", 2);
            if (domainParts.length != 2) {
                return ApiResponse.error(500, "域名格式错误: " + fullDomain);
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
            
            // 确保recordId不为空
            if (record.getRecordId() == null) {
                return ApiResponse.error(400, "DNSPod记录ID为空，无法修改记录");
            }
            
            log.info("调用DNSPod API修改记录: domain={}, recordId={}, subDomain={}, type={}, value={}", 
                    mainDomain, record.getRecordId(), dnspodSubDomain, 
                    request.getType() != null ? request.getType() : record.getType(),
                    request.getValue() != null ? request.getValue() : record.getValue());
            
            ModifyRecordResponse dnspodResponse;
            try {
                // 安全处理Long类型参数，避免NullPointerException
                Long ttlValue = 600L; // 默认TTL值
                if (request.getTtl() != null) {
                    ttlValue = request.getTtl().longValue();
                } else if (record.getTtl() != null) {
                    ttlValue = record.getTtl().longValue();
                }
                
                Long mxValue = null;
                if (request.getMx() != null) {
                    mxValue = request.getMx().longValue();
                } else if (record.getMx() != null) {
                    mxValue = record.getMx().longValue();
                }
                
                Long weightValue = null;
                if (request.getWeight() != null) {
                    weightValue = request.getWeight().longValue();
                } else if (record.getWeight() != null) {
                    weightValue = record.getWeight().longValue();
                }
                
                dnspodResponse = dnspodService.modifyRecord(
                        mainDomain,
                        record.getRecordId(),
                        request.getType() != null ? request.getType() : record.getType(),
                        record.getLine(),
                        request.getValue() != null ? request.getValue() : record.getValue(),
                        dnspodSubDomain,
                        null, // domainId
                        ttlValue,
                        mxValue,
                        weightValue,
                        request.getStatus() != null ? request.getStatus() : record.getStatus(),
                        request.getRemark() != null ? request.getRemark() : record.getRemark()
                );
                
                // 检查DNSPod API调用结果
                if (dnspodResponse == null || dnspodResponse.getRecordId() == null) {
                    log.error("DNSPod API修改失败: 响应为空或记录ID为空");
                    return ApiResponse.error(500, "DNSPod API修改失败: 响应异常");
                }
                
                log.info("DNSPod API修改成功，recordId: {}", dnspodResponse.getRecordId());
                
            } catch (Exception e) {
                log.error("DNSPod API修改失败: {}", e.getMessage(), e);
                // DNSPod API失败，不更新本地数据库，直接返回错误信息
                return ApiResponse.error(500, "DNSPod API修改失败: " + e.getMessage());
            }
            
            // 6. 更新本地记录(只有DNSPod修改成功后才更新本地数据库)
            if (request.getType() != null) record.setType(request.getType());
            if (request.getValue() != null) record.setValue(request.getValue());
            if (request.getLine() != null) record.setLine(request.getLine());
            if (request.getTtl() != null) record.setTtl(request.getTtl());
            if (request.getMx() != null) record.setMx(request.getMx());
            if (request.getWeight() != null) record.setWeight(request.getWeight());
            if (request.getStatus() != null) record.setStatus(request.getStatus());
            if (request.getRemark() != null) record.setRemark(request.getRemark());
            
            // 更新同步状态为成功
            record.setSyncStatus("SUCCESS");
            record.setSyncError(null);
            record.setUpdateTime(LocalDateTime.now());
            
            // 保存到数据库
            UserDnsRecord updatedRecord = userDnsRecordService.updateRecord(record);
            
            log.info("DNS解析记录修改成功: recordId={}, dnspodRecordId={}", 
                    request.getId(), dnspodResponse.getRecordId());
            
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
     * 删除DNS解析记录（POST方式，JSON格式）
     * 按照业务流程文档实现的标准删除接口
     * 
     * @param request 删除请求参数
     * @param authHeader Authorization头信息
     * @return 删除结果
     */
    @PostMapping("/delete")
    @Transactional
    public ApiResponse<Boolean> deleteDnsRecordPost(
            @Valid @RequestBody DeleteDnsRecordRequest request,
            @RequestHeader("Authorization") String authHeader) {
        
        log.info("开始执行DNS解析记录删除业务流程，记录ID: {}", request.getRecordId());
        
        // 步骤1：参数验证
        if (request.getRecordId() == null || request.getRecordId() <= 0) {
            log.warn("删除DNS记录失败：记录ID无效 - {}", request.getRecordId());
            return ApiResponse.error(400, "记录ID无效");
        }
        
        return deleteDnsRecordInternal(request.getRecordId(), authHeader);
    }
    
    /**
     * 删除DNS解析记录（DELETE方式，路径参数）
     * 已废弃，请使用POST /delete接口
     * 
     * @param id 记录ID
     * @param authHeader Authorization头信息
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Transactional
    @Deprecated
    public ApiResponse<Boolean> deleteDnsRecord(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        return deleteDnsRecordInternal(id, authHeader);
    }
    
    /**
     * 删除DNS解析记录的内部实现
     * 严格按照业务流程文档执行
     * 
     * @param recordId 记录ID
     * @param authHeader Authorization头信息
     * @return 删除结果
     */
    private ApiResponse<Boolean> deleteDnsRecordInternal(
            Long recordId,
            String authHeader) {
        try {
            log.info("=== 开始DNS解析记录删除业务流程 ===");
            
            // 步骤1：用户身份验证(JWT)
            log.info("步骤1：执行用户身份验证");
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            log.info("用户身份验证成功：userId={}, email={}", userId, email);
            
            // 步骤2：参数验证和业务规则检查
            log.info("步骤2：执行参数验证和业务规则检查");
            if (recordId == null || recordId <= 0) {
                log.warn("参数验证失败：记录ID无效 - {}", recordId);
                return ApiResponse.error(400, "记录ID无效");
            }
            log.info("参数验证通过：recordId={}", recordId);
            
            // 步骤3：检查记录是否存在且属于当前用户
            log.info("步骤3：检查记录存在性和用户权限");
            UserDnsRecord record = userDnsRecordService.getRecordById(recordId);
            if (record == null) {
                log.warn("记录不存在：recordId={}", recordId);
                return ApiResponse.error(404, "DNS解析记录不存在");
            }
            
            if (!record.getUserId().equals(userId)) {
                log.warn("权限验证失败：用户{}尝试删除不属于自己的记录{}", userId, recordId);
                return ApiResponse.error(403, "无权限删除该DNS解析记录");
            }
            
            log.info("记录存在性和权限验证通过：recordId={}, userId={}, recordType={}, recordValue={}", 
                    recordId, userId, record.getType(), record.getValue());
            
            // 步骤4：从DNSPod删除记录
            log.info("步骤4：从DNSPod删除记录");
            if ("SUCCESS".equals(record.getSyncStatus()) && record.getRecordId() != null) {
                try {
                    UserSubdomain userSubdomain = userSubdomainService.getById(record.getSubdomainId());
                    if (userSubdomain != null) {
                        String[] domainParts = userSubdomain.getFullDomain().split("\\.", 2);
                        if (domainParts.length == 2) {
                            String mainDomain = domainParts[1];
                            log.info("调用DNSPod API删除记录：domain={}, dnspodRecordId={}", 
                                    mainDomain, record.getRecordId());
                            dnspodService.deleteRecord(mainDomain, record.getRecordId(), null);
                            log.info("从DNSPod删除记录成功：dnspodRecordId={}", record.getRecordId());
                        } else {
                            log.warn("域名格式异常，跳过DNSPod删除：fullDomain={}", userSubdomain.getFullDomain());
                        }
                    } else {
                        log.warn("子域名不存在，跳过DNSPod删除：subdomainId={}", record.getSubdomainId());
                    }
                } catch (Exception e) {
                    log.warn("从DNSPod删除记录失败，但继续删除本地记录：{}", e.getMessage(), e);
                }
            } else {
                log.info("记录未同步到DNSPod或状态异常，跳过DNSPod删除：syncStatus={}, dnspodRecordId={}", 
                        record.getSyncStatus(), record.getRecordId());
            }
            
            // 步骤5：删除本地数据库记录
            log.info("步骤5：删除本地数据库记录");
            boolean deleted = userDnsRecordService.deleteRecord(recordId);
            if (deleted) {
                log.info("本地DNS记录删除成功：recordId={}", recordId);
                
                // 步骤6：返回操作结果
                log.info("步骤6：返回操作成功结果");
                log.info("=== DNS解析记录删除业务流程完成 ===");
                return ApiResponse.success("DNS解析记录删除成功", true);
            } else {
                log.error("本地DNS记录删除失败：recordId={}", recordId);
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
        
        // 去除首尾空格
        value = value.trim();
        
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
     * 严格验证IP地址格式，拒绝无效格式如 "a.1.1.1"、"new s.1.1.1" 或域名格式
     */
    private boolean isValidIPv4(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        
        ip = ip.trim();
        
        // 严格检查：只允许数字和点，不允许字母或其他字符
        if (!ip.matches("^[0-9.]+$")) {
            return false;
        }
        
        // 检查是否以点开头或结尾
        if (ip.startsWith(".") || ip.endsWith(".")) {
            return false;
        }
        
        // 检查是否包含连续的点
        if (ip.contains("..")) {
            return false;
        }
        
        // 检查是否包含空格（防止类似 "1.1.1. 1" 的情况）
        if (ip.contains(" ")) {
            return false;
        }
        
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        
        try {
            for (String part : parts) {
                // 检查每个部分是否为空
                if (part.isEmpty()) {
                    return false;
                }
                
                // 检查是否包含非数字字符
                if (!part.matches("^[0-9]+$")) {
                    return false;
                }
                
                // 检查是否有前导零（除了单独的0）
                if (part.length() > 1 && part.startsWith("0")) {
                    return false;
                }
                
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
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
     * 严格验证域名格式，确保符合DNS规范
     */
    private boolean isValidDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }
        
        domain = domain.trim();
        
        // 域名长度不能超过253个字符
        if (domain.length() > 253) {
            return false;
        }
        
        // 域名不能以点开头或结尾
        if (domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }
        
        // 检查是否包含连续的点
        if (domain.contains("..")) {
            return false;
        }
        
        // 分割域名各部分进行验证
        String[] labels = domain.split("\\.");
        
        // 至少要有一个标签
        if (labels.length == 0) {
            return false;
        }
        
        for (String label : labels) {
            // 每个标签不能为空
            if (label.isEmpty()) {
                return false;
            }
            
            // 每个标签长度不能超过63个字符
            if (label.length() > 63) {
                return false;
            }
            
            // 标签不能以连字符开头或结尾
            if (label.startsWith("-") || label.endsWith("-")) {
                return false;
            }
            
            // 标签只能包含字母、数字和连字符
            if (!label.matches("^[a-zA-Z0-9-]+$")) {
                return false;
            }
        }
        
        // 顶级域名必须至少包含一个字母
        String tld = labels[labels.length - 1];
        if (!tld.matches(".*[a-zA-Z].*")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 根据记录类型和值生成详细的错误消息
     * 
     * @param type 记录类型
     * @param value 记录值
     * @return 详细的错误消息
     */
    private String getRecordValueErrorMessage(String type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return "记录值不能为空";
        }
        
        value = value.trim();
        
        switch (type.toUpperCase()) {
            case "A":
                return getIPv4ValidationError(value);
            case "AAAA":
                return "AAAA记录值必须是有效的IPv6地址格式（如：2001:db8::1），当前值：" + value;
            case "CNAME":
                return "CNAME记录值必须是有效的域名格式（如：example.com），当前值：" + value;
            case "MX":
                return "MX记录值必须是有效的域名格式（如：mail.example.com），当前值：" + value;
            case "NS":
                return "NS记录值必须是有效的域名格式（如：ns1.example.com），当前值：" + value;
            case "TXT":
                return "TXT记录值长度不能超过255个字符，当前长度：" + value.length();
            default:
                return "记录值格式不正确，当前值：" + value;
        }
    }
    
    /**
     * 获取IPv4地址验证的详细错误信息
     * 
     * @param ip IP地址字符串
     * @return 详细的错误信息
     */
    private String getIPv4ValidationError(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return "A记录值不能为空";
        }
        
        ip = ip.trim();
        
        // 检查是否包含字母或其他非法字符
        if (!ip.matches("^[0-9.]+$")) {
            return "A记录值只能包含数字和点，不能包含字母或其他字符。正确格式如：192.168.1.1，当前值：" + ip;
        }
        
        // 检查是否以点开头或结尾
        if (ip.startsWith(".") || ip.endsWith(".")) {
            return "A记录值不能以点开头或结尾。正确格式如：192.168.1.1，当前值：" + ip;
        }
        
        // 检查是否包含连续的点
        if (ip.contains("..")) {
            return "A记录值不能包含连续的点。正确格式如：192.168.1.1，当前值：" + ip;
        }
        
        // 检查是否包含空格
        if (ip.contains(" ")) {
            return "A记录值不能包含空格。正确格式如：192.168.1.1，当前值：" + ip;
        }
        
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return "A记录值必须包含4个数字段，用点分隔。正确格式如：192.168.1.1，当前值：" + ip;
        }
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            // 检查每个部分是否为空
            if (part.isEmpty()) {
                return "A记录值的第" + (i + 1) + "段不能为空。正确格式如：192.168.1.1，当前值：" + ip;
            }
            
            // 检查是否包含非数字字符
            if (!part.matches("^[0-9]+$")) {
                return "A记录值的第" + (i + 1) + "段只能包含数字。正确格式如：192.168.1.1，当前值：" + ip;
            }
            
            // 检查是否有前导零（除了单独的0）
            if (part.length() > 1 && part.startsWith("0")) {
                return "A记录值的第" + (i + 1) + "段不能有前导零。正确格式如：192.168.1.1，当前值：" + ip;
            }
            
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return "A记录值的第" + (i + 1) + "段必须在0-255范围内。正确格式如：192.168.1.1，当前值：" + ip;
                }
            } catch (NumberFormatException e) {
                return "A记录值的第" + (i + 1) + "段不是有效数字。正确格式如：192.168.1.1，当前值：" + ip;
            }
        }
        
        return "A记录值格式不正确。正确格式如：192.168.1.1，当前值：" + ip;
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