package com.example.demo.dto;

/**
 * 文件名：UserRegisterVerifyRequest.java
 * 功能：用户注册验证请求数据传输对象
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public class UserRegisterVerifyRequest {
    
    private String email;
    private String password;
    private String code;
    
    public UserRegisterVerifyRequest() {
    }
    
    public UserRegisterVerifyRequest(String email, String password, String code) {
        this.email = email;
        this.password = password;
        this.code = code;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    @Override
    public String toString() {
        return "UserRegisterVerifyRequest{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", code='" + code + '\'' +
                '}';
    }
}