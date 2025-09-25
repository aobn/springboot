package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cloudflare Zone API响应DTO
 * 
 * @author CodeBuddy
 * @since 2025-09-25
 */
@Data
public class CloudflareZoneResponse {
    
    private List<Zone> result;
    
    @JsonProperty("result_info")
    private ResultInfo resultInfo;
    
    private boolean success;
    private List<Error> errors;
    private List<Message> messages;
    
    /**
     * Zone域名信息
     */
    @Data
    public static class Zone {
        private String id;
        private String name;
        private String status;
        private boolean paused;
        private String type;
        
        @JsonProperty("development_mode")
        private Integer developmentMode;
        
        @JsonProperty("name_servers")
        private List<String> nameServers;
        
        @JsonProperty("original_name_servers")
        private List<String> originalNameServers;
        
        @JsonProperty("original_registrar")
        private String originalRegistrar;
        
        @JsonProperty("original_dnshost")
        private String originalDnshost;
        
        @JsonProperty("modified_on")
        private String modifiedOn;
        
        @JsonProperty("created_on")
        private String createdOn;
        
        @JsonProperty("activated_on")
        private String activatedOn;
        
        @JsonProperty("vanity_name_servers")
        private List<String> vanityNameServers;
        
        @JsonProperty("vanity_name_servers_ips")
        private Object vanityNameServersIps;
        
        private Meta meta;
        private Owner owner;
        private Account account;
        private Tenant tenant;
        
        @JsonProperty("tenant_unit")
        private TenantUnit tenantUnit;
        
        private List<String> permissions;
        private Plan plan;
    }
    
    /**
     * 元数据信息
     */
    @Data
    public static class Meta {
        private Integer step;
        
        @JsonProperty("custom_certificate_quota")
        private Integer customCertificateQuota;
        
        @JsonProperty("page_rule_quota")
        private Integer pageRuleQuota;
        
        @JsonProperty("phishing_detected")
        private Boolean phishingDetected;
    }
    
    /**
     * 所有者信息
     */
    @Data
    public static class Owner {
        private String id;
        private String type;
        private String email;
    }
    
    /**
     * 账户信息
     */
    @Data
    public static class Account {
        private String id;
        private String name;
    }
    
    /**
     * 租户信息
     */
    @Data
    public static class Tenant {
        private String id;
        private String name;
    }
    
    /**
     * 租户单元信息
     */
    @Data
    public static class TenantUnit {
        private String id;
    }
    
    /**
     * 计划信息
     */
    @Data
    public static class Plan {
        private String id;
        private String name;
        private Double price;
        private String currency;
        private String frequency;
        
        @JsonProperty("is_subscribed")
        private Boolean isSubscribed;
        
        @JsonProperty("can_subscribe")
        private Boolean canSubscribe;
        
        @JsonProperty("legacy_id")
        private String legacyId;
        
        @JsonProperty("legacy_discount")
        private Boolean legacyDiscount;
        
        @JsonProperty("externally_managed")
        private Boolean externallyManaged;
    }
    
    /**
     * 分页信息
     */
    @Data
    public static class ResultInfo {
        private Integer page;
        
        @JsonProperty("per_page")
        private Integer perPage;
        
        @JsonProperty("total_pages")
        private Integer totalPages;
        
        private Integer count;
        
        @JsonProperty("total_count")
        private Integer totalCount;
    }
    
    /**
     * 错误信息
     */
    @Data
    public static class Error {
        private Integer code;
        private String message;
        
        @JsonProperty("documentation_url")
        private String documentationUrl;
        
        private Source source;
    }
    
    /**
     * 消息信息
     */
    @Data
    public static class Message {
        private Integer code;
        private String message;
        
        @JsonProperty("documentation_url")
        private String documentationUrl;
        
        private Source source;
    }
    
    /**
     * 源信息
     */
    @Data
    public static class Source {
        private String pointer;
    }
}