package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户DNS解析记录请求DTO
 * 用于接收用户添加DNS解析记录的请求参数
 * 
 * @author CodeBuddy
 * @since 2025-08-25
 */
public class UserDnsRecordRequest {
    
    /**
     * 用户3级域名ID
     */
    @NotNull(message = "子域名ID不能为空")
    private Long subdomainId;
    
    /**
     * 主机记录（如www、mail等，@表示主域名）
     */
    @NotBlank(message = "主机记录不能为空")
    @Size(max = 100, message = "主机记录长度不能超过100个字符")
    private String name;
    
    /**
     * 记录类型（A、CNAME、MX、TXT等）
     */
    @NotBlank(message = "记录类型不能为空")
    @Pattern(regexp = "^(A|AAAA|CNAME|MX|TXT|NS|SRV|PTR)$", 
             message = "记录类型只能是：A、AAAA、CNAME、MX、TXT、NS、SRV、PTR")
    private String type;
    
    /**
     * 记录值（如IP地址、域名等）
     */
    @NotBlank(message = "记录值不能为空")
    @Size(max = 500, message = "记录值长度不能超过500个字符")
    private String value;
    
    /**
     * 记录线路（可选，默认为"默认"）
     */
    private String line = "默认";
    
    /**
     * TTL值（生存时间，可选，默认为600）
     */
    @Min(value = 1, message = "TTL值不能小于1")
    @Max(value = 604800, message = "TTL值不能大于604800")
    private Integer ttl = 600;
    
    /**
     * MX优先级（仅MX记录需要）
     */
    @Min(value = 1, message = "MX优先级不能小于1")
    @Max(value = 20, message = "MX优先级不能大于20")
    private Integer mx;
    
    /**
     * 权重（负载均衡，可选）
     */
    @Min(value = 0, message = "权重不能小于0")
    @Max(value = 100, message = "权重不能大于100")
    private Integer weight;
    
    /**
     * 备注信息（可选）
     */
    private String remark;
    
    // 构造函数
    public UserDnsRecordRequest() {}
    
    // Getter和Setter方法
    public Long getSubdomainId() {
        return subdomainId;
    }
    
    public void setSubdomainId(Long subdomainId) {
        this.subdomainId = subdomainId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getLine() {
        return line;
    }
    
    public void setLine(String line) {
        this.line = line;
    }
    
    public Integer getTtl() {
        return ttl;
    }
    
    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }
    
    public Integer getMx() {
        return mx;
    }
    
    public void setMx(Integer mx) {
        this.mx = mx;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    @Override
    public String toString() {
        return "UserDnsRecordRequest{" +
                "subdomainId=" + subdomainId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", line='" + line + '\'' +
                ", ttl=" + ttl +
                ", mx=" + mx +
                ", weight=" + weight +
                ", remark='" + remark + '\'' +
                '}';
    }
}