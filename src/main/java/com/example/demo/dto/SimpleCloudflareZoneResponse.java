package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 简化的Cloudflare域名响应DTO
 * 只返回必要的域名信息，不包含复杂的meta、owner、tenant、permissions、plan等字段
 * 
 * @author CodeBuddy
 * @since 2025-09-26
 */
@Data
public class SimpleCloudflareZoneResponse {
    
    /**
     * Zone ID
     */
    @JsonProperty("zone_id")
    private String zoneId;
    
    /**
     * 域名名称
     */
    private String name;
    
    /**
     * 域名状态
     */
    private String status;
    
    /**
     * 是否暂停
     */
    private Boolean paused;
    
    /**
     * Zone类型
     */
    private String type;
    
    /**
     * 名称服务器列表
     */
    @JsonProperty("name_servers")
    private List<String> nameServers;
    
    /**
     * Cloudflare创建时间
     */
    @JsonProperty("created_on")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    
    /**
     * Cloudflare修改时间
     */
    @JsonProperty("modified_on")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedOn;
    
    /**
     * DNS记录总数量
     */
    @JsonProperty("dns_record_count")
    private Integer dnsRecordCount;
    
    /**
     * DNS记录数量限制
     */
    @JsonProperty("dns_record_limit")
    private Integer dnsRecordLimit;
    
    /**
     * 已注册用户数量
     */
    @JsonProperty("user_count")
    private Integer userCount;
    
    /**
     * 用户数量限制
     */
    @JsonProperty("user_limit")
    private Integer userLimit;
    
    /**
     * 是否已满
     */
    @JsonProperty("is_full")
    private Boolean isFull;
    
    /**
     * 是否启用自动分配前缀
     */
    @JsonProperty("auto_assign_enabled")
    private Boolean autoAssignEnabled;
    
    /**
     * 下一个可分配的十六进制前缀
     */
    @JsonProperty("next_prefix_hex")
    private String nextPrefixHex;
    
    /**
     * 本地启用状态
     */
    @JsonProperty("is_active")
    private Boolean isActive;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 本地创建时间
     */
    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 本地更新时间
     */
    @JsonProperty("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}