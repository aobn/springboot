package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

/**
 * 用户DNS解析记录更新请求DTO
 * 用于接收用户修改DNS解析记录的请求参数
 * 
 * @author CodeBuddy
 * @since 2025-08-25
 */
public class UserDnsRecordUpdateRequest {
    
    /**
     * 要修改的DNS解析记录ID
     */
    @NotNull(message = "记录ID不能为空")
    private Long id;
    
    /**
     * 记录类型（A、CNAME、MX、TXT等）
     */
    private String type;
    
    /**
     * 记录值（如IP地址、域名等）
     */
    private String value;
    
    /**
     * 记录线路（可选，默认为"默认"）
     */
    private String line;
    
    /**
     * TTL值（生存时间，可选，默认为600）
     */
    @Min(value = 1, message = "TTL值不能小于1")
    @Max(value = 604800, message = "TTL值不能大于604800")
    private Integer ttl;
    
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
     * 记录状态（ENABLE或DISABLE）
     */
    private String status;
    
    /**
     * 备注信息（可选）
     */
    private String remark;
    
    // 构造函数
    public UserDnsRecordUpdateRequest() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    @Override
    public String toString() {
        return "UserDnsRecordUpdateRequest{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", line='" + line + '\'' +
                ", ttl=" + ttl +
                ", mx=" + mx +
                ", weight=" + weight +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}