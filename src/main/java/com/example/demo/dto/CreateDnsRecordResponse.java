package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文件名：CreateDnsRecordResponse.java
 * 功能：创建DNS记录响应DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Data
public class CreateDnsRecordResponse {
    
    /**
     * 响应是否成功
     */
    private boolean success;
    
    /**
     * 错误信息列表
     */
    private List<ErrorInfo> errors;
    
    /**
     * 消息列表
     */
    private List<String> messages;
    
    /**
     * 创建的DNS记录结果
     */
    private DnsRecordResult result;
    
    /**
     * DNS记录结果信息
     */
    @Data
    public static class DnsRecordResult {
        /**
         * Cloudflare记录ID
         */
        private String id;
        
        /**
         * DNS记录名称
         */
        private String name;
        
        /**
         * DNS记录类型
         */
        private String type;
        
        /**
         * DNS记录内容
         */
        private String content;
        
        /**
         * 是否可代理
         */
        private boolean proxiable;
        
        /**
         * 是否启用代理
         */
        private boolean proxied;
        
        /**
         * TTL值
         */
        private int ttl;
        
        /**
         * 优先级
         */
        private Integer priority;
        
        /**
         * 权重
         */
        private Integer weight;
        
        /**
         * 端口
         */
        private Integer port;
        
        /**
         * 高级设置
         */
        private Map<String, Object> settings;
        
        /**
         * 元数据
         */
        private Map<String, Object> meta;
        
        /**
         * 标签
         */
        private List<String> tags;
        
        /**
         * 注释
         */
        private String comment;
        
        /**
         * 创建时间
         */
        @JsonProperty("created_on")
        private String createdOn;
        
        /**
         * 修改时间
         */
        @JsonProperty("modified_on")
        private String modifiedOn;
        
        /**
         * 注释修改时间
         */
        @JsonProperty("comment_modified_on")
        private String commentModifiedOn;
    }
    
    /**
     * 错误信息
     */
    @Data
    public static class ErrorInfo {
        /**
         * 错误代码
         */
        private int code;
        
        /**
         * 错误消息
         */
        private String message;
    }
}