package com.example.demo.service;

import com.example.demo.entity.UserDnsRecord;

import java.util.List;

/**
 * 用户DNS解析记录服务接口
 * 提供用户DNS解析记录的业务逻辑处理
 * 
 * @author CodeBuddy
 * @since 2025-08-25
 */
public interface UserDnsRecordService {
    
    /**
     * 创建用户DNS解析记录
     * 
     * @param record 用户DNS解析记录对象
     * @return 创建的记录对象
     */
    UserDnsRecord createRecord(UserDnsRecord record);
    
    /**
     * 根据ID获取用户DNS解析记录
     * 
     * @param id 记录ID
     * @return 用户DNS解析记录对象
     */
    UserDnsRecord getRecordById(Long id);
    
    /**
     * 根据用户ID获取DNS解析记录列表
     * 
     * @param userId 用户ID
     * @return DNS解析记录列表
     */
    List<UserDnsRecord> getRecordsByUserId(Long userId);
    
    /**
     * 根据子域名ID获取DNS解析记录列表
     * 
     * @param subdomainId 子域名ID
     * @return DNS解析记录列表
     */
    List<UserDnsRecord> getRecordsBySubdomainId(Long subdomainId);
    
    /**
     * 根据用户ID和子域名ID获取DNS解析记录列表
     * 
     * @param userId 用户ID
     * @param subdomainId 子域名ID
     * @return DNS解析记录列表
     */
    List<UserDnsRecord> getRecordsByUserIdAndSubdomainId(Long userId, Long subdomainId);
    
    /**
     * 根据DNSPod记录ID获取用户DNS解析记录
     * 
     * @param recordId DNSPod记录ID
     * @return 用户DNS解析记录对象
     */
    UserDnsRecord getRecordByRecordId(Long recordId);
    
    /**
     * 根据同步状态获取DNS解析记录列表
     * 
     * @param syncStatus 同步状态
     * @return DNS解析记录列表
     */
    List<UserDnsRecord> getRecordsBySyncStatus(String syncStatus);
    
    /**
     * 更新用户DNS解析记录
     * 
     * @param record 用户DNS解析记录对象
     * @return 更新后的记录对象
     */
    UserDnsRecord updateRecord(UserDnsRecord record);
    
    /**
     * 更新同步状态
     * 
     * @param id 记录ID
     * @param syncStatus 同步状态
     * @param syncError 同步错误信息
     * @return 是否更新成功
     */
    boolean updateSyncStatus(Long id, String syncStatus, String syncError);
    
    /**
     * 更新DNSPod记录ID
     * 
     * @param id 记录ID
     * @param recordId DNSPod记录ID
     * @return 是否更新成功
     */
    boolean updateRecordId(Long id, Long recordId);
    
    /**
     * 根据ID删除用户DNS解析记录
     * 
     * @param id 记录ID
     * @return 是否删除成功
     */
    boolean deleteRecord(Long id);
    
    /**
     * 根据用户ID删除所有DNS解析记录
     * 
     * @param userId 用户ID
     * @return 删除的记录数量
     */
    int deleteRecordsByUserId(Long userId);
    
    /**
     * 根据子域名ID删除所有DNS解析记录
     * 
     * @param subdomainId 子域名ID
     * @return 删除的记录数量
     */
    int deleteRecordsBySubdomainId(Long subdomainId);
    
    /**
     * 统计用户的DNS解析记录数量
     * 
     * @param userId 用户ID
     * @return 记录数量
     */
    int countRecordsByUserId(Long userId);
    
    /**
     * 统计子域名的DNS解析记录数量
     * 
     * @param subdomainId 子域名ID
     * @return 记录数量
     */
    int countRecordsBySubdomainId(Long subdomainId);
    
    /**
     * 检查记录是否存在
     * 
     * @param userId 用户ID
     * @param subdomainId 子域名ID
     * @param name 主机记录
     * @param type 记录类型
     * @return 是否存在
     */
    boolean existsRecord(Long userId, Long subdomainId, String name, String type);
    
    /**
     * 同步DNS解析记录到DNSPod
     * 
     * @param recordId 记录ID
     * @return 是否同步成功
     */
    boolean syncRecordToDnsPod(Long recordId);
    
    /**
     * 批量同步待同步的DNS解析记录
     * 
     * @return 同步成功的记录数量
     */
    int batchSyncPendingRecords();
}