package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件名：UserAllDomainsResponse.java
 * 功能：用户所有域名响应DTO（包括DNSPod和Cloudflare域名）
 * 作者：CodeBuddy
 * 创建时间：2025-09-27
 * 版本：v1.0.0
 */
@Data
public class UserAllDomainsResponse {
    
    /**
     * DNSPod 3级域名列表
     */
    private List<DnspodDomainInfo> dnspodDomains;
    
    /**
     * Cloudflare域名列表
     */
    private List<CloudflareDomainInfo> cloudflareDomains;
    
    /**
     * 域名统计信息
     */
    private DomainStats stats;
    
    /**
     * DNSPod域名信息
     */
    @Data
    public static class DnspodDomainInfo {
        /**
         * 记录ID
         */
        private Long id;
        
        /**
         * 用户ID
         */
        private Long userId;
        
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
         * 状态
         */
        private String status;
        
        /**
         * 备注信息
         */
        private String remark;
        
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
        
        /**
         * 域名类型
         */
        private String type = "DNSPOD";
    }
    
    /**
     * Cloudflare域名信息
     */
    @Data
    public static class CloudflareDomainInfo {
        /**
         * 记录ID
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
         * 备注信息
         */
        private String remark;
        
        /**
         * 分配时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime assignedTime;
        
        /**
         * 域名类型
         */
        private String type = "CLOUDFLARE";
    }
    
    /**
     * 域名统计信息
     */
    @Data
    public static class DomainStats {
        /**
         * 用户可注册域名数量
         */
        private Integer availableDomains;
        
        /**
         * DNSPod域名数量
         */
        private Integer dnspodDomainsCount;
        
        /**
         * Cloudflare域名数量
         */
        private Integer cloudflareDomainsCount;
        
        /**
         * 总域名数量
         */
        private Integer totalDomainsCount;
        
        /**
         * 注册状态
         */
        private String registrationStatus;
    }
}