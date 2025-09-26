package com.example.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.regex.Pattern;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;

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
    
    /**
     * 验证DNS记录内容格式
     * @return 验证结果，null表示验证通过，否则返回错误信息
     */
    public String validateContent() {
        if (content == null || content.trim().isEmpty()) {
            return "记录内容不能为空";
        }
        
        String trimmedContent = content.trim();
        
        switch (type.toUpperCase()) {
            case "A":
                return validateIPv4Address(trimmedContent);
            case "AAAA":
                return validateIPv6Address(trimmedContent);
            case "CNAME":
            case "MX":
            case "NS":
                return validateDomainName(trimmedContent);
            case "TXT":
                return validateTxtRecord(trimmedContent);
            case "SRV":
                return validateSrvContent(trimmedContent);
            default:
                return null; // 其他类型暂不验证
        }
    }
    
    /**
     * 验证IPv4地址格式
     */
    private String validateIPv4Address(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            if (!(addr instanceof Inet4Address)) {
                return "IPv4地址格式不正确";
            }
            return null;
        } catch (Exception e) {
            return "IPv4地址格式不正确";
        }
    }
    
    /**
     * 验证IPv6地址格式
     */
    private String validateIPv6Address(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            if (!(addr instanceof Inet6Address)) {
                return "IPv6地址格式不正确";
            }
            return null;
        } catch (Exception e) {
            return "IPv6地址格式不正确";
        }
    }
    
    /**
     * 验证域名格式
     */
    private String validateDomainName(String domain) {
        // 域名正则表达式
        String domainRegex = "^(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+" +
                           "[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?$";
        
        if (!Pattern.matches(domainRegex, domain)) {
            return "域名格式不正确";
        }
        
        if (domain.length() > 253) {
            return "域名长度不能超过253个字符";
        }
        
        return null;
    }
    
    /**
     * 验证TXT记录内容
     */
    private String validateTxtRecord(String txt) {
        if (txt.length() > 255) {
            return "TXT记录内容长度不能超过255个字符";
        }
        return null;
    }
    
    /**
     * 验证SRV记录内容格式
     */
    private String validateSrvContent(String srv) {
        // SRV记录格式：priority weight port target
        String[] parts = srv.split("\\s+");
        if (parts.length != 4) {
            return "SRV记录格式不正确，应为：priority weight port target";
        }
        
        try {
            Integer.parseInt(parts[0]); // priority
            Integer.parseInt(parts[1]); // weight
            Integer.parseInt(parts[2]); // port
            // parts[3] 是target域名，可以进一步验证
            return validateDomainName(parts[3]);
        } catch (NumberFormatException e) {
            return "SRV记录中的数字格式不正确";
        }
    }
}