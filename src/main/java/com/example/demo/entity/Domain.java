package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件名：Domain.java
 * 功能：域名实体类，对应domain表
 * 作者：CodeBuddy
 * 创建时间：2025-08-21
 * 版本：v1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Domain {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * DNSPod域名ID
     */
    private Long domainId;
    
    /**
     * 域名名称
     */
    private String name;
    
    /**
     * 域名Punycode编码
     */
    private String punycode;
    
    /**
     * 域名套餐等级
     */
    private String grade;
    
    /**
     * 套餐等级数值
     */
    private Integer gradeLevel;
    
    /**
     * 套餐等级标题
     */
    private String gradeTitle;
    
    /**
     * 是否为VIP域名
     */
    private String isVip;
    
    /**
     * 域名所有者
     */
    private String owner;
    
    /**
     * 域名状态
     */
    private String status;
    
    /**
     * 分组ID
     */
    private Integer groupId;
    
    /**
     * 搜索引擎推送状态
     */
    private String searchEnginePush;
    
    /**
     * 解析记录数量
     */
    private Integer recordCount;
    
    /**
     * 默认TTL值
     */
    private Integer ttl;
    
    /**
     * CNAME加速状态
     */
    private String cnameSpeedup;
    
    /**
     * DNS状态
     */
    private String dnsStatus;
    
    /**
     * 域名备注
     */
    private String remark;
    
    /**
     * VIP开始时间
     */
    private LocalDateTime vipStartAt;
    
    /**
     * VIP结束时间
     */
    private LocalDateTime vipEndAt;
    
    /**
     * VIP自动续费状态
     */
    private String vipAutoRenew;
    
    /**
     * DNSPod创建时间
     */
    private LocalDateTime createdOn;
    
    /**
     * DNSPod更新时间
     */
    private LocalDateTime updatedOn;
    
    /**
     * 本地创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 本地更新时间
     */
    private LocalDateTime updateTime;
}