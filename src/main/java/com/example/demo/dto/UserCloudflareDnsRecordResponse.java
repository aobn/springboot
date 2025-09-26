package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件名：UserCloudflareDnsRecordResponse.java
 * 功能：用户Cloudflare DNS记录响应DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Data
public class UserCloudflareDnsRecordResponse {
    
    /**
     * 本地记录ID
     */
    private Long id;
    
    /**
     * Cloudflare DNS记录ID
     */
    private String recordId;
    
    /**
     * 用户域名ID
     */
    private Long userDomainId;
    
    /**
     * 用户子域名（如0.1.example.com）
     */
    private String userSubdomain;
    
    /**
     * DNS记录名称（完整域名）
     */
    private String name;
    
    /**
     * DNS记录类型
     */
    private String type;
    
    /**
     * DNS记录内容
     */
    private String content;
    
    /**
     * TTL值
     */
    private Integer ttl;
    
    /**
     * 是否启用代理
     */
    private Boolean proxied;
    
    /**
     * 是否可代理
     */
    private Boolean proxiable;
    
    /**
     * 优先级（MX、SRV记录）
     */
    private Integer priority;
    
    /**
     * 权重（SRV记录）
     */
    private Integer weight;
    
    /**
     * 端口（SRV记录）
     */
    private Integer port;
    
    /**
     * 记录注释
     */
    private String comment;
    
    /**
     * 同步状态
     */
    private String syncStatus;
    
    /**
     * 同步错误信息
     */
    private String syncError;
    
    /**
     * 是否启用
     */
    private Boolean isActive;
    
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