package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.CreateDnsRecordRequest;
import com.example.demo.dto.CreateDnsRecordResponse;
import com.example.demo.dto.UserCloudflareDnsRecordRequest;
import com.example.demo.dto.UserCloudflareDnsRecordResponse;
import com.example.demo.entity.CloudflareDnsRecord;
import com.example.demo.entity.UserCloudflareZone;
import com.example.demo.service.CloudflareService;
import com.example.demo.service.CloudflareDnsRecordService;
import com.example.demo.service.UserCloudflareZoneService;
import com.example.demo.util.DnsRecordValidator;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件名：UserCloudflareDnsRecordController.java
 * 功能：用户Cloudflare DNS记录管理控制器
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/user/cloudflare/dns-records")
@RequiredArgsConstructor
public class UserCloudflareDnsRecordController {
    
    private final CloudflareService cloudflareService;
    private final CloudflareDnsRecordService cloudflareDnsRecordService;
    private final UserCloudflareZoneService userCloudflareZoneService;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户添加Cloudflare DNS记录
     * 用户在自己注册的域名下添加DNS记录
     */
    @PostMapping
    @Transactional
    public ApiResponse<UserCloudflareDnsRecordResponse> addDnsRecord(
            @Valid @RequestBody UserCloudflareDnsRecordRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            
            log.info("用户 {} ({}) 请求添加Cloudflare DNS记录: {}", userId, email, request);
            
            // 1. 验证用户域名权限
            UserCloudflareZone userZone = userCloudflareZoneService.getUserZoneById(userId, request.getUserDomainId());
            if (userZone == null) {
                return ApiResponse.error(404, "用户域名不存在或无权限访问");
            }
            
            if (!"ACTIVE".equals(userZone.getStatus())) {
                return ApiResponse.error(400, "域名状态异常，无法添加DNS记录");
            }
            
            // 2. 检查DNS记录数量限制
            if (userZone.getDnsRecordCount() >= userZone.getDnsRecordLimit()) {
                return ApiResponse.error(423, "DNS记录数量已达上限（" + userZone.getDnsRecordLimit() + "条），无法添加新记录");
            }
            
            // 3. 验证记录类型和值的格式
            DnsRecordValidator.ValidationResult validationResult = 
                DnsRecordValidator.validateRecordValue(request.getType(), request.getContent());
            if (!validationResult.isValid()) {
                return ApiResponse.error(400, "参数验证失败：" + validationResult.getErrorMessage());
            }
            
            // 4. 验证特殊记录类型的必需字段
            if ("SRV".equalsIgnoreCase(request.getType()) && !request.validateSrvRecord()) {
                return ApiResponse.error(400, "SRV记录缺少必需字段：priority、weight、port、service、proto、target");
            }
            
            if ("MX".equalsIgnoreCase(request.getType()) && !request.validateMxRecord()) {
                return ApiResponse.error(400, "MX记录缺少必需字段：priority");
            }
            
            // 5. 构建完整的DNS记录名称
            String fullName = request.getFullName(userZone.getFullSubdomain());
            
            // 6. 检查记录是否已存在（除NS记录外）
            List<CloudflareDnsRecord> existingRecords = cloudflareDnsRecordService
                .getLocalDnsRecordsByZoneId(userZone.getZoneId());
            
            boolean recordExists = existingRecords.stream()
                .anyMatch(record -> record.getName().equals(fullName) 
                    && record.getType().equals(request.getType())
                    && record.getUserId() != null 
                    && record.getUserId().equals(userId)
                    && record.getIsActive());
            
            if (recordExists && !"NS".equalsIgnoreCase(request.getType())) {
                return ApiResponse.error(409, "DNS记录已存在");
            }
            
            // 对于NS记录，检查是否已存在相同的记录值
            if ("NS".equalsIgnoreCase(request.getType())) {
                boolean sameValueExists = existingRecords.stream()
                    .anyMatch(record -> record.getName().equals(fullName) 
                        && record.getType().equals(request.getType())
                        && record.getContent().equals(request.getContent())
                        && record.getUserId() != null 
                        && record.getUserId().equals(userId)
                        && record.getIsActive());
                
                if (sameValueExists) {
                    return ApiResponse.error(409, "相同的NS记录已存在");
                }
            }
            
            // 7. 构建Cloudflare API请求
            CreateDnsRecordRequest cloudflareRequest = new CreateDnsRecordRequest();
            cloudflareRequest.setNamePrefix(fullName.replace("." + userZone.getZoneName(), ""));
            cloudflareRequest.setType(request.getType());
            cloudflareRequest.setContent(request.getContent());
            cloudflareRequest.setTtl(request.getTtl());
            cloudflareRequest.setProxied(request.getProxied());
            cloudflareRequest.setPriority(request.getPriority());
            cloudflareRequest.setWeight(request.getWeight());
            cloudflareRequest.setPort(request.getPort());
            cloudflareRequest.setService(request.getService());
            cloudflareRequest.setProto(request.getProto());
            cloudflareRequest.setTarget(request.getTarget());
            cloudflareRequest.setComment(request.getComment());
            
            // 8. 调用Cloudflare API创建DNS记录
            CreateDnsRecordResponse cloudflareResponse;
            try {
                log.info("调用Cloudflare API创建DNS记录: zoneId={}, name={}, type={}, content={}", 
                        userZone.getZoneId(), fullName, request.getType(), request.getContent());
                
                cloudflareResponse = cloudflareService.createDnsRecord(userZone.getZoneId(), cloudflareRequest);
                
                if (cloudflareResponse == null || !cloudflareResponse.isSuccess()) {
                    String errorMsg = cloudflareResponse != null ? cloudflareResponse.getErrorMessage() : "Cloudflare API返回异常";
                    log.error("Cloudflare API创建DNS记录失败: {}", errorMsg);
                    return ApiResponse.error(500, "创建DNS记录失败: " + errorMsg);
                }
                
                log.info("Cloudflare API创建DNS记录成功: recordId={}", cloudflareResponse.getRecordId());
                
            } catch (Exception e) {
                log.error("调用Cloudflare API失败", e);
                return ApiResponse.error(500, "创建DNS记录失败: " + e.getMessage());
            }
            
            // 9. 保存到本地数据库
            CloudflareDnsRecord localRecord = new CloudflareDnsRecord();
            localRecord.setZoneId(userZone.getZoneId());
            localRecord.setRecordId(cloudflareResponse.getRecordId());
            localRecord.setName(fullName);
            localRecord.setType(request.getType());
            localRecord.setContent(request.getContent());
            localRecord.setTtl(request.getTtl());
            localRecord.setProxied(request.getProxied() != null ? request.getProxied() : false);
            localRecord.setProxiable(cloudflareResponse.getProxiable() != null ? cloudflareResponse.getProxiable() : false);
            localRecord.setPriority(request.getPriority());
            localRecord.setWeight(request.getWeight());
            localRecord.setPort(request.getPort());
            localRecord.setComment(request.getComment());
            localRecord.setCreatedOn(LocalDateTime.now());
            localRecord.setModifiedOn(LocalDateTime.now());
            localRecord.setSyncStatus("SUCCESS");
            localRecord.setIsActive(true);
            localRecord.setUserId(userId);
            localRecord.setUserDomainId(request.getUserDomainId());
            
            boolean saved = cloudflareDnsRecordService.saveOrUpdateDnsRecord(localRecord);
            if (!saved) {
                log.error("保存DNS记录到本地数据库失败");
                return ApiResponse.error(500, "保存DNS记录失败");
            }
            
            // 10. 构建响应
            UserCloudflareDnsRecordResponse response = convertToResponse(localRecord, userZone);
            
            log.info("用户 {} 添加Cloudflare DNS记录成功: recordId={}", userId, cloudflareResponse.getRecordId());
            
            return ApiResponse.success("DNS记录添加成功", response);
            
        } catch (Exception e) {
            log.error("添加Cloudflare DNS记录异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 获取用户的Cloudflare DNS记录列表
     * 获取用户在指定域名下的DNS记录列表
     */
    @GetMapping
    public ApiResponse<List<UserCloudflareDnsRecordResponse>> getUserDnsRecords(
            @RequestParam(required = false) Long userDomainId,
            @RequestParam(required = false) String type,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 请求获取Cloudflare DNS记录列表: userDomainId={}, type={}", userId, userDomainId, type);
            
            List<CloudflareDnsRecord> records;
            
            if (userDomainId != null) {
                // 验证用户域名权限
                UserCloudflareZone userZone = userCloudflareZoneService.getUserZoneById(userId, userDomainId);
                if (userZone == null) {
                    return ApiResponse.error(404, "用户域名不存在或无权限访问");
                }
                
                // 获取指定域名下的用户DNS记录
                records = cloudflareDnsRecordService.getLocalDnsRecordsByZoneId(userZone.getZoneId())
                    .stream()
                    .filter(record -> record.getUserId() != null && record.getUserId().equals(userId))
                    .filter(record -> record.getUserDomainId() != null && record.getUserDomainId().equals(userDomainId))
                    .filter(record -> record.getIsActive())
                    .collect(Collectors.toList());
            } else {
                // 获取用户所有域名下的DNS记录
                List<UserCloudflareZone> userZones = userCloudflareZoneService.getUserZonesByUserId(userId);
                records = userZones.stream()
                    .flatMap(zone -> cloudflareDnsRecordService.getLocalDnsRecordsByZoneId(zone.getZoneId()).stream())
                    .filter(record -> record.getUserId() != null && record.getUserId().equals(userId))
                    .filter(record -> record.getIsActive())
                    .collect(Collectors.toList());
            }
            
            // 按类型过滤
            if (type != null && !type.trim().isEmpty()) {
                records = records.stream()
                    .filter(record -> type.equalsIgnoreCase(record.getType()))
                    .collect(Collectors.toList());
            }
            
            // 转换为响应DTO
            List<UserCloudflareDnsRecordResponse> responses = records.stream()
                .map(record -> {
                    UserCloudflareZone userZone = userCloudflareZoneService.getUserZoneByZoneId(userId, record.getZoneId());
                    return convertToResponse(record, userZone);
                })
                .collect(Collectors.toList());
            
            log.info("用户 {} 获取Cloudflare DNS记录列表成功，共 {} 条", userId, responses.size());
            
            return ApiResponse.success("获取DNS记录列表成功", responses);
            
        } catch (Exception e) {
            log.error("获取用户Cloudflare DNS记录列表异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 根据ID获取DNS记录详情
     */
    @GetMapping("/{recordId}")
    public ApiResponse<UserCloudflareDnsRecordResponse> getDnsRecordById(
            @PathVariable String recordId,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 请求获取DNS记录详情: recordId={}", userId, recordId);
            
            CloudflareDnsRecord record = cloudflareDnsRecordService.getLocalDnsRecordByRecordId(recordId);
            if (record == null) {
                return ApiResponse.error(404, "DNS记录不存在");
            }
            
            if (record.getUserId() == null || !record.getUserId().equals(userId)) {
                return ApiResponse.error(403, "无权限访问该DNS记录");
            }
            
            UserCloudflareZone userZone = userCloudflareZoneService.getUserZoneByZoneId(userId, record.getZoneId());
            UserCloudflareDnsRecordResponse response = convertToResponse(record, userZone);
            
            return ApiResponse.success("获取DNS记录详情成功", response);
            
        } catch (Exception e) {
            log.error("获取DNS记录详情异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 删除DNS记录
     */
    @PostMapping("/delete")
    @Transactional
    public ApiResponse<String> deleteDnsRecord(
            @RequestParam String recordId,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT令牌获取用户ID
            String token = jwtUtil.getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户 {} 请求删除DNS记录: recordId={}", userId, recordId);
            
            CloudflareDnsRecord record = cloudflareDnsRecordService.getLocalDnsRecordByRecordId(recordId);
            if (record == null) {
                return ApiResponse.error(404, "DNS记录不存在");
            }
            
            if (record.getUserId() == null || !record.getUserId().equals(userId)) {
                return ApiResponse.error(403, "无权限删除该DNS记录");
            }
            
            // 删除DNS记录（会同时删除Cloudflare和本地记录）
            boolean deleted = cloudflareDnsRecordService.deleteDnsRecord(recordId);
            if (deleted) {
                log.info("用户 {} 删除DNS记录成功: recordId={}", userId, recordId);
                return ApiResponse.success("DNS记录删除成功");
            } else {
                return ApiResponse.error(500, "删除DNS记录失败");
            }
            
        } catch (Exception e) {
            log.error("删除DNS记录异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    /**
     * 转换为响应DTO
     */
    private UserCloudflareDnsRecordResponse convertToResponse(CloudflareDnsRecord record, UserCloudflareZone userZone) {
        UserCloudflareDnsRecordResponse response = new UserCloudflareDnsRecordResponse();
        response.setId(record.getId());
        response.setRecordId(record.getRecordId());
        response.setUserDomainId(record.getUserDomainId());
        response.setUserSubdomain(userZone != null ? userZone.getFullSubdomain() : null);
        response.setName(record.getName());
        response.setType(record.getType());
        response.setContent(record.getContent());
        response.setTtl(record.getTtl());
        response.setProxied(record.getProxied());
        response.setProxiable(record.getProxiable());
        response.setPriority(record.getPriority());
        response.setWeight(record.getWeight());
        response.setPort(record.getPort());
        response.setComment(record.getComment());
        response.setSyncStatus(record.getSyncStatus());
        response.setSyncError(record.getSyncError());
        response.setIsActive(record.getIsActive());
        response.setCreatedOn(record.getCreatedOn());
        response.setModifiedOn(record.getModifiedOn());
        response.setCreateTime(record.getCreateTime());
        response.setUpdateTime(record.getUpdateTime());
        return response;
    }
}