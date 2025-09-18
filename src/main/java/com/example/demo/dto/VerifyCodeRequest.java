package com.example.demo.dto;

/**
 * 文件名：VerifyCodeRequest.java
 * 功能：验证码验证请求数据传输对象
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public class VerifyCodeRequest {
    
    private String email;
    private String code;
    
    public VerifyCodeRequest() {
    }
    
    public VerifyCodeRequest(String email, String code) {
        this.email = email;
        this.code = code;
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
    
    @Override
    public String toString() {
        return "VerifyCodeRequest{" +
                "email='" + email + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}