package com.example.demo.service;

import java.util.Map;

/**
 * 管理员Cloudflare域名管理服务接口
 */
public interface AdminCloudflareZoneService {

    /**
     * 初始化域名前缀池
     * @param zoneId Cloudflare Zone ID
     * @return 初始化结果
     */
    Map<String, Object> initPrefixPool(String zoneId);

    /**
     * 重置域名前缀池
     * @param zoneId Cloudflare Zone ID
     * @return 重置结果
     */
    Map<String, Object> resetPrefixPool(String zoneId);

    /**
     * 获取域名前缀池状态
     * @param zoneId Cloudflare Zone ID
     * @return 前缀池状态信息
     */
    Map<String, Object> getPrefixPoolStatus(String zoneId);

    /**
     * 批量初始化所有域名前缀池
     * @return 批量初始化结果
     */
    Map<String, Object> batchInitPrefixPools();

    /**
     * 获取所有域名前缀池状态
     * @return 所有域名前缀池状态
     */
    Map<String, Object> getAllPrefixPoolStatus();

    /**
     * 获取域名详细信息
     * @param zoneId Cloudflare Zone ID
     * @return 域名详细信息
     */
    Map<String, Object> getZoneDetails(String zoneId);
}