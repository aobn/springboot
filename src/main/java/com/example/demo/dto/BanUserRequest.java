package com.example.demo.dto;

import lombok.Data;

/**
 * 文件名：BanUserRequest.java
 * 功能：管理员封禁用户请求DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-13
 * 版本：v1.0.0
 * 
 * 用于管理员封禁用户账户的请求参数
 */
@Data
public class BanUserRequest {
    
    /**
     * 用户ID - 必填
     * 要封禁的用户ID
     */
    private Long userId;
    
    /**
     * 封禁原因 - 必填
     * 管理员填写的封禁原因，用于记录和用户查询
     * 长度限制：1-500字符
     */
    private String banReason;
    
    /**
     * 参数验证
     */
    public boolean isValid() {
        return userId != null && userId > 0 
            && banReason != null && !banReason.trim().isEmpty() 
            && banReason.trim().length() <= 500;
    }
    
    /**
     * 获取验证错误信息
     */
    public String getValidationError() {
        if (userId == null || userId <= 0) {
            return "用户ID不能为空且必须大于0";
        }
        if (banReason == null || banReason.trim().isEmpty()) {
            return "封禁原因不能为空";
        }
        if (banReason.trim().length() > 500) {
            return "封禁原因长度不能超过500字符";
        }
        return null;
    }
}