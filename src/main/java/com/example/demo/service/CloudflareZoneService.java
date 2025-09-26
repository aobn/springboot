package com.example.demo.service;

import com.example.demo.entity.CloudflareZone;
import com.example.demo.dto.SimpleCloudflareZoneResponse;
import com.example.demo.dto.CloudflareZoneResponse;
import java.util.List;

/**
 * 文件名：CloudflareZoneService.java
 * 功能：Cloudflare域名数据服务接口
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
public interface CloudflareZoneService {
    
    /**
     * 同步Cloudflare域名数据到本地数据库
     * 
     * @return 同步的域名数量
     */
    int syncZonesFromCloudflare();
    
    /**
     * 获取所有本地存储的Cloudflare域名
     * 
     * @return 域名列表
     */
    List<CloudflareZone> getAllLocalZones();
    
    /**
     * 根据域名名称获取本地存储的域名信息
     * 
     * @param zoneName 域名名称
     * @return 域名信息
     */
    CloudflareZone getLocalZoneByName(String zoneName);
    
    /**
     * 根据Cloudflare Zone ID获取本地存储的域名信息
     * 
     * @param zoneId Cloudflare Zone ID
     * @return 域名信息
     */
    CloudflareZone getLocalZoneById(String zoneId);
    
    /**
     * 保存或更新域名信息
     * 
     * @param zone 域名信息
     * @return 保存结果
     */
    boolean saveOrUpdateZone(CloudflareZone zone);
    
    /**
     * 批量保存或更新域名信息
     * 
     * @param zones 域名列表
     * @return 保存的数量
     */
    int batchSaveOrUpdateZones(List<CloudflareZone> zones);
    
    /**
     * 删除域名信息
     * 
     * @param zoneId Cloudflare Zone ID
     * @return 删除结果
     */
    boolean deleteZone(String zoneId);
    
    /**
     * 获取所有简化的Cloudflare域名信息（用于公开接口）
     * 
     * @return 简化的域名列表
     */
    List<SimpleCloudflareZoneResponse> getAllSimpleZones();
    
    /**
     * 将Cloudflare API响应转换为本地实体
     * 
     * @param apiZone API响应的域名信息
     * @return 本地实体
     */
    CloudflareZone convertToEntity(CloudflareZoneResponse.Zone apiZone);
}