package com.example.demo.dto;

import lombok.Data;

/**
 * 文件名：UnbanUserRequest.java
 * 功能：管理员解封用户请求DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-13
 * 版本：v1.0.0
 * 
 * 用于管理员解封用户账户的请求参数
 */
@Data
public class UnbanUserRequest {
    
    /**
     * 用户ID - 必填
     * 要解封的用户ID
     */
    private Long userId;
    
    /**
     * 解封原因 - 可选
     * 管理员填写的解封原因，用于记录
     * 长度限制：最多500字符
     */
    private String unbanReason;
    
    /**
     * 参数验证
     */
    public boolean isValid() {
        return userId != null && userId > 0 
            && (unbanReason == null || unbanReason.length() <= 500);
    }
    
    /**
     * 获取验证错误信息
     */
    public String getValidationError() {
        if (userId == null || userId <= 0) {
            return "用户ID不能为空且必须大于0";
        }
        if (unbanReason != null && unbanReason.length() > 500) {
            return "解封原因长度不能超过500字符";
        }
        return null;
    }
}