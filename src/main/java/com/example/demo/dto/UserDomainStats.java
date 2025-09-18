package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件名：UserDomainStats.java
 * 功能：用户域名使用统计DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-10
 * 版本：v1.0.0
 * 
 * 用于返回用户域名注册和使用情况的统计信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDomainStats {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户邮箱
     */
    private String email;
    
    /**
     * 可注册域名数量
     */
    private Integer availableDomains;
    
    /**
     * 已使用域名数量
     */
    private Integer usedDomains;
    
    /**
     * 注册状态
     * CAN_REGISTER - 可以注册
     * LIMIT_REACHED - 已达上限
     */
    private String registrationStatus;
    
    /**
     * 获取注册状态描述
     */
    public String getRegistrationStatusDesc() {
        if ("CAN_REGISTER".equals(registrationStatus)) {
            return "可以注册";
        } else if ("LIMIT_REACHED".equals(registrationStatus)) {
            return "已达上限";
        }
        return "未知状态";
    }
    
    /**
     * 检查是否可以注册域名
     */
    public boolean canRegister() {
        return availableDomains != null && availableDomains > 0;
    }
    
    /**
     * 获取总域名配额
     */
    public Integer getTotalQuota() {
        if (availableDomains == null || usedDomains == null) {
            return 2; // 默认配额
        }
        return availableDomains + usedDomains;
    }
}