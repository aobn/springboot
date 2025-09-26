package com.example.demo.mapper;

import com.example.demo.entity.UserCloudflareZone;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 文件名：UserCloudflareZoneMapper.java
 * 功能：用户Cloudflare域名数据访问层
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Mapper
public interface UserCloudflareZoneMapper {
    
    // 所有方法都在XML文件中定义，这里只保留方法签名
    
    /**
     * 根据用户ID查询已注册的域名列表
     */
    List<UserCloudflareZone> findByUserId(Long userId);
    
    /**
     * 根据用户ID和Zone ID查询注册记录
     */
    UserCloudflareZone findByUserIdAndZoneId(@Param("userId") Long userId, @Param("zoneId") String zoneId);
    
    /**
     * 根据ID和用户ID查询
     */
    UserCloudflareZone findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 插入用户域名注册记录
     */
    int insertUserZone(UserCloudflareZone userZone);
    
    /**
     * 注销域名（软删除）
     */
    int unregisterZone(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 检查用户是否已注册域名
     */
    boolean isUserRegisteredZone(@Param("userId") Long userId, @Param("zoneId") String zoneId);
    
    /**
     * 获取可用域名列表
     */
    List<java.util.Map<String, Object>> getAvailableZones();
    
    /**
     * 调用存储过程注册域名
     */
    void callRegisterUserCloudflareZone(java.util.Map<String, Object> params);
    
    /**
     * 获取域名统计信息
     */
    java.util.Map<String, Object> getZoneStats(@Param("zoneId") String zoneId);
    
    /**
     * 批量查询用户域名信息
     */
    List<UserCloudflareZone> findByUserIdList(@Param("userIds") List<Long> userIds);
    
    /**
     * 根据Zone ID查询所有用户
     */
    List<UserCloudflareZone> findByZoneId(@Param("zoneId") String zoneId);
    
    /**
     * 统计用户在指定域名下的DNS记录数量
     */
    int countUserDnsRecords(@Param("userId") Long userId, @Param("zoneId") String zoneId);
    
    /**
     * 检查用户是否可以添加DNS记录
     */
    boolean canAddDnsRecord(@Param("userId") Long userId, @Param("zoneId") String zoneId);
    
    /**
     * 更新用户DNS记录数量
     * 
     * @param userId 用户ID
     * @param zoneId Zone ID
     * @param dnsRecordCount DNS记录数量
     * @return 影响行数
     */
    int updateDnsRecordCount(@Param("userId") Long userId, @Param("zoneId") String zoneId, @Param("dnsRecordCount") Integer dnsRecordCount);
}