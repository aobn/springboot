package com.example.demo.dto;

/**
 * 文件名：VerificationCodeRequest.java
 * 功能：验证码请求数据传输对象
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public class VerificationCodeRequest {
    
    private String email;
    
    public VerificationCodeRequest() {
    }
    
    public VerificationCodeRequest(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "VerificationCodeRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}