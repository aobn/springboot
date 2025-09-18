package com.example.demo.entity;

import java.time.LocalDateTime;

/**
 * 用户DNS解析记录实体类
 * 用于存储用户的DNS解析记录信息
 * 
 * @author CodeBuddy
 * @since 2025-08-25
 */
public class UserDnsRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户3级域名ID
     */
    private Long subdomainId;
    
    /**
     * DNSPod记录ID
     */
    private Long recordId;
    
    /**
     * 主机记录（如www、mail等，@表示主域名）
     */
    private String name;
    
    /**
     * 记录类型（A、CNAME、MX、TXT等）
     */
    private String type;
    
    /**
     * 记录值（如IP地址、域名等）
     */
    private String value;
    
    /**
     * 记录线路
     */
    private String line;
    
    /**
     * 线路ID
     */
    private String lineId;
    
    /**
     * TTL值（生存时间）
     */
    private Integer ttl;
    
    /**
     * MX优先级（仅MX记录需要）
     */
    private Integer mx;
    
    /**
     * 权重（负载均衡）
     */
    private Integer weight;
    
    /**
     * 记录状态（ENABLE或DISABLE）
     */
    private String status;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 监控状态
     */
    private String monitorStatus;
    
    /**
     * DNSPod更新时间
     */
    private LocalDateTime updatedOn;
    
    /**
     * 同步状态（PENDING-待同步，SUCCESS-同步成功，FAILED-同步失败）
     */
    private String syncStatus;
    
    /**
     * 同步错误信息
     */
    private String syncError;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 构造函数
    public UserDnsRecord() {}
    
    public UserDnsRecord(Long userId, Long subdomainId, String name, String type, String value) {
        this.userId = userId;
        this.subdomainId = subdomainId;
        this.name = name;
        this.type = type;
        this.value = value;
        this.line = "默认";
        this.lineId = "0";
        this.ttl = 600;
        this.status = "ENABLE";
        this.syncStatus = "PENDING";
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getSubdomainId() {
        return subdomainId;
    }
    
    public void setSubdomainId(Long subdomainId) {
        this.subdomainId = subdomainId;
    }
    
    public Long getRecordId() {
        return recordId;
    }
    
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
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
    
    public String getLineId() {
        return lineId;
    }
    
    public void setLineId(String lineId) {
        this.lineId = lineId;
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
    
    public String getMonitorStatus() {
        return monitorStatus;
    }
    
    public void setMonitorStatus(String monitorStatus) {
        this.monitorStatus = monitorStatus;
    }
    
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }
    
    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }
    
    public String getSyncStatus() {
        return syncStatus;
    }
    
    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
    
    public String getSyncError() {
        return syncError;
    }
    
    public void setSyncError(String syncError) {
        this.syncError = syncError;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    @Override
    public String toString() {
        return "UserDnsRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", subdomainId=" + subdomainId +
                ", recordId=" + recordId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", line='" + line + '\'' +
                ", ttl=" + ttl +
                ", status='" + status + '\'' +
                ", syncStatus='" + syncStatus + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}