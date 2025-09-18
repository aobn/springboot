package com.example.demo.dto;

import lombok.Data;
import java.util.List;

/**
 * 文件名：PageResponse.java
 * 功能：分页响应结果
 * 作者：CodeBuddy
 * 创建时间：2025-09-12
 * 版本：v1.0.0
 */
@Data
public class PageResponse<T> {
    
    /**
     * 当前页数据列表
     */
    private List<T> content;
    
    /**
     * 当前页码（从1开始）
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    /**
     * 是否为第一页
     */
    private Boolean first;
    
    /**
     * 是否为最后一页
     */
    private Boolean last;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;
    
    /**
     * 构造函数
     */
    public PageResponse() {}
    
    /**
     * 构造函数
     * @param content 数据列表
     * @param page 当前页码
     * @param size 每页大小
     * @param total 总记录数
     */
    public PageResponse(List<T> content, Integer page, Integer size, Long total) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
        this.first = page == 1;
        this.last = page.equals(totalPages);
        this.hasNext = page < totalPages;
        this.hasPrevious = page > 1;
    }
    
    /**
     * 创建分页响应对象
     * @param content 数据列表
     * @param page 当前页码
     * @param size 每页大小
     * @param total 总记录数
     * @return 分页响应对象
     */
    public static <T> PageResponse<T> of(List<T> content, Integer page, Integer size, Long total) {
        return new PageResponse<>(content, page, size, total);
    }
}