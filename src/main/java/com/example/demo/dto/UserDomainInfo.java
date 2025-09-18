package com.example.demo.dto;

import com.example.demo.entity.UserSubdomain;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件名：UserDomainInfo.java
 * 功能：用户域名信息DTO，包含用户基本信息
 * 作者：CodeBuddy
 * 创建时间：2025-09-13
 * 版本：v1.0.0
 */
@Data
public class UserDomainInfo {
    
    /**
     * 域名ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户邮箱
     */
    private String email;
    
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
     * 域名状态
     */
    private String status;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 从UserSubdomain实体转换
     */
    public static UserDomainInfo fromUserSubdomain(UserSubdomain userSubdomain) {
        UserDomainInfo info = new UserDomainInfo();
        info.setId(userSubdomain.getId());
        info.setUserId(userSubdomain.getUserId());
        info.setSubdomain(userSubdomain.getSubdomain());
        info.setDomain(userSubdomain.getDomain());
        info.setFullDomain(userSubdomain.getFullDomain());
        info.setStatus(userSubdomain.getStatus());
        info.setRemark(userSubdomain.getRemark());
        info.setCreateTime(userSubdomain.getCreateTime());
        info.setUpdateTime(userSubdomain.getUpdateTime());
        return info;
    }
}