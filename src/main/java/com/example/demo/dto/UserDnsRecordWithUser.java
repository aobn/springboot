package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件名：UserDnsRecordWithUser.java
 * 功能：用户DNS记录与用户信息联合查询结果DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-13
 * 版本：v1.0.0
 */
@Data
public class UserDnsRecordWithUser {
    
    // DNS记录信息
    private Long id;
    private Long userId;
    private Long subdomainId;
    private Long recordId;
    private String name;
    private String type;
    private String value;
    private String line;
    private String lineId;
    private Integer ttl;
    private Integer mx;
    private Integer weight;
    private String status;
    private String remark;
    private String monitorStatus;
    private LocalDateTime updatedOn;
    private String syncStatus;
    private String syncError;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 用户信息
    private String username;
    private String email;
    
    // 域名信息
    private String subdomain;
    private String domain;
    private String fullDomain;
}