package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件名：UserCloudflareZoneListResponse.java
 * 功能：用户Cloudflare域名列表响应DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Data
public class UserCloudflareZoneListResponse {
    
    /**
     * 用户已注册的域名列表
     */
    private List<UserZoneInfo> userZones;
    
    /**
     * 可用的域名列表
     */
    private List<AvailableZoneInfo> availableZones;
    
    /**
     * 用户域名信息
     */
    @Data
    public static class UserZoneInfo {
        /**
         * 注册记录ID
         */
        private Long id;
        
        /**
         * Cloudflare Zone ID
         */
        private String zoneId;
        
        /**
         * 域名名称
         */
        private String zoneName;
        
        /**
         * 分配的前缀
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
         * DNS使用率百分比
         */
        private Double dnsUsagePercent;
        
        /**
         * 分配时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime assignedTime;
    }
    
    /**
     * 可用域名信息
     */
    @Data
    public static class AvailableZoneInfo {
        /**
         * Cloudflare Zone ID
         */
        private String zoneId;
        
        /**
         * 域名名称
         */
        private String zoneName;
        
        /**
         * 域名状态
         */
        private String status;
        
        /**
         * 已注册用户数量
         */
        private Integer userCount;
        
        /**
         * 用户数量限制
         */
        private Integer userLimit;
        
        /**
         * 用户使用率百分比
         */
        private Double userUsagePercent;
        
        /**
         * DNS记录数量
         */
        private Integer dnsRecordCount;
        
        /**
         * DNS记录限制
         */
        private Integer dnsRecordLimit;
        
        /**
         * DNS使用率百分比
         */
        private Double dnsUsagePercent;
        
        /**
         * 是否已满
         */
        private Boolean isFull;
        
        /**
         * 是否启用自动分配
         */
        private Boolean autoAssignEnabled;
        
        /**
         * 可用前缀数量
         */
        private Integer availablePrefixes;
    }
}