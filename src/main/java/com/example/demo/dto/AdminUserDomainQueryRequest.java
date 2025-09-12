package com.example.demo.dto;

import lombok.Data;

/**
 * 文件名：AdminUserDomainQueryRequest.java
 * 功能：管理员查询用户域名请求DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-13
 * 版本：v1.0.0
 */
@Data
public class AdminUserDomainQueryRequest {
    
    /**
     * 页码，从1开始
     */
    private Integer page = 1;
    
    /**
     * 每页大小，最少20，最大3000
     */
    private Integer size = 20;
    
    /**
     * 排序字段，默认按创建时间排序
     */
    private String sortBy = "create_time";
    
    /**
     * 排序方向，ASC或DESC
     */
    private String sortDir = "DESC";
    
    /**
     * 搜索关键词，支持模糊查询用户名、邮箱、域名
     */
    private String keyword;
    
    /**
     * 用户ID，精确查询指定用户的域名
     */
    private Long userId;
    
    /**
     * 域名状态过滤，ACTIVE、INACTIVE、DELETED
     */
    private String status;
    
    /**
     * 主域名过滤
     */
    private String domain;
    
    /**
     * 验证并设置默认值
     */
    public void validate() {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 20) {
            size = 20;
        }
        if (size > 3000) {
            size = 3000;
        }
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "create_time";
        }
        if (sortDir == null || (!sortDir.equalsIgnoreCase("ASC") && !sortDir.equalsIgnoreCase("DESC"))) {
            sortDir = "DESC";
        }
    }
}