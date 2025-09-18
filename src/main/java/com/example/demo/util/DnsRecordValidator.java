package com.example.demo.util;

import java.util.regex.Pattern;

/**
 * DNS记录参数校验工具类
 * 提供各种DNS记录类型的参数格式校验
 * 
 * @author CodeBuddy
 * @since 2025-08-30
 */
public class DnsRecordValidator {
    
    // IPv4地址正则表达式
    private static final Pattern IPV4_PATTERN = Pattern.compile("^[0-9.]+$");
    
    // 域名正则表达式
    private static final Pattern DOMAIN_LABEL_PATTERN = Pattern.compile("^[a-zA-Z0-9-]+$");
    
    /**
     * 验证DNS记录值格式
     * 
     * @param type 记录类型
     * @param value 记录值
     * @return 校验结果
     */
    public static ValidationResult validateRecordValue(String type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error("记录值不能为空");
        }
        
        value = value.trim();
        
        switch (type.toUpperCase()) {
            case "A":
                return validateIPv4(value);
            case "AAAA":
                return validateIPv6(value);
            case "CNAME":
            case "MX":
            case "NS":
                return validateDomain(value);
            case "TXT":
                return validateTXT(value);
            case "SRV":
                return validateSRV(value);
            case "PTR":
                return validateDomain(value);
            default:
                return ValidationResult.success();
        }
    }
    
    /**
     * 验证IPv4地址格式
     * 严格验证，拒绝包含字母的格式如 "a.1.1.1"
     * 
     * @param ip IP地址字符串
     * @return 校验结果
     */
    public static ValidationResult validateIPv4(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return ValidationResult.error("IPv4地址不能为空");
        }
        
        ip = ip.trim();
        
        // 严格检查：只允许数字和点
        if (!IPV4_PATTERN.matcher(ip).matches()) {
            return ValidationResult.error("IPv4地址只能包含数字和点，不能包含字母或其他字符。正确格式如：192.168.1.1，当前值：" + ip);
        }
        
        // 检查是否以点开头或结尾
        if (ip.startsWith(".") || ip.endsWith(".")) {
            return ValidationResult.error("IPv4地址不能以点开头或结尾。正确格式如：192.168.1.1，当前值：" + ip);
        }
        
        // 检查是否包含连续的点
        if (ip.contains("..")) {
            return ValidationResult.error("IPv4地址不能包含连续的点。正确格式如：192.168.1.1，当前值：" + ip);
        }
        
        // 检查是否包含空格
        if (ip.contains(" ")) {
            return ValidationResult.error("IPv4地址不能包含空格。正确格式如：192.168.1.1，当前值：" + ip);
        }
        
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return ValidationResult.error("IPv4地址必须包含4个数字段，用点分隔。正确格式如：192.168.1.1，当前值：" + ip);
        }
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            // 检查每个部分是否为空
            if (part.isEmpty()) {
                return ValidationResult.error("IPv4地址的第" + (i + 1) + "段不能为空。正确格式如：192.168.1.1，当前值：" + ip);
            }
            
            // 检查是否包含非数字字符
            if (!part.matches("^[0-9]+$")) {
                return ValidationResult.error("IPv4地址的第" + (i + 1) + "段只能包含数字。正确格式如：192.168.1.1，当前值：" + ip);
            }
            
            // 检查是否有前导零（除了单独的0）
            if (part.length() > 1 && part.startsWith("0")) {
                return ValidationResult.error("IPv4地址的第" + (i + 1) + "段不能有前导零。正确格式如：192.168.1.1，当前值：" + ip);
            }
            
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return ValidationResult.error("IPv4地址的第" + (i + 1) + "段必须在0-255范围内。正确格式如：192.168.1.1，当前值：" + ip);
                }
            } catch (NumberFormatException e) {
                return ValidationResult.error("IPv4地址的第" + (i + 1) + "段不是有效数字。正确格式如：192.168.1.1，当前值：" + ip);
            }
        }
        
        return ValidationResult.success();
    }
    
    /**
     * 验证IPv6地址格式（简单验证）
     * 
     * @param ip IPv6地址字符串
     * @return 校验结果
     */
    public static ValidationResult validateIPv6(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return ValidationResult.error("IPv6地址不能为空");
        }
        
        ip = ip.trim();
        
        // 简单的IPv6格式验证
        if (ip.matches("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$") ||
            ip.matches("^::1$") || ip.matches("^::$") ||
            ip.matches("^([0-9a-fA-F]{1,4}:)*::([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4}$")) {
            return ValidationResult.success();
        }
        
        return ValidationResult.error("IPv6地址格式不正确。正确格式如：2001:db8::1，当前值：" + ip);
    }
    
    /**
     * 验证域名格式
     * 严格验证域名格式，确保符合DNS规范
     * 
     * @param domain 域名字符串
     * @return 校验结果
     */
    public static ValidationResult validateDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return ValidationResult.error("域名不能为空");
        }
        
        domain = domain.trim();
        
        // 域名长度不能超过253个字符
        if (domain.length() > 253) {
            return ValidationResult.error("域名长度不能超过253个字符，当前长度：" + domain.length());
        }
        
        // 域名不能以点开头或结尾
        if (domain.startsWith(".") || domain.endsWith(".")) {
            return ValidationResult.error("域名不能以点开头或结尾。正确格式如：example.com，当前值：" + domain);
        }
        
        // 检查是否包含连续的点
        if (domain.contains("..")) {
            return ValidationResult.error("域名不能包含连续的点。正确格式如：example.com，当前值：" + domain);
        }
        
        // 分割域名各部分进行验证
        String[] labels = domain.split("\\.");
        
        // 至少要有一个标签
        if (labels.length == 0) {
            return ValidationResult.error("域名格式不正确。正确格式如：example.com，当前值：" + domain);
        }
        
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i];
            
            // 每个标签不能为空
            if (label.isEmpty()) {
                return ValidationResult.error("域名的第" + (i + 1) + "段不能为空。正确格式如：example.com，当前值：" + domain);
            }
            
            // 每个标签长度不能超过63个字符
            if (label.length() > 63) {
                return ValidationResult.error("域名的第" + (i + 1) + "段长度不能超过63个字符。正确格式如：example.com，当前值：" + domain);
            }
            
            // 标签不能以连字符开头或结尾
            if (label.startsWith("-") || label.endsWith("-")) {
                return ValidationResult.error("域名的第" + (i + 1) + "段不能以连字符开头或结尾。正确格式如：example.com，当前值：" + domain);
            }
            
            // 标签只能包含字母、数字和连字符
            if (!DOMAIN_LABEL_PATTERN.matcher(label).matches()) {
                return ValidationResult.error("域名的第" + (i + 1) + "段只能包含字母、数字和连字符。正确格式如：example.com，当前值：" + domain);
            }
        }
        
        // 顶级域名必须至少包含一个字母
        String tld = labels[labels.length - 1];
        if (!tld.matches(".*[a-zA-Z].*")) {
            return ValidationResult.error("顶级域名必须至少包含一个字母。正确格式如：example.com，当前值：" + domain);
        }
        
        return ValidationResult.success();
    }
    
    /**
     * 验证TXT记录格式
     * 
     * @param txt TXT记录值
     * @return 校验结果
     */
    public static ValidationResult validateTXT(String txt) {
        if (txt == null) {
            return ValidationResult.error("TXT记录值不能为空");
        }
        
        if (txt.length() > 600) {
            return ValidationResult.error("TXT记录值长度不能超过600个字符，当前长度：" + txt.length());
        }
        
        return ValidationResult.success();
    }
    
    /**
     * 验证SRV记录格式
     * SRV记录格式：优先级 权重 端口 目标主机
     * 例如：10 5 443 target.example.com.
     * 
     * @param srv SRV记录值
     * @return 校验结果
     */
    public static ValidationResult validateSRV(String srv) {
        if (srv == null || srv.trim().isEmpty()) {
            return ValidationResult.error("SRV记录值不能为空");
        }
        
        srv = srv.trim();
        
        // 分割SRV记录的各个部分
        String[] parts = srv.split("\\s+");
        
        if (parts.length != 4) {
            return ValidationResult.error("SRV记录格式不正确。正确格式：优先级 权重 端口 目标主机，例如：10 5 443 target.example.com，当前值：" + srv);
        }
        
        // 验证优先级（0-65535）
        try {
            int priority = Integer.parseInt(parts[0]);
            if (priority < 0 || priority > 65535) {
                return ValidationResult.error("SRV记录优先级必须在0-65535范围内，当前值：" + parts[0]);
            }
        } catch (NumberFormatException e) {
            return ValidationResult.error("SRV记录优先级必须是数字，当前值：" + parts[0]);
        }
        
        // 验证权重（0-65535）
        try {
            int weight = Integer.parseInt(parts[1]);
            if (weight < 0 || weight > 65535) {
                return ValidationResult.error("SRV记录权重必须在0-65535范围内，当前值：" + parts[1]);
            }
        } catch (NumberFormatException e) {
            return ValidationResult.error("SRV记录权重必须是数字，当前值：" + parts[1]);
        }
        
        // 验证端口（1-65535）
        try {
            int port = Integer.parseInt(parts[2]);
            if (port < 1 || port > 65535) {
                return ValidationResult.error("SRV记录端口必须在1-65535范围内，当前值：" + parts[2]);
            }
        } catch (NumberFormatException e) {
            return ValidationResult.error("SRV记录端口必须是数字，当前值：" + parts[2]);
        }
        
        // 验证目标主机（域名格式或"."）
        String target = parts[3];
        if (!target.equals(".")) {
            // 如果不是"."，则验证域名格式
            ValidationResult domainResult = validateDomain(target);
            if (!domainResult.isValid()) {
                return ValidationResult.error("SRV记录目标主机格式不正确：" + domainResult.getErrorMessage());
            }
        }
        
        return ValidationResult.success();
    }
    
    /**
     * 校验结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}