package com.example.demo.service.impl;

import com.example.demo.dto.UserCloudflareZoneRegisterRequest;
import com.example.demo.dto.UserCloudflareZoneRegisterResponse;
import com.example.demo.dto.UserCloudflareZoneListResponse;
import com.example.demo.dto.SimpleCloudflareZoneResponse;
import com.example.demo.entity.CloudflareZone;
import com.example.demo.entity.UserCloudflareZone;
import com.example.demo.mapper.CloudflareZoneMapper;
import com.example.demo.mapper.UserCloudflareZoneMapper;
import com.example.demo.service.UserCloudflareZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件名：UserCloudflareZoneServiceImpl.java
 * 功能：用户Cloudflare域名服务实现类
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCloudflareZoneServiceImpl implements UserCloudflareZoneService {
    
    private final UserCloudflareZoneMapper userCloudflareZoneMapper;
    private final CloudflareZoneMapper cloudflareZoneMapper;
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * 用户注册Cloudflare域名
     */
    @Override
    @Transactional
    public UserCloudflareZoneRegisterResponse registerZone(Long userId, UserCloudflareZoneRegisterRequest request) {
        log.info("用户 {} 尝试注册Cloudflare域名: {}", userId, request.getZoneId());
        
        UserCloudflareZoneRegisterResponse response = new UserCloudflareZoneRegisterResponse();
        
        try {
            // 检查用户可注册域名数量限制
            Integer userDomainLimit = jdbcTemplate.queryForObject(
                "SELECT fn_check_user_domain_limit(?)", Integer.class, userId);
            
            if (userDomainLimit == null || userDomainLimit <= 0) {
                response.setResultCode(403);
                response.setResultMessage("您的可注册域名数量已用完，无法注册新域名");
                log.warn("用户 {} 可注册域名数量不足: {}", userId, userDomainLimit);
                return response;
            }
            
            // 检查用户是否已注册该域名
            UserCloudflareZone existingZone = userCloudflareZoneMapper.findByUserIdAndZoneId(userId, request.getZoneId());
            if (existingZone != null) {
                response.setResultCode(409);
                response.setResultMessage("您已注册过该域名");
                return response;
            }
            
            // 检查域名是否存在且可用
            CloudflareZone zone = cloudflareZoneMapper.findByZoneId(request.getZoneId());
            if (zone == null || !zone.getIsActive()) {
                response.setResultCode(404);
                response.setResultMessage("域名不存在或已禁用");
                return response;
            }
            
            // 调用存储过程进行域名注册
            String sql = "CALL register_user_cloudflare_domain(?, ?, @result_code, @result_message, @assigned_prefix, @full_subdomain)";
            jdbcTemplate.update(sql, userId, request.getZoneId());
            
            // 获取存储过程执行结果
            Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT @result_code as result_code, @result_message as result_message, " +
                "@assigned_prefix as assigned_prefix, @full_subdomain as full_subdomain"
            );
            
            Integer resultCode = ((Number) result.get("result_code")).intValue();
            String resultMessage = (String) result.get("result_message");
            String assignedPrefix = (String) result.get("assigned_prefix");
            String fullSubdomain = (String) result.get("full_subdomain");
            
            response.setResultCode(resultCode);
            response.setResultMessage(resultMessage);
            
            if (resultCode == 201) {
                // 注册成功，减少用户可注册域名数量
                jdbcTemplate.update("UPDATE user SET dom_num = dom_num - 1 WHERE id = ? AND dom_num > 0", userId);
                
                // 查询注册记录详情
                UserCloudflareZone userZone = userCloudflareZoneMapper.findByUserIdAndZoneId(userId, request.getZoneId());
                if (userZone != null) {
                    response.setId(userZone.getId());
                    response.setUserId(userZone.getUserId());
                    response.setZoneId(userZone.getZoneId());
                    response.setZoneName(userZone.getZoneName());
                    response.setAssignedPrefix(userZone.getAssignedPrefix());
                    response.setFullSubdomain(userZone.getFullSubdomain());
                    response.setStatus(userZone.getStatus());
                    response.setDnsRecordCount(userZone.getDnsRecordCount());
                    response.setDnsRecordLimit(userZone.getDnsRecordLimit());
                    response.setRemark(userZone.getRemark());
                    response.setAssignedTime(userZone.getAssignedTime());
                }
                
                log.info("用户 {} 成功注册Cloudflare域名: {}, 分配前缀: {}, 完整子域名: {}, 剩余可注册数量已减1", 
                    userId, request.getZoneId(), assignedPrefix, fullSubdomain);
            } else {
                log.warn("用户 {} 注册Cloudflare域名失败: {}, 错误: {}", userId, request.getZoneId(), resultMessage);
            }
            
        } catch (Exception e) {
            log.error("用户 {} 注册Cloudflare域名异常: {}", userId, request.getZoneId(), e);
            response.setResultCode(500);
            response.setResultMessage("系统错误，请稍后重试");
        }
        
        return response;
    }
    
    /**
     * 获取用户的Cloudflare域名列表
     */
    @Override
    public UserCloudflareZoneListResponse getUserZoneList(Long userId) {
        log.info("获取用户 {} 的Cloudflare域名列表", userId);
        
        UserCloudflareZoneListResponse response = new UserCloudflareZoneListResponse();
        
        try {
            // 获取用户已注册的域名
            List<UserCloudflareZone> userZones = userCloudflareZoneMapper.findByUserId(userId);
            List<UserCloudflareZoneListResponse.UserZoneInfo> userZoneInfos = new ArrayList<>();
            
            for (UserCloudflareZone userZone : userZones) {
                UserCloudflareZoneListResponse.UserZoneInfo info = new UserCloudflareZoneListResponse.UserZoneInfo();
                info.setId(userZone.getId());
                info.setZoneId(userZone.getZoneId());
                info.setZoneName(userZone.getZoneName());
                info.setAssignedPrefix(userZone.getAssignedPrefix());
                info.setFullSubdomain(userZone.getFullSubdomain());
                info.setStatus(userZone.getStatus());
                info.setDnsRecordCount(userZone.getDnsRecordCount());
                info.setDnsRecordLimit(userZone.getDnsRecordLimit());
                info.setAssignedTime(userZone.getAssignedTime());
                
                // 计算DNS使用率
                if (userZone.getDnsRecordLimit() > 0) {
                    double usagePercent = (double) userZone.getDnsRecordCount() / userZone.getDnsRecordLimit() * 100;
                    info.setDnsUsagePercent(Math.round(usagePercent * 100.0) / 100.0);
                } else {
                    info.setDnsUsagePercent(0.0);
                }
                
                userZoneInfos.add(info);
            }
            
            response.setUserZones(userZoneInfos);
            
            // 获取可用的域名列表
            response.setAvailableZones(getAvailableZones());
            
        } catch (Exception e) {
            log.error("获取用户 {} 的Cloudflare域名列表异常", userId, e);
            response.setUserZones(new ArrayList<>());
            response.setAvailableZones(new ArrayList<>());
        }
        
        return response;
    }
    
    /**
     * 获取可用的Cloudflare域名列表
     */
    @Override
    public List<UserCloudflareZoneListResponse.AvailableZoneInfo> getAvailableZones() {
        log.info("获取可用的Cloudflare域名列表");
        
        List<UserCloudflareZoneListResponse.AvailableZoneInfo> availableZones = new ArrayList<>();
        
        try {
            // 查询可用域名统计信息
            String sql = "SELECT * FROM v_cloudflare_zone_stats WHERE auto_assign_enabled = 1 AND is_full = 0 ORDER BY user_usage_percent ASC";
            List<Map<String, Object>> zoneStats = jdbcTemplate.queryForList(sql);
            
            for (Map<String, Object> stat : zoneStats) {
                UserCloudflareZoneListResponse.AvailableZoneInfo info = new UserCloudflareZoneListResponse.AvailableZoneInfo();
                info.setZoneId((String) stat.get("zone_id"));
                info.setZoneName((String) stat.get("zone_name"));
                info.setStatus((String) stat.get("zone_status"));
                info.setUserCount((Integer) stat.get("user_count"));
                info.setUserLimit((Integer) stat.get("user_limit"));
                info.setDnsRecordCount((Integer) stat.get("dns_record_count"));
                info.setDnsRecordLimit((Integer) stat.get("dns_record_limit"));
                info.setIsFull((Boolean) stat.get("is_full"));
                info.setAutoAssignEnabled((Boolean) stat.get("auto_assign_enabled"));
                info.setAvailablePrefixes((Integer) stat.get("available_prefixes"));
                
                // 计算使用率
                Object userUsagePercent = stat.get("user_usage_percent");
                if (userUsagePercent != null) {
                    info.setUserUsagePercent(((Number) userUsagePercent).doubleValue());
                } else {
                    info.setUserUsagePercent(0.0);
                }
                
                Object dnsUsagePercent = stat.get("dns_usage_percent");
                if (dnsUsagePercent != null) {
                    info.setDnsUsagePercent(((Number) dnsUsagePercent).doubleValue());
                } else {
                    info.setDnsUsagePercent(0.0);
                }
                
                availableZones.add(info);
            }
            
        } catch (Exception e) {
            log.error("获取可用Cloudflare域名列表异常", e);
        }
        
        return availableZones;
    }
    
    /**
     * 根据ID获取用户域名记录
     */
    @Override
    public UserCloudflareZone getUserZoneById(Long userId, Long id) {
        log.info("获取用户 {} 的域名记录: {}", userId, id);
        
        try {
            UserCloudflareZone userZone = userCloudflareZoneMapper.findByIdAndUserId(id, userId);
            if (userZone != null) {
                return userZone;
            }
        } catch (Exception e) {
            log.error("获取用户 {} 的域名记录 {} 异常", userId, id, e);
        }
        
        return null;
    }
    
    /**
     * 注销用户域名
     */
    @Override
    @Transactional
    public boolean unregisterZone(Long userId, Long id) {
        log.info("用户 {} 尝试注销域名记录: {}", userId, id);
        
        try {
            UserCloudflareZone userZone = userCloudflareZoneMapper.findByIdAndUserId(id, userId);
            if (userZone == null) {
                log.warn("用户 {} 尝试注销不存在或不属于自己的域名记录: {}", userId, id);
                return false;
            }
            
            // 检查是否有DNS记录
            int dnsRecordCount = userCloudflareZoneMapper.countUserDnsRecords(userId, userZone.getZoneId());
            if (dnsRecordCount > 0) {
                log.warn("用户 {} 的域名 {} 还有 {} 条DNS记录，无法注销", userId, userZone.getZoneId(), dnsRecordCount);
                return false;
            }
            
            // 软删除域名记录
            int result = userCloudflareZoneMapper.unregisterZone(id, userId);
            if (result > 0) {
                // 注销成功，恢复用户可注册域名数量
                jdbcTemplate.update("UPDATE user SET dom_num = dom_num + 1 WHERE id = ?", userId);
                log.info("用户 {} 成功注销域名记录: {}，可注册域名数量已恢复+1", userId, id);
                return true;
            }
            
        } catch (Exception e) {
            log.error("用户 {} 注销域名记录 {} 异常", userId, id, e);
        }
        
        return false;
    }
    
    /**
     * 同步用户DNS记录数量
     */
    @Override
    public boolean syncDnsRecordCount(Long userId, String zoneId) {
        log.info("同步用户 {} 在域名 {} 的DNS记录数量", userId, zoneId);
        
        try {
            UserCloudflareZone userZone = userCloudflareZoneMapper.findByUserIdAndZoneId(userId, zoneId);
            if (userZone == null) {
                log.warn("用户 {} 未注册域名: {}", userId, zoneId);
                return false;
            }
            
            int actualCount = userCloudflareZoneMapper.countUserDnsRecords(userId, zoneId);
            if (actualCount != userZone.getDnsRecordCount()) {
                userCloudflareZoneMapper.updateDnsRecordCount(userId, zoneId, actualCount);
                log.info("用户 {} 在域名 {} 的DNS记录数量已同步: {} -> {}", userId, zoneId, userZone.getDnsRecordCount(), actualCount);
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("同步用户 {} 在域名 {} 的DNS记录数量异常", userId, zoneId, e);
            return false;
        }
    }
    
    /**
     * 检查用户是否已注册指定域名
     */
    @Override
    public boolean isUserRegisteredZone(Long userId, String zoneId) {
        try {
            UserCloudflareZone userZone = userCloudflareZoneMapper.findByUserIdAndZoneId(userId, zoneId);
            return userZone != null;
        } catch (Exception e) {
            log.error("检查用户 {} 是否已注册域名 {} 异常", userId, zoneId, e);
            return false;
        }
    }
    
    /**
     * 获取用户所有域名列表
     */
    @Override
    public List<UserCloudflareZone> getUserZonesByUserId(Long userId) {
        log.info("获取用户 {} 的所有域名列表", userId);
        
        try {
            return userCloudflareZoneMapper.findByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户 {} 的所有域名列表异常", userId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 根据Zone ID获取用户域名记录
     */
    @Override
    public UserCloudflareZone getUserZoneByZoneId(Long userId, String zoneId) {
        log.info("获取用户 {} 在域名 {} 的记录", userId, zoneId);
        
        try {
            return userCloudflareZoneMapper.findByUserIdAndZoneId(userId, zoneId);
        } catch (Exception e) {
            log.error("获取用户 {} 在域名 {} 的记录异常", userId, zoneId, e);
            return null;
        }
    }
    
    /**
     * 获取用户Cloudflare域名注册统计信息
     */
    @Override
    public UserCloudflareZoneListResponse.UserDomainStats getUserDomainStats(Long userId) {
        log.info("获取用户 {} 的Cloudflare域名注册统计信息", userId);
        
        UserCloudflareZoneListResponse.UserDomainStats stats = new UserCloudflareZoneListResponse.UserDomainStats();
        
        try {
            // 获取用户可注册域名数量
            Integer availableDomains = jdbcTemplate.queryForObject(
                "SELECT dom_num FROM user WHERE id = ?", Integer.class, userId);
            stats.setAvailableDomains(availableDomains != null ? availableDomains : 0);
            
            // 获取用户已注册的Cloudflare域名数量
            Integer registeredCloudflareZones = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_cloudflare_domain WHERE user_id = ? AND status = 'ACTIVE'", 
                Integer.class, userId);
            stats.setRegisteredCloudflareZones(registeredCloudflareZones != null ? registeredCloudflareZones : 0);
            
            // 获取用户已注册的DNSPod域名数量（从user_subdomain表）
            Integer registeredDnspodDomains = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_subdomain WHERE user_id = ? AND status = 'ACTIVE'", 
                Integer.class, userId);
            stats.setRegisteredDnspodDomains(registeredDnspodDomains != null ? registeredDnspodDomains : 0);
            
            // 计算总已注册域名数量
            stats.setTotalRegisteredDomains(stats.getRegisteredCloudflareZones() + stats.getRegisteredDnspodDomains());
            
            // 判断注册状态
            if (stats.getAvailableDomains() > 0) {
                stats.setRegistrationStatus("CAN_REGISTER");
            } else {
                stats.setRegistrationStatus("LIMIT_REACHED");
            }
            
            log.info("用户 {} 域名统计: 可注册={}, Cloudflare已注册={}, DNSPod已注册={}, 总已注册={}", 
                userId, stats.getAvailableDomains(), stats.getRegisteredCloudflareZones(), 
                stats.getRegisteredDnspodDomains(), stats.getTotalRegisteredDomains());
            
        } catch (Exception e) {
            log.error("获取用户 {} 的域名统计信息异常", userId, e);
            // 设置默认值
            stats.setAvailableDomains(0);
            stats.setRegisteredCloudflareZones(0);
            stats.setRegisteredDnspodDomains(0);
            stats.setTotalRegisteredDomains(0);
            stats.setRegistrationStatus("LIMIT_REACHED");
        }
        
        return stats;
    }
    
    /**
     * 获取所有Cloudflare域名信息
     * 从数据库获取所有可用的Cloudflare域名列表
     */
    @Override
    public List<SimpleCloudflareZoneResponse> getAllCloudflareZones() {
        try {
            log.info("开始获取所有Cloudflare域名信息");
            
            // 从数据库获取所有活跃的域名
            List<CloudflareZone> zones = cloudflareZoneMapper.findAllActive();
            
            if (zones == null || zones.isEmpty()) {
                log.warn("数据库中没有找到任何活跃的Cloudflare域名");
                return new ArrayList<>();
            }
            
            // 转换为SimpleCloudflareZoneResponse
            List<SimpleCloudflareZoneResponse> result = new ArrayList<>();
            for (CloudflareZone zone : zones) {
                SimpleCloudflareZoneResponse response = convertToSimpleResponse(zone);
                result.add(response);
            }
            
            log.info("成功获取 {} 个Cloudflare域名信息", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("获取所有Cloudflare域名信息失败", e);
            throw new RuntimeException("获取域名信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将CloudflareZone实体转换为SimpleCloudflareZoneResponse
     * 
     * @param zone 域名实体
     * @return 简化的响应DTO
     */
    private SimpleCloudflareZoneResponse convertToSimpleResponse(CloudflareZone zone) {
        SimpleCloudflareZoneResponse response = new SimpleCloudflareZoneResponse();
        
        response.setZoneId(zone.getZoneId());
        response.setName(zone.getName());
        response.setStatus(zone.getStatus());
        response.setPaused(zone.getPaused());
        response.setType(zone.getType());
        response.setNameServers(zone.getNameServers());
        response.setCreatedOn(zone.getCreatedOn());
        response.setModifiedOn(zone.getModifiedOn());
        response.setDnsRecordCount(zone.getDnsRecordCount());
        response.setDnsRecordLimit(zone.getDnsRecordLimit());
        response.setUserCount(zone.getUserCount());
        response.setUserLimit(zone.getUserLimit());
        response.setIsFull(zone.getIsFull());
        response.setAutoAssignEnabled(zone.getAutoAssignEnabled());
        response.setNextPrefixHex(zone.getNextPrefixHex());
        response.setIsActive(zone.getIsActive());
        response.setRemark(zone.getRemark());
        response.setCreateTime(zone.getCreateTime());
        response.setUpdateTime(zone.getUpdateTime());
        
        return response;
    }
}