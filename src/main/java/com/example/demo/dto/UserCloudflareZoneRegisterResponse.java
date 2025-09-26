package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件名：UserCloudflareZoneRegisterResponse.java
 * 功能：用户注册Cloudflare域名响应DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Data
public class UserCloudflareZoneRegisterResponse {
    
    /**
     * 注册记录ID
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
     * 分配的十六进制前缀
     */
    private String assignedPrefix;
    
    /**
     * 完整子域名
     */
    private String fullSubdomain;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * DNS记录数量
     */
    private Integer dnsRecordCount;
    
    /**
     * DNS记录限制
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
     * 注册结果代码
     */
    private Integer resultCode;
    
    /**
     * 注册结果消息
     */
    private String resultMessage;
}