package com.example.demo.dto;

import com.example.demo.entity.UserDnsRecord;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件名：UserDnsRecordInfo.java
 * 功能：用户DNS记录信息DTO，包含用户和域名基本信息
 * 作者：CodeBuddy
 * 创建时间：2025-09-13
 * 版本：v1.0.0
 */
@Data
public class UserDnsRecordInfo {
    
    /**
     * DNS记录ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户邮箱
     */
    private String email;
    
    /**
     * 子域名ID
     */
    private Long subdomainId;
    
    /**
     * 子域名前缀
     */
    private String subdomain;
    
    /**
     * 主域名
     */
    private String domain;
    
    /**
     * 完整域名
     */
    private String fullDomain;
    
    /**
     * DNSPod记录ID
     */
    private Long recordId;
    
    /**
     * 主机记录
     */
    private String name;
    
    /**
     * 记录类型
     */
    private String type;
    
    /**
     * 记录值
     */
    private String value;
    
    /**
     * 记录线路
     */
    private String line;
    
    /**
     * 线路ID
     */
    private String lineId;
    
    /**
     * TTL值
     */
    private Integer ttl;
    
    /**
     * MX优先级
     */
    private Integer mx;
    
    /**
     * 权重
     */
    private Integer weight;
    
    /**
     * 记录状态
     */
    private String status;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 监控状态
     */
    private String monitorStatus;
    
    /**
     * DNSPod更新时间
     */
    private LocalDateTime updatedOn;
    
    /**
     * 同步状态
     */
    private String syncStatus;
    
    /**
     * 同步错误信息
     */
    private String syncError;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 从UserDnsRecord实体转换
     */
    public static UserDnsRecordInfo fromUserDnsRecord(UserDnsRecord record) {
        UserDnsRecordInfo info = new UserDnsRecordInfo();
        info.setId(record.getId());
        info.setUserId(record.getUserId());
        info.setSubdomainId(record.getSubdomainId());
        info.setRecordId(record.getRecordId());
        info.setName(record.getName());
        info.setType(record.getType());
        info.setValue(record.getValue());
        info.setLine(record.getLine());
        info.setLineId(record.getLineId());
        info.setTtl(record.getTtl());
        info.setMx(record.getMx());
        info.setWeight(record.getWeight());
        info.setStatus(record.getStatus());
        info.setRemark(record.getRemark());
        info.setMonitorStatus(record.getMonitorStatus());
        info.setUpdatedOn(record.getUpdatedOn());
        info.setSyncStatus(record.getSyncStatus());
        info.setSyncError(record.getSyncError());
        info.setCreateTime(record.getCreateTime());
        info.setUpdateTime(record.getUpdateTime());
        return info;
    }
}