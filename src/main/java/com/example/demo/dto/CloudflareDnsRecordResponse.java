package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 文件名：CloudflareDnsRecordResponse.java
 * 功能：Cloudflare DNS记录API响应DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudflareDnsRecordResponse {
    
    /**
     * API调用是否成功
     */
    private boolean success;
    
    /**
     * 错误信息列表
     */
    private List<Map<String, Object>> errors;
    
    /**
     * 消息列表
     */
    private List<Map<String, Object>> messages;
    
    /**
     * DNS记录列表
     */
    private List<DnsRecord> result;
    
    /**
     * 分页信息
     */
    @JsonProperty("result_info")
    private ResultInfo resultInfo;
    
    /**
     * DNS记录信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DnsRecord {
        
        /**
         * DNS记录ID
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
         * 是否已代理
         */
        private boolean proxied;
        
        /**
         * TTL值
         */
        private int ttl;
        
        /**
         * MX记录优先级
         */
        private Integer priority;
        
        /**
         * SRV记录权重
         */
        private Integer weight;
        
        /**
         * SRV记录端口
         */
        private Integer port;
        
        /**
         * SRV记录数据
         */
        private SrvData data;
        
        /**
         * DNS记录设置
         */
        private Map<String, Object> settings;
        
        /**
         * DNS记录元数据
         */
        private Map<String, Object> meta;
        
        /**
         * DNS记录备注
         */
        private String comment;
        
        /**
         * DNS记录标签
         */
        private List<String> tags;
        
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
         * 备注修改时间
         */
        @JsonProperty("comment_modified_on")
        private String commentModifiedOn;
    }
    
    /**
     * SRV记录数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SrvData {
        
        /**
         * 服务名称
         */
        private String service;
        
        /**
         * 协议
         */
        private String proto;
        
        /**
         * 名称
         */
        private String name;
        
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
         * 目标
         */
        private String target;
    }
    
    /**
     * 分页信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultInfo {
        
        /**
         * 当前页码
         */
        private int page;
        
        /**
         * 每页数量
         */
        @JsonProperty("per_page")
        private int perPage;
        
        /**
         * 当前页记录数
         */
        private int count;
        
        /**
         * 总记录数
         */
        @JsonProperty("total_count")
        private int totalCount;
        
        /**
         * 总页数
         */
        @JsonProperty("total_pages")
        private int totalPages;
    }
}