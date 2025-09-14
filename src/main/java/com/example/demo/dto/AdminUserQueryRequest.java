package com.example.demo.dto;

import lombok.Data;

/**
 * 文件名：AdminUserQueryRequest.java
 * 功能：管理员查询用户信息请求DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-13
 * 版本：v1.0.0
 */
@Data
public class AdminUserQueryRequest {
    
    /**
     * 页码，从1开始
     */
    private Integer page = 1;
    
    /**
     * 每页大小，范围20-3000
     */
    private Integer size = 20;
    
    /**
     * 排序字段，支持：id、username、email、create_time、update_time
     */
    private String sortBy = "create_time";
    
    /**
     * 排序方向，ASC或DESC
     */
    private String sortDir = "DESC";
    
    /**
     * 关键词模糊搜索，支持用户名、邮箱搜索
     */
    private String keyword;
    
    /**
     * 用户ID精确查询
     */
    private Long userId;
    
    /**
     * 用户角色过滤，USER或ADMIN
     */
    private String role;
    
    /**
     * 账户状态过滤，ACTIVE或BANNED
     */
    private String status;
    
    /**
     * 创建时间范围查询 - 开始时间
     */
    private String createTimeStart;
    
    /**
     * 创建时间范围查询 - 结束时间
     */
    private String createTimeEnd;
    
    /**
     * 验证并设置默认值
     */
    public void validate() {
        // 页码验证
        if (page == null || page < 1) {
            page = 1;
        }
        
        // 每页大小验证
        if (size == null || size < 20) {
            size = 20;
        } else if (size > 3000) {
            size = 3000;
        }
        
        // 排序字段验证
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "create_time";
        } else {
            // 只允许特定字段排序
            String[] allowedFields = {"id", "username", "email", "create_time", "update_time"};
            boolean isValidField = false;
            for (String field : allowedFields) {
                if (field.equals(sortBy.trim())) {
                    isValidField = true;
                    break;
                }
            }
            if (!isValidField) {
                sortBy = "create_time";
            }
        }
        
        // 排序方向验证
        if (sortDir == null || (!sortDir.equalsIgnoreCase("ASC") && !sortDir.equalsIgnoreCase("DESC"))) {
            sortDir = "DESC";
        } else {
            sortDir = sortDir.toUpperCase();
        }
        
        // 关键词处理
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        
        // 角色验证
        if (role != null) {
            role = role.trim().toUpperCase();
            if (!role.equals("USER") && !role.equals("ADMIN")) {
                role = null;
            }
        }
        
        // 状态验证
        if (status != null) {
            status = status.trim().toUpperCase();
            if (!status.equals("ACTIVE") && !status.equals("BANNED")) {
                status = null;
            }
        }
    }
    
    /**
     * 获取LIMIT的offset值
     */
    public Integer getOffset() {
        return (page - 1) * size;
    }
}