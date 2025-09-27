package com.example.demo.dto;

import com.example.demo.util.DnsRecordValidator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文件名：UserCloudflareDnsRecordUpdateRequest.java
 * 功能：用户更新Cloudflare DNS记录请求DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-27
 * 版本：v1.0.0
 */
@Data
public class UserCloudflareDnsRecordUpdateRequest {
    
    /**
     * DNS记录内容（IP地址、域名、文本等）
     */
    @NotBlank(message = "DNS记录内容不能为空")
    private String content;
    
    /**
     * TTL值（1表示自动，其他为具体秒数）
     */
    @NotNull(message = "TTL值不能为空")
    @Min(value = 1, message = "TTL值最小为1")
    @Max(value = 86400, message = "TTL值最大为86400秒（24小时）")
    private Integer ttl = 1;
    
    /**
     * 是否启用Cloudflare代理（仅A、AAAA、CNAME记录支持）
     */
    private Boolean proxied = false;
    
    /**
     * 优先级（MX记录和SRV记录必填）
     */
    @Min(value = 0, message = "优先级不能小于0")
    @Max(value = 65535, message = "优先级不能大于65535")
    private Integer priority;
    
    /**
     * 权重（SRV记录必填）
     */
    @Min(value = 0, message = "权重不能小于0")
    @Max(value = 65535, message = "权重不能大于65535")
    private Integer weight;
    
    /**
     * 端口（SRV记录必填）
     */
    @Min(value = 1, message = "端口不能小于1")
    @Max(value = 65535, message = "端口不能大于65535")
    private Integer port;
    
    /**
     * 服务名（SRV记录必填，如_http、_https）
     */
    private String service;
    
    /**
     * 协议（SRV记录必填，如_tcp、_udp）
     */
    private String proto;
    
    /**
     * 目标（SRV记录必填）
     */
    private String target;
    
    /**
     * 记录注释
     */
    private String comment;
    
    /**
     * 验证DNS记录内容格式
     * @param type DNS记录类型
     * @return 验证错误信息，null表示验证通过
     */
    public String validateContent(String type) {
        DnsRecordValidator.ValidationResult result = DnsRecordValidator.validateRecordValue(type, content);
        return result.isValid() ? null : result.getErrorMessage();
    }
    
    /**
     * 验证SRV记录的必需字段
     * @return 是否验证通过
     */
    public boolean validateSrvRecord() {
        return priority != null && weight != null && port != null 
            && service != null && !service.trim().isEmpty()
            && proto != null && !proto.trim().isEmpty()
            && target != null && !target.trim().isEmpty();
    }
    
    /**
     * 验证MX记录的必需字段
     * @return 是否验证通过
     */
    public boolean validateMxRecord() {
        return priority != null;
    }
    
    /**
     * 检查是否支持代理功能
     * @param type DNS记录类型
     * @return 是否支持代理
     */
    public boolean isProxySupported(String type) {
        return "A".equalsIgnoreCase(type) || 
               "AAAA".equalsIgnoreCase(type) || 
               "CNAME".equalsIgnoreCase(type);
    }
}