package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文件名：CloudflareDnsRecord.java
 * 功能：Cloudflare DNS记录实体类
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudflareDnsRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * Cloudflare Zone ID，关联cloudflare_zone表
     */
    private String zoneId;
    
    /**
     * Cloudflare DNS记录ID
     */
    private String recordId;
    
    /**
     * DNS记录名称（完整域名）
     */
    private String name;
    
    /**
     * DNS记录类型（A、AAAA、CNAME、MX、TXT、NS、SRV等）
     */
    private String type;
    
    /**
     * DNS记录内容（IP地址、域名、文本等）
     */
    private String content;
    
    /**
     * 是否可代理(false-否，true-是)
     */
    private Boolean proxiable;
    
    /**
     * 是否已代理(false-否，true-是)
     */
    private Boolean proxied;
    
    /**
     * TTL值（1表示自动）
     */
    private Integer ttl;
    
    /**
     * MX记录优先级或SRV记录优先级
     */
    private Integer priority;
    
    /**
     * SRV记录权重
     */
    private Integer weight;
    
    /**
     * SRV记录端口
     */
    private Integer port;
    
    /**
     * SRV记录服务
     */
    private String service;
    
    /**
     * SRV记录协议
     */
    private String proto;
    
    /**
     * SRV记录目标
     */
    private String target;
    
    /**
     * DNS记录设置(JSON格式)
     */
    private Map<String, Object> settings;
    
    /**
     * DNS记录元数据(JSON格式)
     */
    private Map<String, Object> meta;
    
    /**
     * DNS记录标签列表(JSON格式)
     */
    private List<String> tags;
    
    /**
     * DNS记录备注
     */
    private String comment;
    
    /**
     * 备注修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentModifiedOn;
    
    /**
     * Cloudflare创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    
    /**
     * Cloudflare修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedOn;
    
    /**
     * 同步状态(SUCCESS-同步成功，FAILED-同步失败)
     */
    private String syncStatus;
    
    /**
     * 同步错误信息
     */
    private String syncError;
    
    /**
     * 最后同步时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSyncTime;
    
    /**
     * 本地启用状态(false-禁用，true-启用)
     */
    private Boolean isActive;
    
    /**
     * 本地备注信息
     */
    private String remark;
    
    /**
     * 本地创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 本地更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}