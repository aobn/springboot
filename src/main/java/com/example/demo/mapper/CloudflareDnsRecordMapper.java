package com.example.demo.mapper;

import com.example.demo.entity.CloudflareDnsRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件名：CloudflareDnsRecordMapper.java
 * 功能：Cloudflare DNS记录数据访问层
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
@Mapper
public interface CloudflareDnsRecordMapper {
    
    /**
     * 插入DNS记录
     * @param record DNS记录实体
     * @return 影响行数
     */
    int insert(CloudflareDnsRecord record);
    
    /**
     * 根据记录ID更新DNS记录
     * @param record DNS记录实体
     * @return 影响行数
     */
    int updateByRecordId(CloudflareDnsRecord record);
    
    /**
     * 根据记录ID删除DNS记录
     * @param recordId 记录ID
     * @return 影响行数
     */
    int deleteByRecordId(@Param("recordId") String recordId);
    
    /**
     * 根据Zone ID删除所有DNS记录
     * @param zoneId Zone ID
     * @return 影响行数
     */
    int deleteByZoneId(@Param("zoneId") String zoneId);
    
    /**
     * 根据记录ID查询DNS记录
     * @param recordId 记录ID
     * @return DNS记录实体
     */
    CloudflareDnsRecord findByRecordId(@Param("recordId") String recordId);
    
    /**
     * 根据Zone ID查询所有DNS记录
     * @param zoneId Zone ID
     * @return DNS记录列表
     */
    List<CloudflareDnsRecord> findByZoneId(@Param("zoneId") String zoneId);
    
    /**
     * 根据Zone ID和记录类型查询DNS记录
     * @param zoneId Zone ID
     * @param type 记录类型
     * @return DNS记录列表
     */
    List<CloudflareDnsRecord> findByZoneIdAndType(@Param("zoneId") String zoneId, @Param("type") String type);
    
    /**
     * 根据Zone ID和记录名称查询DNS记录
     * @param zoneId Zone ID
     * @param name 记录名称
     * @return DNS记录列表
     */
    List<CloudflareDnsRecord> findByZoneIdAndName(@Param("zoneId") String zoneId, @Param("name") String name);
    
    /**
     * 查询所有DNS记录
     * @return DNS记录列表
     */
    List<CloudflareDnsRecord> findAll();
    
    /**
     * 根据同步状态查询DNS记录
     * @param syncStatus 同步状态
     * @return DNS记录列表
     */
    List<CloudflareDnsRecord> findBySyncStatus(@Param("syncStatus") String syncStatus);
    
    /**
     * 批量插入DNS记录
     * @param records DNS记录列表
     * @return 影响行数
     */
    int batchInsert(@Param("records") List<CloudflareDnsRecord> records);
    
    /**
     * 统计Zone的DNS记录数量
     * @param zoneId Zone ID
     * @return 记录数量
     */
    int countByZoneId(@Param("zoneId") String zoneId);
    
    /**
     * 统计指定类型的DNS记录数量
     * @param zoneId Zone ID
     * @param type 记录类型
     * @return 记录数量
     */
    int countByZoneIdAndType(@Param("zoneId") String zoneId, @Param("type") String type);
}