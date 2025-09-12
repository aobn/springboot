package com.example.demo.dto;

import com.example.demo.entity.UserSubdomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件名：UserSubdomainWithUser.java
 * 功能：包含用户信息的用户子域名DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-13
 * 版本：v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSubdomainWithUser extends UserSubdomain {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户邮箱
     */
    private String email;
}