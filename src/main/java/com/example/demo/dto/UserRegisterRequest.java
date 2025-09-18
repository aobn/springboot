package com.example.demo.dto;

/**
 * 文件名：UserRegisterRequest.java
 * 功能：用户注册请求数据传输对象
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public class UserRegisterRequest {
    
    private String email;
    private String password;
    
    public UserRegisterRequest() {
    }
    
    public UserRegisterRequest(String email, String password) {
        this.email = email;
        this.password = password;
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
    
    @Override
    public String toString() {
        return "UserRegisterRequest{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}