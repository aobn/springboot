package com.example.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;

/**
 * 用户DNS解析记录查询请求DTO
 * 用于接收JSON格式的查询参数
 * 
 * @author CodeBuddy
 * @since 2025-08-29
 */
@Data
public class UserDnsRecordQueryRequest {
    
    /**
     * 子域名ID（可选）
     * 用于过滤特定3级域名的DNS记录
     */
    @Min(value = 1, message = "子域名ID必须大于0")
    private Long subdomainId;
    
    /**
     * 记录类型（可选）
     * 如：A、CNAME、MX、TXT等
     */
    private String type;
    
    /**
     * 记录状态（可选）
     * 如：ENABLE、DISABLE等
     */
    private String status;
    
    /**
     * 同步状态（可选）
     * 如：SUCCESS、PENDING、FAILED
     */
    private String syncStatus;
    
    /**
     * 分页偏移量（可选，默认0）
     */
    @Min(value = 0, message = "偏移量不能小于0")
    private Integer offset = 0;
    
    /**
     * 分页限制数量（可选，默认100）
     */
    @Min(value = 1, message = "限制数量必须大于0")
    private Integer limit = 100;
}