package com.example.demo.service;

import com.example.demo.dto.UserCloudflareZoneRegisterRequest;
import com.example.demo.dto.UserCloudflareZoneRegisterResponse;
import com.example.demo.dto.UserCloudflareZoneListResponse;
import com.example.demo.entity.UserCloudflareZone;

import java.util.List;

/**
 * 文件名：UserCloudflareZoneService.java
 * 功能：用户Cloudflare域名服务接口
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
public interface UserCloudflareZoneService {
    
    /**
     * 用户注册Cloudflare域名
     * 
     * @param userId 用户ID
     * @param request 注册请求
     * @return 注册响应
     */
    UserCloudflareZoneRegisterResponse registerZone(Long userId, UserCloudflareZoneRegisterRequest request);
    
    /**
     * 获取用户的Cloudflare域名列表
     * 
     * @param userId 用户ID
     * @return 域名列表响应
     */
    UserCloudflareZoneListResponse getUserZoneList(Long userId);
    
    /**
     * 获取可用的Cloudflare域名列表
     * 
     * @return 可用域名列表
     */
    List<UserCloudflareZoneListResponse.AvailableZoneInfo> getAvailableZones();
    
    /**
     * 根据ID获取用户域名记录
     * 
     * @param userId 用户ID
     * @param id 记录ID
     * @return 用户域名记录
     */
    UserCloudflareZone getUserZoneById(Long userId, Long id);
    
    /**
     * 注销用户域名
     * 
     * @param userId 用户ID
     * @param id 记录ID
     * @return 是否成功
     */
    boolean unregisterZone(Long userId, Long id);
    
    /**
     * 同步用户DNS记录数量
     * 
     * @param userId 用户ID
     * @param zoneId Zone ID
     * @return 是否成功
     */
    boolean syncDnsRecordCount(Long userId, String zoneId);
    
    /**
     * 检查用户是否已注册指定域名
     * 
     * @param userId 用户ID
     * @param zoneId Zone ID
     * @return 是否已注册
     */
    boolean isUserRegisteredZone(Long userId, String zoneId);
    
    /**
     * 获取用户所有域名列表
     * 
     * @param userId 用户ID
     * @return 用户域名列表
     */
    List<UserCloudflareZone> getUserZonesByUserId(Long userId);
    
    /**
     * 根据Zone ID获取用户域名记录
     * 
     * @param userId 用户ID
     * @param zoneId Zone ID
     * @return 用户域名记录
     */
    UserCloudflareZone getUserZoneByZoneId(Long userId, String zoneId);
}