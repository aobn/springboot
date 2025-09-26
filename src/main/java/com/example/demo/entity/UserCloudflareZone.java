package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件名：UserCloudflareZone.java
 * 功能：用户Cloudflare域名注册实体类
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Data
public class UserCloudflareZone {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * Cloudflare Zone ID
     */
    private String zoneId;
    
    /**
     * 域名名称
     */
    private String zoneName;
    
    /**
     * 分配的十六进制前缀(如0.1, a.f等)
     */
    private String assignedPrefix;
    
    /**
     * 完整子域名(如0.1.example.com)
     */
    private String fullSubdomain;
    
    /**
     * 状态(ACTIVE-活跃，SUSPENDED-暂停，DELETED-已删除)
     */
    private String status;
    
    /**
     * 该用户在此域名下的DNS记录数量
     */
    private Integer dnsRecordCount;
    
    /**
     * 该用户在此域名下的DNS记录限制
     */
    private Integer dnsRecordLimit;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 分配时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignedTime;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}