package com.example.demo.dto;

import lombok.Data;

/**
 * 文件名：PageRequest.java
 * 功能：分页请求参数
 * 作者：CodeBuddy
 * 创建时间：2025-09-12
 * 版本：v1.0.0
 */
@Data
public class PageRequest {
    
    /**
     * 页码，从1开始
     */
    private Integer page = 1;
    
    /**
     * 每页大小，默认3000条
     */
    private Integer size = 3000;
    
    /**
     * 排序字段，默认按ID排序
     */
    private String sortBy = "id";
    
    /**
     * 排序方向，ASC或DESC，默认ASC
     */
    private String sortDir = "ASC";
    
    /**
     * 获取偏移量
     * @return 偏移量
     */
    public Integer getOffset() {
        return (page - 1) * size;
    }
    
    /**
     * 验证分页参数
     */
    public void validate() {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 3000;
        }
        if (size > 10000) {
            size = 10000; // 限制最大查询数量
        }
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id";
        }
        if (sortDir == null || (!sortDir.equalsIgnoreCase("ASC") && !sortDir.equalsIgnoreCase("DESC"))) {
            sortDir = "ASC";
        }
    }
}