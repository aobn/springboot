package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件名：CloudflareZone.java
 * 功能：Cloudflare域名(Zone)信息实体类
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
@Data
public class CloudflareZone {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * Cloudflare Zone ID
     */
    private String zoneId;
    
    /**
     * 域名名称
     */
    private String name;
    
    /**
     * 域名状态(active/pending/initializing/moved/deleted/deactivated)
     */
    private String status;
    
    /**
     * 是否暂停(false-否，true-是)
     */
    private Boolean paused;
    
    /**
     * Zone类型(full/partial)
     */
    private String type;
    
    /**
     * 开发模式剩余时间(秒)
     */
    private Integer developmentMode;
    
    /**
     * Cloudflare名称服务器列表
     */
    private List<String> nameServers;
    
    /**
     * 原始名称服务器列表
     */
    private List<String> originalNameServers;
    
    /**
     * 原始注册商
     */
    private String originalRegistrar;
    
    /**
     * 原始DNS主机
     */
    private String originalDnshost;
    
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
     * Cloudflare激活时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedOn;
    
    /**
     * 虚荣名称服务器列表
     */
    private List<String> vanityNameServers;
    
    /**
     * 虚荣名称服务器IP列表
     */
    private List<String> vanityNameServersIps;
    
    /**
     * 设置步骤
     */
    private Integer metaStep;
    
    /**
     * 自定义证书配额
     */
    private Integer metaCustomCertificateQuota;
    
    /**
     * 页面规则配额
     */
    private Integer metaPageRuleQuota;
    
    /**
     * 是否检测到钓鱼(false-否，true-是)
     */
    private Boolean metaPhishingDetected;
    
    /**
     * 所有者ID
     */
    private String ownerId;
    
    /**
     * 所有者类型
     */
    private String ownerType;
    
    /**
     * 所有者邮箱
     */
    private String ownerEmail;
    
    /**
     * Cloudflare账户ID
     */
    private String accountId;
    
    /**
     * Cloudflare账户名称
     */
    private String accountName;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 租户名称
     */
    private String tenantName;
    
    /**
     * 租户单元ID
     */
    private String tenantUnitId;
    
    /**
     * 计划ID
     */
    private String planId;
    
    /**
     * 计划名称
     */
    private String planName;
    
    /**
     * 计划价格
     */
    private BigDecimal planPrice;
    
    /**
     * 计划货币
     */
    private String planCurrency;
    
    /**
     * 计费频率
     */
    private String planFrequency;
    
    /**
     * 是否已订阅(false-否，true-是)
     */
    private Boolean planIsSubscribed;
    
    /**
     * 是否可订阅(false-否，true-是)
     */
    private Boolean planCanSubscribe;
    
    /**
     * 旧版计划ID
     */
    private String planLegacyId;
    
    /**
     * 旧版折扣(false-否，true-是)
     */
    private Boolean planLegacyDiscount;
    
    /**
     * 外部管理(false-否，true-是)
     */
    private Boolean planExternallyManaged;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
    
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
     * 备注信息
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