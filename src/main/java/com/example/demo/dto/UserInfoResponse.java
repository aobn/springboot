package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文件名：UserInfoResponse.java
 * 功能：用户信息响应DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-13
 * 版本：v1.0.0
 */
@Data
public class UserInfoResponse {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户角色
     */
    private String role;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 用户可注册域名数量限制
     */
    private Integer domNum;
    
    /**
     * 用户统计信息 - 域名数量
     */
    private Integer domainCount;
    
    /**
     * 用户统计信息 - DNS记录数量
     */
    private Integer dnsRecordCount;
    
    /**
     * 最后登录时间（如果有相关字段）
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 账户状态（如果有相关字段）
     */
    private String status;
}