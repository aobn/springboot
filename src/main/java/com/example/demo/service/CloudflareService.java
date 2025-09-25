package com.example.demo.service;

import com.example.demo.dto.CloudflareZoneResponse;
import java.util.List;

/**
 * Cloudflare DNS服务接口
 * 提供Cloudflare API相关功能
 * 
 * @author CodeBuddy
 * @since 2025-09-25
 */
public interface CloudflareService {
    
    /**
     * 获取所有域名(Zone)列表
     * 
     * @return 域名列表
     * @throws Exception 调用API异常
     */
    CloudflareZoneResponse getAllZones() throws Exception;
    
    /**
     * 根据域名名称获取Zone信息
     * 
     * @param zoneName 域名名称
     * @return Zone信息
     * @throws Exception 调用API异常
     */
    CloudflareZoneResponse.Zone getZoneByName(String zoneName) throws Exception;
}