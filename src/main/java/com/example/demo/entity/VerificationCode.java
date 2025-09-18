package com.example.demo.entity;

import java.time.LocalDateTime;

/**
 * 文件名：VerificationCode.java
 * 功能：验证码实体类，用于存储邮箱验证码信息
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public class VerificationCode {
    
    private Long id;
    private String email;
    private String code;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private Boolean used;
    
    public VerificationCode() {
    }
    
    public VerificationCode(String email, String code, LocalDateTime createTime, LocalDateTime expireTime) {
        this.email = email;
        this.code = code;
        this.createTime = createTime;
        this.expireTime = expireTime;
        this.used = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    
    public Boolean getUsed() {
        return used;
    }
    
    public void setUsed(Boolean used) {
        this.used = used;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
    
    @Override
    public String toString() {
        return "VerificationCode{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", createTime=" + createTime +
                ", expireTime=" + expireTime +
                ", used=" + used +
                '}';
    }
}