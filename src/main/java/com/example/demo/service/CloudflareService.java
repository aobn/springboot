package com.example.demo.service;

import com.example.demo.dto.CloudflareZoneResponse;
import com.example.demo.dto.CloudflareDnsRecordResponse;
import com.example.demo.dto.CreateDnsRecordRequest;
import com.example.demo.dto.CreateDnsRecordResponse;

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
    
    /**
     * 获取指定Zone的DNS记录
     * 
     * @param zoneId Zone ID
     * @return DNS记录响应
     * @throws Exception 调用API异常
     */
    CloudflareDnsRecordResponse getDnsRecords(String zoneId) throws Exception;
    
    /**
     * 创建DNS记录
     * 
     * @param zoneId Zone ID
     * @param request 创建DNS记录请求
     * @return 创建结果
     * @throws Exception 调用API异常
     */
    CreateDnsRecordResponse createDnsRecord(String zoneId, CreateDnsRecordRequest request) throws Exception;
    
    /**
     * 更新DNS记录
     * 
     * @param zoneId Zone ID
     * @param recordId DNS记录ID
     * @param updateData 更新数据
     * @return 更新结果
     * @throws Exception 调用API异常
     */
    CreateDnsRecordResponse updateDnsRecord(String zoneId, String recordId, java.util.Map<String, Object> updateData) throws Exception;
    
    /**
     * 测试Cloudflare API连接
     * 
     * @return 是否连接成功
     */
    boolean testConnection();
}