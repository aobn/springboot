package com.example.demo.entity;

import java.time.LocalDateTime;

/**
 * 用户3级域名实体类
 * 
 * @author CodeBuddy
 * @since 2025-08-24
 */
public class UserSubdomain {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 子域名前缀
     */
    private String subdomain;
    
    /**
     * 主域名
     */
    private String domain;
    
    /**
     * 完整域名
     */
    private String fullDomain;
    
    /**
     * 状态：ACTIVE-活跃，INACTIVE-不活跃，DELETED-已删除
     */
    private String status;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 构造函数
    public UserSubdomain() {}
    
    public UserSubdomain(Long userId, String subdomain, String domain, String fullDomain) {
        this.userId = userId;
        this.subdomain = subdomain;
        this.domain = domain;
        this.fullDomain = fullDomain;
        this.status = "ACTIVE";
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
    
    public String getSubdomain() {
        return subdomain;
    }
    
    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String getFullDomain() {
        return fullDomain;
    }
    
    public void setFullDomain(String fullDomain) {
        this.fullDomain = fullDomain;
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
        return "UserSubdomain{" +
                "id=" + id +
                ", userId=" + userId +
                ", subdomain='" + subdomain + '\'' +
                ", domain='" + domain + '\'' +
                ", fullDomain='" + fullDomain + '\'' +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}