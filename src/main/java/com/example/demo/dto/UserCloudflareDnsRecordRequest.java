package com.example.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 文件名：UserCloudflareDnsRecordRequest.java
 * 功能：用户Cloudflare DNS记录请求DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Data
public class UserCloudflareDnsRecordRequest {
    
    /**
     * 用户域名ID（user_cloudflare_domain表的ID）
     */
    @NotNull(message = "用户域名ID不能为空")
    private Long userDomainId;
    
    /**
     * DNS记录名称前缀（用户输入部分）
     * 例如：用户输入"www"，实际记录名称为"www.0.1.example.com"
     * 如果输入"@"，则表示根域名"0.1.example.com"
     */
    @NotBlank(message = "记录名称前缀不能为空")
    private String namePrefix;
    
    /**
     * DNS记录类型
     * 支持：A、AAAA、CNAME、MX、TXT、NS、SRV等
     */
    @NotBlank(message = "记录类型不能为空")
    private String type;
    
    /**
     * DNS记录内容
     */
    @NotBlank(message = "记录内容不能为空")
    private String content;
    
    /**
     * TTL值（生存时间）
     * 1表示自动，其他值为具体秒数
     */
    @NotNull(message = "TTL不能为空")
    @Min(value = 1, message = "TTL最小值为1")
    @Max(value = 2147483647, message = "TTL值过大")
    private Integer ttl = 1;
    
    /**
     * 是否启用Cloudflare代理
     * 仅对A、AAAA、CNAME记录有效
     */
    private Boolean proxied = false;
    
    /**
     * 优先级（MX、SRV记录使用）
     */
    private Integer priority;
    
    /**
     * 权重（SRV记录使用）
     */
    private Integer weight;
    
    /**
     * 端口（SRV记录使用）
     */
    private Integer port;
    
    /**
     * 服务名（SRV记录使用）
     */
    private String service;
    
    /**
     * 协议（SRV记录使用）
     */
    private String proto;
    
    /**
     * 目标（SRV记录使用）
     */
    private String target;
    
    /**
     * 记录注释
     */
    private String comment;
    
    /**
     * 获取完整的DNS记录名称
     * @param userSubdomain 用户子域名（如0.1.example.com）
     * @return 完整的DNS记录名称
     */
    public String getFullName(String userSubdomain) {
        if (namePrefix == null || namePrefix.trim().isEmpty()) {
            return userSubdomain;
        }
        
        // 如果用户输入的是@，表示根域名
        if ("@".equals(namePrefix.trim())) {
            return userSubdomain;
        }
        
        // 如果用户输入已经包含域名，直接返回
        if (namePrefix.contains(".") && namePrefix.endsWith(userSubdomain)) {
            return namePrefix;
        }
        
        // 拼接前缀和用户子域名
        return namePrefix + "." + userSubdomain;
    }
    
    /**
     * 验证SRV记录必需字段
     * @return 是否验证通过
     */
    public boolean validateSrvRecord() {
        if (!"SRV".equalsIgnoreCase(type)) {
            return true;
        }
        
        return priority != null && weight != null && port != null 
               && service != null && proto != null && target != null;
    }
    
    /**
     * 验证MX记录必需字段
     * @return 是否验证通过
     */
    public boolean validateMxRecord() {
        if (!"MX".equalsIgnoreCase(type)) {
            return true;
        }
        
        return priority != null;
    }
}