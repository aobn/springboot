package com.example.demo.service;

import com.example.demo.dto.CloudflareDnsRecordResponse;
import com.example.demo.entity.CloudflareDnsRecord;

import java.util.List;

/**
 * 文件名：CloudflareDnsRecordService.java
 * 功能：Cloudflare DNS记录服务接口
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
public interface CloudflareDnsRecordService {
    
    /**
     * 从Cloudflare API同步指定Zone的DNS记录
     * @param zoneId Zone ID
     * @return 同步的记录数量
     */
    int syncDnsRecordsFromCloudflare(String zoneId);
    
    /**
     * 从Cloudflare API同步所有Zone的DNS记录
     * @return 同步的记录数量
     */
    int syncAllDnsRecordsFromCloudflare();
    
    /**
     * 获取指定Zone的本地DNS记录
     * @param zoneId Zone ID
     * @return DNS记录列表
     */
    List<CloudflareDnsRecord> getLocalDnsRecordsByZoneId(String zoneId);
    
    /**
     * 根据Zone ID和记录类型获取本地DNS记录
     * @param zoneId Zone ID
     * @param type 记录类型
     * @return DNS记录列表
     */
    List<CloudflareDnsRecord> getLocalDnsRecordsByZoneIdAndType(String zoneId, String type);
    
    /**
     * 根据记录ID获取本地DNS记录
     * @param recordId 记录ID
     * @return DNS记录实体
     */
    CloudflareDnsRecord getLocalDnsRecordByRecordId(String recordId);
    
    /**
     * 保存或更新DNS记录
     * @param record DNS记录实体
     * @return 是否成功
     */
    boolean saveOrUpdateDnsRecord(CloudflareDnsRecord record);
    
    /**
     * 批量保存或更新DNS记录
     * @param records DNS记录列表
     * @return 成功保存的记录数量
     */
    int batchSaveOrUpdateDnsRecords(List<CloudflareDnsRecord> records);
    
    /**
     * 删除DNS记录
     * @param recordId 记录ID
     * @return 是否成功
     */
    boolean deleteDnsRecord(String recordId);
    
    /**
     * 删除指定Zone的所有DNS记录
     * @param zoneId Zone ID
     * @return 删除的记录数量
     */
    int deleteDnsRecordsByZoneId(String zoneId);
    
    /**
     * 统计指定Zone的DNS记录数量
     * @param zoneId Zone ID
     * @return 记录数量
     */
    int countDnsRecordsByZoneId(String zoneId);
    
    /**
     * 将Cloudflare API响应转换为本地实体
     * @param apiRecord API响应中的DNS记录
     * @param zoneId Zone ID
     * @return 本地DNS记录实体
     */
    CloudflareDnsRecord convertToEntity(CloudflareDnsRecordResponse.DnsRecord apiRecord, String zoneId);
}